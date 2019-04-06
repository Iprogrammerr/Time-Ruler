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
    private static final String ACTIVATION_LINK_BASE = "activationLinkBase";
    private static final String SIGN_UP_EMAIL_SUBJECT = "signUpEmailSubject";
    private static final String SIGN_UP_EMAIL_TEMPLATE = "signUpEmailTemplate";
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
        return Integer.parseInt(getOrThrow(PORT));
    }

    public String resourcesPath() {
        String path;
        if (Boolean.parseBoolean(getOrThrow(COMPILED_RESOURCES))) {
            path = Configuration.class.getResource("/").getPath();
        } else {
            String classPath = Configuration.class.getResource(".").getPath();
            path = classPath.substring(0, classPath.indexOf("target")) + "src/main/resources";
        }
        return path;
    }

    public String databaseUser() {
        return getOrThrow(DATABASE_USER);
    }

    public String databasePassword() {
        return getOrThrow(DATABASE_PASSWORD);
    }

    public String jdbcUrl() {
        return getOrThrow(JDBC_URL);
    }

    public String adminEmail() {
        return getOrThrow(ADMIN_EMAIL);
    }

    public String adminPassword() {
        return getOrThrow(ADMIN_PASSWORD);
    }

    public String smtpHost() {
        return getOrThrow(SMTP_HOST);
    }

    public int smtpPort() {
        return Integer.parseInt(getOrThrow(SMTP_PORT));
    }

    public String activationLinkBase() {
        return getOrThrow(ACTIVATION_LINK_BASE);
    }

    public String signUpEmailSubject() {
        return getOrThrow(SIGN_UP_EMAIL_SUBJECT);
    }

    public String signUpEmailTemplate() {
        String template = getOrThrow(SIGN_UP_EMAIL_TEMPLATE);
        if (!template.contains("%s")) {
            throw new RuntimeException("Template must contain %s sign for inserting activation link");
        }
        return template;
    }


    private String getOrThrow(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(String.format("There is no property associated with %s key", key));
        }
        return value;
    }
}
