package com.github.xpenatan.gdx.backends.dragome;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.dragome.commons.ChainedInstrumentationDragomeConfigurator;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.compiler.PrioritySolver;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.serverside.VirtualFolderClasspathEntry;
import com.dragome.web.helpers.DefaultClasspathFileFilter;

@DragomeConfiguratorImplementor(priority= 10)
public class DragomeConfigurator extends ChainedInstrumentationDragomeConfigurator
{

	private static Logger LOGGER= Logger.getLogger(DragomeConfigurator.class.getName());
	final StringBuilder sb = new StringBuilder();

	String projName;

	public DragomeConfigurator()
	{
		super();

		String projPath= System.getProperty("user.dir");
		File file= new File(projPath);
		projName = file.getName().replace("\\", "/");

		final String thisClassName = getClass().getSimpleName();

		setClasspathFilter(new DefaultClasspathFileFilter()
		{
			public boolean accept(ClasspathFile classpathFile)
			{
				if(!super.accept(classpathFile))
					return false;
				boolean flag = true;
				String fileName = classpathFile.getFilename();
				String path = classpathFile.getPath();
				path = path.replace("\\", "/");

				{
					// All classes will compile except
					flag = true;
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, DragomeConfigurator.class.getSimpleName(), flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/github/xpenatan/gdx/backends/dragome/preloader/AssetFilter", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/github/xpenatan/gdx/backends/dragome/preloader/AssetsCopy", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/github/xpenatan/gdx/backends/dragome/preloader/DefaultAssetFilter", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/github/xpenatan/gdx/backends/dragome/preloader/DefaultAssetFilter", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".conf", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "javax/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "junit/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/junit/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "sun/reflect", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".html", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".idl", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".css", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".MF", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".xml", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".txt", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".properties", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".template", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/dragome/tests/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/dragome/web/config/DomHandlerApplicationConfigurator", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/dragome/web/helpers/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/dragome/commons/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/dragome/compiler/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/android/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/jf", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/xmlvm/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/github/javaparser/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/hamcrest/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/badlogic/gdx/jnigen/", flag);
					flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, thisClassName, flag);

					{
						//Ignore classes added by gradle getty. Eclipse jetty is not needed.
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "javolution/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "net/jpountz/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "net/sf/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".tld", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "proguard/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "com/google/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "io/netty/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "JDOMAbout", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "ro/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/apache", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/xml/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/mockito/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/atmosphere/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/objectweb/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/objenesis/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/hamcrest/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/dom4j/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/slf4j/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/jdom/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "org/reflections/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "META-INF/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "javassist/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".vm", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".so", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, ".dylib", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "/serverside/", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "LICENSE", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "NOTICE", flag);
						flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, "Main.class", flag);
					}

					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/commons/AbstractProxyRelatedInvocationHandler", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/commons/ProxyRelatedInvocationHandler", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/commons/compiler/annotations/", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/commons/javascript/", flag);
				}

				boolean test2 = false;

//				flag = false;
//
//				flag = toAccept(ACCEPT_TYPE.DONT_ACCEPT, path, thisClassName, flag);
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "TestLauncher", flag);
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "MyClassTest", flag);
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "/Object.class", flag);
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "/String", flag);
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/io/PrintStream", flag);
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/io/OutputStream.class", flag);
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/lang/System.class", flag);
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "runtime.js", flag); // required to compile
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/view/VisualActivity.class", flag); // required interface to writes to js file
//				flag = toAccept(ACCEPT_TYPE.ACCEPT, path, ".js", flag);

				if(test2) {

					{
						flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/lang/", flag);
					}
					{

	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/logging/", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/HashMap", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/List", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/ArrayList", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/AbstractList", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/HashSet", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/Collection", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/AbstractCollection", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/Calendar", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/TimeZone", flag); // required to compile
	//					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util/Date", flag); // required to compile
						flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/util", flag); // required to compile
						flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "java/", flag); // required to compile
					}



					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "runtime.js", flag); // required to compile
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "javascript/Utils.class", flag); // required
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/view/VisualActivity.class", flag); // required interface to writes to js file

					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "TestLauncher", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "PageAlias", flag);

					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/commons/compiler/annotations/", flag);  // required
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/commons/javascript/", flag); // required


					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/commons/ProxyRelatedInvocationHandler", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/commons/AbstractProxyRelatedInvocationHandler.class", flag);

					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "webdefault.xml", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, ".js", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "flexjson/", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/view/", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/services/", flag);
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/helpers/", flag);


					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "org/w3c/", flag);   // required
					flag = toAccept(ACCEPT_TYPE.ACCEPT, path, "com/dragome/web/", flag);   // required

				}

				if(path.endsWith("/"))
					flag = false;

				if(flag)
					System.out.println("Allow Class: " + flag + " Path: " + path);

				return flag;
			}
		});

	}

	public boolean isRemoveUnusedCode()
	{
		return false;
	}

	@Override
	public boolean isObfuscateCode()
	{
		return false;
	}

	public List<URL> getAdditionalCodeKeepConfigFile()
	{
		URL resource = getClass().getResource("/dragome/proguard-extra.conf");
		return Arrays.asList(resource);
	}

	public List<URL> getAdditionalObfuscateCodeKeepConfigFile()
	{
		URL resource = getClass().getResource("/proguard-extra.conf");
		return Arrays.asList(resource);
	}

	public boolean filterClassPath(String classpathEntry)
	{
		boolean include= super.filterClassPath(classpathEntry);
		classpathEntry = classpathEntry.replace("\\", "/");
		include |= classpathEntry.contains(projName + "/") && classpathEntry.endsWith("/bin");
		include |= classpathEntry.contains("dragome-js-jre");
		include |= classpathEntry.contains("dragome-web");
		include |= classpathEntry.contains("dragome-core");
		include |= classpathEntry.contains("dragome-js-commons");
		include |= classpathEntry.contains("dragome-w3c-standards");
		include |= classpathEntry.contains("dragome-bytecode-js-compiler");

		include &= !classpathEntry.contains("/resources/");


		if(!include && classpathEntry.contains("/bin"))
			include = true;

		if(include) {
			String text = "Compile: " + include + " path: " + classpathEntry + "\n";

			System.out.println(text);
		}

		return include;
	}


	@Override
	public void sortClassPath(Classpath classPath) {
		classPath.sortByPriority(new PrioritySolver()
		{
			@Override
			public int getPriorityOf(ClasspathEntry string)
			{
				String name = string.getName();
				if (name.contains("web-backend"))
					return 5;
				else if (name.contains("dragome-backend"))
					return 4; // dragome backend is first so it can override any dragome classes
				else if (name.contains("dragome-js-jre"))
					return 3;
				else if (name.contains("dragome-"))
					return 2;
				else if (name.contains("gdx-box2d-gwt"))
					return 1;
				else
					return 0;
			}
		});

		if(sb.length() > 0)
			sb.insert(0, "\n" + "########### Libs/Classes PATH to allow Dragome to compile ###########" + "\n");

		Iterator<ClasspathEntry> iterator = classPath.getEntries().iterator();
		int i = 0;
		sb.append("\n" + "######################## Libs ClassPath Order ########################\n");
		while(iterator.hasNext()) {
			ClasspathEntry next = iterator.next();
			String name = next.getName();
			if(next instanceof VirtualFolderClasspathEntry)
				name = "Delegate Virtual Path";
			sb.append(i + ": " + name);
			sb.append("\n");
			i++;
		}
		sb.append("#################################################################");
		LOGGER.info(sb.toString());
		sb.setLength(0);
	}

	@Override
	public boolean isCaching()
	{
		return false;
	}

	public boolean toAccept(ACCEPT_TYPE filter, String path, String toSearch, boolean flag) {
		boolean contains = path.contains(toSearch);
		if(filter == ACCEPT_TYPE.DONT_ACCEPT)
			flag &= !contains;
		else if(filter == ACCEPT_TYPE.ACCEPT)
			flag |= contains;
		return flag;
	}

	enum ACCEPT_TYPE{
		ACCEPT, DONT_ACCEPT
	}


	@Override
	public CompilerType getDefaultCompilerType() {
		return CompilerType.Standard;
	}
}
