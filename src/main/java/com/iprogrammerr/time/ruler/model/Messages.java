package com.iprogrammerr.time.ruler.model;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.IMessageResolver;
import sun.plugin2.message.Message;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Messages implements IMessageResolver {

    private final Map<String, String> source;

    public Messages() {
        this.source = new HashMap<>();
    }

    public void init(String resource) {
        try (InputStream is = Message.class.getResourceAsStream("/" + resource)) {
            Properties properties = new Properties();
            properties.load(is);
            init(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void init(Properties properties) {
        for (Object k : properties.keySet()) {
            String key = (String) k;
            source.put(key, properties.getProperty(key));
        }
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Integer getOrder() {
        return 1;
    }

    @Override
    public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        String message = source.get(key);
        if (message == null) {
            message = absentMessage(key);
        }
        return message;
    }

    private String absentMessage(String key) {
        return String.format("There is no message associated with %s key", key);
    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return absentMessage(key);
    }
}
