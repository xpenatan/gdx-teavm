package com.github.xpenatan.gdx.backends.dragome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import com.dragome.commons.DragomeConfigurator;
import com.dragome.commons.compiler.BytecodeToJavascriptCompiler;
import com.dragome.commons.compiler.BytecodeToJavascriptCompilerConfiguration;
import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.CopyUtils;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;
import com.dragome.commons.compiler.classpath.serverside.JarClasspathEntry;
import com.dragome.compiler.utils.Log;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.services.serverside.ServerReflectionServiceImpl;
import com.dragome.view.VisualActivity;
import com.dragome.web.helpers.DefaultClasspathFileFilter;
import proguard.Configuration;
import proguard.ConfigurationParser;
import proguard.ParseException;
import proguard.ProGuard;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

public class DragomeStandaloneAppGenerator
{
	private static Logger LOGGER= Logger.getLogger(DragomeStandaloneAppGenerator.class.getName());

	public static void compileWithMainClass(Classpath classPath, String target, String mainClass)
	{
		Log logger2 = Log.getLogger();
//		logger2.setState(Log.DEBUG);

		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		DragomeConfigurator configurator= serviceLocator.getConfigurator();

		String mainClassName= mainClass;
		CompilerType defaultCompilerType= configurator.getDefaultCompilerType();
		BytecodeTransformer bytecodeTransformer= configurator.getBytecodeTransformer();

		ClasspathFileFilter classpathFilter= configurator.getClasspathFilter();
		if (classpathFilter == null)
			classpathFilter= new DefaultClasspathFileFilter();

		BytecodeToJavascriptCompiler bytecodeToJavascriptCompiler= WebServiceLocator.getInstance().getBytecodeToJavascriptCompiler();

		List<ClasspathEntry> extraClasspath= configurator.getExtraClasspath(classPath);
		classPath.addEntries(extraClasspath);
		configurator.sortClassPath(classPath);
		classPath= process(classPath, configurator);

		BytecodeToJavascriptCompilerConfiguration compilerConfiguration= new BytecodeToJavascriptCompilerConfiguration(classPath, target, mainClassName, defaultCompilerType, bytecodeTransformer, new DefaultClasspathFileFilter(), configurator.isCheckingCast(), configurator.isCaching(), configurator.isFailOnError());
		bytecodeToJavascriptCompiler.configure(compilerConfiguration);
		bytecodeToJavascriptCompiler.compile();
	}

