package com.github.xpenatan.gdx.backends.teavm.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TeaVMResourceProperties {
    private static final String OPTION_ADDITIONAL_RESOURCES = "resources";
    private static final String OPTION_IGNORE_RESOURCES = "ignore-resources";

    public String path;

    public ArrayList<String> additionalPath = new ArrayList<>();
    public ArrayList<String> ignorePath = new ArrayList<>();

    public TeaVMResourceProperties(String path, String content) {
        this.path = path;

        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            String[] split = line.split("=");
            if(split.length == 2) {
                String option = split[0].trim();
                String value = split[1].trim();
                if(!option.isEmpty() && !value.isEmpty()) {
                    setupOption(option, value);
                }
            }
        }
        scanner.close();
    }

    private void setupOption(String option, String value) {
        if(option.equals(OPTION_ADDITIONAL_RESOURCES)) {
            additionalPath.add(value);
        }
        if(option.equals(OPTION_IGNORE_RESOURCES)) {
            ignorePath.add(value);
        }
    }
    public static List<String> getResources(ArrayList<URL> acceptedURL) {
        // Get all resources
        ArrayList<TeaVMResourceProperties> propertiesList = getAllProperties(acceptedURL);
        ArrayList<URI> filteredUrl = new ArrayList<>();
        HashSet<String> ignoreResources = new HashSet<>();
        for(URL url : acceptedURL) {
            String path = url.getPath();
            boolean accept = false;
            for(TeaVMResourceProperties properties : propertiesList) {
                ignoreResources.addAll(properties.ignorePath);
                // Accept if properties exist in current path
                if(path.contains(properties.path)) {
                    accept = true;
                    break;
                }
                for(String additionalPath : properties.additionalPath) {
                    // Check if the jar path contains in properties
                    if(path.contains(additionalPath)) {
                        accept = true;
                        break;
                    }
                }

                if(accept) {
                    break;
                }
            }
            if(accept) {
                URI uri = URI.create("jar:file:" + path);
                filteredUrl.add(uri);
            }
        }

        ArrayList<String> result = new ArrayList<>();
        for(URI uri : filteredUrl) {
            ArrayList<String> pathsFromResource = getPathsFromResource(uri, ".");
            for(String resource : pathsFromResource) {
                if(!containsResource(resource, ignoreResources)) {
                    result.add(resource);
                }
            }
        }
        return result;
    }

    private static boolean containsResource(String resource, HashSet<String> ignoreResources) {
        for(String ignore : ignoreResources) {
            if(resource.contains(ignore)) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<TeaVMResourceProperties> getAllProperties(ArrayList<URL> acceptedURL) {
        ArrayList<String> filteredUrl = new ArrayList<>();
        for(URL url : acceptedURL) {
            String path = url.getPath();
            path = URLDecoder.decode(path, StandardCharsets.UTF_8);
            boolean accept = !(
                    !(path.endsWith(".jar")) ||
                            path.contains("org.teavm"
                            ));
            if(accept) {
                filteredUrl.add(path);
            }
        }
        ArrayList<TeaVMResourceProperties> result = new ArrayList<>();
        for(String path : filteredUrl) {
            TeaVMResourceProperties properties = getProperties(path);
            if(properties != null) {
                result.add(properties);
            }
        }
        return result;
    }

    private static TeaVMResourceProperties getProperties(String path) {
        try {
            try(ZipFile zipFile = new ZipFile(path)) {
                ZipEntry propertyEntry = zipFile.getEntry("META-INF/gdx-teavm.properties");
                if(propertyEntry != null) {
                    InputStream inputStream = zipFile.getInputStream(propertyEntry);
                    String content = readString(inputStream, null);
                    inputStream.close();
                    return new TeaVMResourceProperties(path, content);
                }
            }
        } catch(IOException e) {
        }
        return null;
    }

    public static String readString(InputStream in, String charset) {
        StringBuilder output = new StringBuilder(512);
        InputStreamReader reader = null;
        try {
            if(charset == null)
                reader = new InputStreamReader(in);
            else
                reader = new InputStreamReader(in, charset);
            char[] buffer = new char[256];
            while(true) {
                int length = reader.read(buffer);
                if(length == -1) break;
                output.append(buffer, 0, length);
            }
        } catch(IOException ex) {
            throw new RuntimeException("Error reading resource zip file", ex);
        } finally {
            try {
                if(reader != null) reader.close();
            } catch(IOException ignored) {
            }
        }
        return output.toString();
    }

    private static ArrayList<String> getPathsFromResource(URI uri, String folder) {
        ArrayList<String> result = new ArrayList<>();
        List<Path> resultPath = null;

        try(FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            resultPath = Files.walk(fs.getPath(folder))
                    .filter(Files::isRegularFile)
                    .filter(new Predicate<Path>() {
                        @Override
                        public boolean test(Path path) {
                            String pathStr = path.toString();
                            boolean isValid = !(
                                    pathStr.endsWith(".java") ||
                                            pathStr.endsWith(".class") ||
                                            pathStr.contains("META-INF") ||
                                            pathStr.contains("WEB-INF") ||
                                            pathStr.endsWith(".html") ||
                                            pathStr.endsWith(".gwt.xml") ||
                                            pathStr.endsWith(".rl")
                            );
                            return isValid;
                        }
                    })
                    .collect(Collectors.toList());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        if(resultPath != null) {
            for(Path path : resultPath) {
                String string = path.toString();
                if(string.startsWith(".")) {
                    string = string.replaceFirst("\\.", "");
                }
                result.add(string);
            }
        }
        return result;
    }
}