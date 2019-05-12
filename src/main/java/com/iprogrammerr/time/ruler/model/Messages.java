package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ErrorsTranslation;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.IMessageResolver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class Messages implements IMessageResolver, ErrorsTranslation {

    private final Map<String, String> source;
    private final String errorsPrefix;

    public Messages(String errorsPrefix) {
        this.source = new HashMap<>();
        this.errorsPrefix = errorsPrefix;
    }

    public Messages() {
        this("error");
    }

    public void init(String resource) {
        try (InputStream is = Messages.class.getResourceAsStream("/" + resource)) {
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

    public String absentMessage(String key) {
        return String.format("There is no message associated with %s value", key);
    }

    public String resolveMessage(String key) {
        return resolveMessage(null, Messages.class, key, new Object[0]);
    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return absentMessage(key);
    }

    //TODO make it possible to use locale
    @Override
    public String translated(ErrorCode code, Locale locale) {
        String key = errorsPrefix + code.ordinal();
        String translation = source.get(key);
        if (translation == null) {
            translation = absentMessage(key);
        }
        return translation;
    }
}
