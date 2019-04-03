package com.iprogrammerr.time.ruler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final String PORT = "port";
    private static final String COMPILED_RESOURCES = "compiledResources";
    private static final String DATABASE_USER = "database.user";
    private static final String DATABASE_PASSWORD = "database.password";
    private static final String JDBC_URL = "jdbc-url";
    private static final String ADMIN_EMAIL = "admin.email";
    private static final String ADMIN_PASSWORD = "admin.password";
    private static final String SMTP_HOST = "smtp.host";
    private static final String SMTP_PORT = "smtp.port";
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

    public String databaseUser() {
        return properties.getProperty(DATABASE_USER);
    }

    public String databasePassword() {
        return properties.getProperty(DATABASE_PASSWORD);
    }

    public String jdbcUrl() {
        return properties.getProperty(JDBC_URL);
    }

    public String adminEmail() {
        return properties.getProperty(ADMIN_EMAIL);
    }

    public String adminPassword() {
        return properties.getProperty(ADMIN_PASSWORD);
    }

    public String smtpHost() {
        return properties.getProperty(SMTP_HOST);
    }

    public int smtpPort() {
        return Integer.parseInt(properties.getProperty(SMTP_PORT));
    }
}
