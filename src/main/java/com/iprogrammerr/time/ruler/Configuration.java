package com.iprogrammerr.time.ruler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    public static final String PORT = "port";
    public static final String COMPILED_RESOURCES = "compiledResources";
    public static final String DATABASE_USER = "database.user";
    public static final String DATABASE_PASSWORD = "database.password";
    public static final String JDBC_URL = "jdbc-url";
    public static final String ADMIN_EMAIL = "admin.email";
    public static final String ADMIN_PASSWORD = "admin.password";
    public static final String SMTP_HOST = "smtp.host";
    public static final String SMTP_PORT = "smtp.port";
    public static final String EMAILS_LINKS_BASE = "emailsLinksBase";
    public static final String SIGN_UP_EMAIL_SUBJECT = "signUpEmailSubject";
    public static final String SIGN_UP_EMAIL_TEMPLATE = "signUpEmailTemplate";
    public static final String PASSWORD_RESET_EMAIL_SUBJECT = "passwordResetEmailSubject";
    public static final String PASSWORD_RESET_EMAIL_TEMPLATE = "passwordResetEmailTemplate";
    private final Properties properties;

    public Configuration(Properties properties) {
        this.properties = properties;
    }

    public static Configuration fromCmd(String... args) {
        try (InputStream is = args.length == 0 ? Configuration.class.getResourceAsStream("/application.properties")
            : new FileInputStream(args[0])) {
            Properties properties = new Properties();
            properties.load(is);
            return new Configuration(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Configuration fromClassPath() {
        return fromCmd();
    }

    public int port() {
        return Integer.parseInt(notNull(PORT));
    }

    public boolean useCompiledResources() {
        return Boolean.parseBoolean(notNull(COMPILED_RESOURCES));
    }

    public String resourcesPath() {
        String path;
        if (useCompiledResources()) {
            path = Configuration.class.getResource("/").getPath();
        } else {
            String classPath = Configuration.class.getResource(".").getPath();
            path = classPath.substring(0, classPath.indexOf("target")) + "src/main/resources";
        }
        return path;
    }

    public String databaseUser() {
        return notNull(DATABASE_USER);
    }

    public String databasePassword() {
        return notNull(DATABASE_PASSWORD);
    }

    public String jdbcUrl() {
        return notNull(JDBC_URL);
    }

    public String adminEmail() {
        return notNull(ADMIN_EMAIL);
    }

    public String adminPassword() {
        return notNull(ADMIN_PASSWORD);
    }

    public String smtpHost() {
        return notNull(SMTP_HOST);
    }

    public int smtpPort() {
        return Integer.parseInt(notNull(SMTP_PORT));
    }

    public String emailsLinksBase() {
        return notNull(EMAILS_LINKS_BASE);
    }

    public String signUpEmailSubject() {
        return notNull(SIGN_UP_EMAIL_SUBJECT);
    }

    public String signUpEmailTemplate() {
        return validatedEmailTemplate(SIGN_UP_EMAIL_TEMPLATE);
    }

    private String validatedEmailTemplate(String key) {
        String template = notNull(key);
        if (!template.contains("%s")) {
            throw new RuntimeException("Template must contain %s sign for inserting a link");
        }
        return template;
    }

    public String passwordResetEmailSubject() {
        return notNull(PASSWORD_RESET_EMAIL_SUBJECT);
    }

    public String passwordResetEmailTemplate() {
        return validatedEmailTemplate(PASSWORD_RESET_EMAIL_TEMPLATE);
    }

    private String notNull(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(String.format("There is no property associated with %s value", key));
        }
        return value;
    }

    @Override
    public String toString() {
        return "Configuration{" +
            "properties=" + properties +
            '}';
    }
}