	private static Classpath process(Classpath classPath, DragomeConfigurator configurator)
	{
		try
		{
			String path= null;

			String tempDir= System.getProperty("java.io.tmpdir");
			File tmpDir= new File(tempDir + File.separatorChar + "dragomeTemp");
			Path tmpPath= tmpDir.toPath();
			FileUtils.deleteDirectory(tmpDir);
			Files.createDirectories(tmpPath);
			File file= Files.createTempFile(tmpPath, "dragome-merged-", ".jar").toFile();
			file.deleteOnExit();

			try (JarOutputStream jos= new JarOutputStream(new FileOutputStream(file)))
			{
				final ArrayList<String> keepClass= new ArrayList<>();
				final ClasspathFileFilter classpathFilter= configurator.getClasspathFilter();
				List<ClasspathEntry> entries= classPath.getEntries();
				for (ClasspathEntry classpathEntry : entries)
				{
					classpathEntry.copyFilesToJar(jos, new DefaultClasspathFileFilter()
					{
						@Override
						public boolean accept(ClasspathFile classpathFile)
						{
							boolean result= super.accept(classpathFile);

							String entryName= classpathFile.getPath();
							entryName= entryName.replace("\\", "/");

							if (!keepClass.contains(entryName))
							{
								keepClass.add(entryName);

								if (entryName.endsWith(".js") || entryName.endsWith(".class") || entryName.contains("MANIFEST") || entryName.contains(".html") || entryName.contains(".css"))
									result&= true;

								if (classpathFilter != null)
									result&= classpathFilter.accept(classpathFile);
							}
							else
								result= false;
							return result;
						}
					});
				}
			}
			if (configurator.isRemoveUnusedCode())
			{
				file= executeProguard(file, "/proguard.conf", "-proguard.jar", configurator.getAdditionalCodeKeepConfigFile(), false);
				file.deleteOnExit();
			}
			if (configurator.isObfuscateCode())
			{
				file= executeProguard(file, "/proguardObf.conf", "-Obf.jar", configurator.getAdditionalObfuscateCodeKeepConfigFile(), true);
				file.deleteOnExit();
			}
			path= file.getAbsolutePath();
			return new Classpath(JarClasspathEntry.createFromPath(path));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static File executeProguard(File inputFile, String name, String replacement, List<URL> additionalUrls, boolean addToClasspath) throws MalformedURLException, URISyntaxException, IOException, ParseException
	{
		String outFilename= inputFile.getAbsolutePath().replace(".jar", replacement);
		File outputFile= new File(outFilename);

		Properties properties= System.getProperties();
		properties.put("in-jar-filename", inputFile.getAbsolutePath());
		properties.put("out-jar-filename", outputFile.getAbsolutePath());

		ConfigurationParser parser= new ConfigurationParser(DragomeStandaloneAppGenerator.class.getResource(name).toURI().toURL(), properties);
		Configuration configuration= new Configuration();

		parser.parse(configuration);

		ArrayList<URL> urls= new ArrayList<URL>(additionalUrls);

		for (URL url : urls)
		{
			ConfigurationParser parserForAdditionalKeepCodeConfigFile= new ConfigurationParser(url, properties);
			parserForAdditionalKeepCodeConfigFile.parse(configuration);
		}

		if (addToClasspath)
			System.setProperty("java.class.path", outFilename + ";" + System.getProperty("java.class.path"));

		new ProGuard(configuration).execute();

		return outputFile;
	}

	private File destinationDirectory;
	private File webappDirectory;
	private boolean removeCache= true;
	private boolean forceRebuild= true;
	private boolean compress;

	public DragomeStandaloneAppGenerator(File destinationDirectory, File webappDirectory, boolean removeCache, boolean forceRebuild, boolean compress)
	{
		this.destinationDirectory= destinationDirectory;
		this.webappDirectory= webappDirectory;
		this.removeCache= removeCache;
		this.forceRebuild= forceRebuild;
		this.compress = compress;
	}

	private void copyResource(String aResourceName, String aLocation)
	{
		LOGGER.info("Copy " + aResourceName + " to " + aLocation);
		InputStream theInputStream= getClass().getResourceAsStream(aResourceName);
		if (theInputStream != null)
		{
			int theLastPathIndex= aLocation.lastIndexOf('/');
			if (theLastPathIndex > 0)
			{
				String thePath= aLocation.substring(0, theLastPathIndex);
				thePath= thePath.replace('/', IOUtils.DIR_SEPARATOR);
				File theTargetDir= new File(destinationDirectory, thePath);
				if (!theTargetDir.exists())
				{
					if (!theTargetDir.mkdirs())
					{
						throw new RuntimeException("Cannot create directory " + theTargetDir);
					}
				}
			}
			String theSystemLocation= aLocation.replace('/', IOUtils.DIR_SEPARATOR);
			File theDestinatioFile= new File(destinationDirectory, theSystemLocation);
			try (FileOutputStream theOutputStream= new FileOutputStream(theDestinatioFile))
			{
				IOUtils.copy(theInputStream, theOutputStream);
			}
			catch (Exception e)
			{
				throw new RuntimeException("Cannot write data to " + theDestinatioFile, e);
			}
		}
		else
		{
			throw new IllegalArgumentException("Cannot find ressource " + aResourceName + " in ClassPath");
		}
	}

	private void copyResourceMinifyJS(String aResourceName)
	{
		//		JSMinProcessor theMinProcessor= new JSMinProcessor();

		String aLocation= "dragome-resources" + aResourceName;

		LOGGER.info("Copy " + aResourceName + " to minified " + aLocation);
		InputStream theInputStream= getClass().getResourceAsStream(aResourceName);
		if (theInputStream != null)
		{
			int theLastPathIndex= aLocation.lastIndexOf('/');
			if (theLastPathIndex > 0)
			{
				String thePath= aLocation.substring(0, theLastPathIndex);
				thePath= thePath.replace('/', IOUtils.DIR_SEPARATOR);
				File theTargetDir= new File(destinationDirectory, thePath);
				if (!theTargetDir.exists())
				{
					if (!theTargetDir.mkdirs())
					{
						throw new RuntimeException("Cannot create directory " + theTargetDir);
					}
				}
			}
			String theSystemLocation= aLocation.replace('/', IOUtils.DIR_SEPARATOR);
			File theDestinatioFile= new File(destinationDirectory, theSystemLocation);
			try
			{
				Files.copy(theInputStream, theDestinatioFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				//				theMinProcessor.process(new InputStreamReader(theInputStream), new FileWriter(theDestinatioFile));
			}
			catch (Exception e)
			{
				throw new RuntimeException("Cannot write data to " + theDestinatioFile, e);
			}
		}
		else
		{
			throw new IllegalArgumentException("Cannot find resource " + aResourceName + " in ClassPath");
		}
	}

	private void compile(String mainClass) throws Exception
	{
		System.setProperty("dragome-compile-mode", "release");

		final Classpath classPath= new Classpath();

		URLClassLoader theCurrentClassLoader= (URLClassLoader) getClass().getClassLoader();
		URL[] theConfiguredURLs= theCurrentClassLoader.getURLs();
		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		serviceLocator.setReflectionService(new ServerReflectionServiceImpl());
		if (serviceLocator.getConfigurator() == null)
			serviceLocator.setConfigurator(serviceLocator.getReflectionService().getConfigurator());

		for (URL theURL : theConfiguredURLs)
		{
			LOGGER.info("Found classpath element " + theURL);
			File file= new File(theURL.toURI());
			String theClassPathEntry= file.toString();
			boolean addToClasspath= serviceLocator.getConfigurator().filterClassPath(theClassPathEntry);

			if (addToClasspath)
				classPath.addEntry(theClassPathEntry);
			else
				LOGGER.warning("Skipping, it is not configured as an included artifact.");
		}

		File theTargetDir= new File(destinationDirectory, "compiled-js");
		if (!theTargetDir.exists() && !theTargetDir.mkdirs())
		{
			throw new RuntimeException("Cannot create directory " + theTargetDir);
		}

		File targetFile = new File(theTargetDir, "webapp.js");
		File theWebAppJS= targetFile;
		if (forceRebuild && theWebAppJS.exists())
		{
			if (!theWebAppJS.delete())
			{
				throw new RuntimeException("Cannot delete file " + theWebAppJS);
			}
		}

		// Store the dragome cache file here
		System.setProperty("cache-dir", theTargetDir.toString());

		LOGGER.info("Using Dragome compiler classpath : " + classPath.toString());

		DragomeStandaloneAppGenerator.compileWithMainClass(classPath, theTargetDir.toString(), mainClass);
		File dest= new File(theTargetDir, "webapp-original.js");
		theWebAppJS.renameTo(dest);

		// Ok, now we have a webapp.js file, do we need to minify it?
		LOGGER.info("Minifying webapp.js to compiled.js");
		JSMinProcessor theProcessor= new JSMinProcessor();

		//		Files.copy(theWebAppJS.toPath(), new File(theTargetDir, "webapp-1.js").toPath(), StandardCopyOption.REPLACE_EXISTING);
		CopyUtils.copyFilesOfFolder(webappDirectory, theTargetDir);

		if (compress)
		{
			theProcessor.process(new FileReader(dest), new FileWriter(targetFile));
			dest.delete();
		}
		else
			dest.renameTo(targetFile);

		// Finally remove the cache file
		if (removeCache)
		{
			File theCacheFile= new File(theTargetDir, "dragome.cache");
			LOGGER.info("Removing cache file " + theCacheFile);
			if (!theCacheFile.delete())
			{
				LOGGER.severe("Cannot delete cache file" + theCacheFile);
			}
		}
	}

	public void execute(String mainClass)
	{
		try
		{
			LOGGER.info("Generating Dragome Client Application at " + destinationDirectory);

			copyResources();
			compile(mainClass);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void copyResources()
	{
		copyResource("/css/dragome.css", "dragome-resources/css/dragome.css");

		copyResourceMinifyJS("/dragome-debug.js");
		copyResourceMinifyJS("/dragome-production.js");
		copyResourceMinifyJS("/js/hashtable.js");
		copyResourceMinifyJS("/js/deflate.js");
		copyResourceMinifyJS("/js/deflate-main.js");
		copyResourceMinifyJS("/js/helpers.js");
		copyResourceMinifyJS("/js/string.js");
		copyResourceMinifyJS("/js/qx-oo-5.0.1.min.js");
	}

}