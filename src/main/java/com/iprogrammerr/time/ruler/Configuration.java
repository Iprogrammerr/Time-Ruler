package com.iprogrammerr.time.ruler;

import java.util.Properties;

public class Configuration {

    private final Properties properties;

    public Configuration(Properties properties) {
        this.properties = properties;
    }

    public int port() {
        return Integer.parseInt(properties.getProperty("port"));
    }
}
