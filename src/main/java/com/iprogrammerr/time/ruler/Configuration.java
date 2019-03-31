package com.iprogrammerr.time.ruler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final String PORT = "port";
    private static final String COMPILED_RESOURCES = "compiledResources";
    private final Properties properties;

    public Configuration(Properties properties) {
        this.properties = properties;
    }

    public static Configuration fromCmd(String[] args) {
        try (InputStream is = args.length == 0 ? App.class.getResourceAsStream("/application.properties")
            : new FileInputStream(args[0])) {
            Properties properties = new Properties();
            properties.load(is);
            return new Configuration(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int port() {
        return Integer.parseInt(properties.getProperty(PORT));
    }

    public boolean useCompiledResources() {
        return Boolean.parseBoolean(properties.getProperty(COMPILED_RESOURCES));
    }

    public String resourcesPath() {
        String path;
        if (Boolean.parseBoolean(properties.getProperty(COMPILED_RESOURCES))) {
            path = Configuration.class.getResource("/").getPath();
        } else {
            String classPath = Configuration.class.getResource(".").getPath();
            path = classPath.substring(0, classPath.indexOf("target")) + "src/main/resources";
        }
        return path;
    }
}
