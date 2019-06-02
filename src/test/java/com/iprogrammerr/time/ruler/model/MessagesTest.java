package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class MessagesTest {

    private static final String RESOURCE = "messages.properties";
    private static final String ABSENT_MESSAGE_FORMAT = "There is no message associated with %s value";
    private static final String ERROR_PREFIX = "error";
    private Messages messages;
    private Map<String, String> allMessages;

    @Before
    public void setup() {
        messages = new Messages();
        messages.init(RESOURCE);
        allMessages = new HashMap<>();
        Properties properties = new Properties();
        try (InputStream is = new BufferedInputStream(MessagesTest.class.getResourceAsStream("/" + RESOURCE))) {
            properties.load(is);
            for (Map.Entry<Object, Object> e : properties.entrySet()) {
                allMessages.put((String) e.getKey(), (String) e.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void resolvesMessage() {
        int index = new Random().nextInt(allMessages.size());
        String key = "";
        String value = "";
        int i = 0;
        for (Map.Entry<String, String> e : allMessages.entrySet()) {
            if (i == index) {
                key = e.getKey();
                value = e.getValue();
                break;
            }
            i++;
        }
        MatcherAssert.assertThat("Does not return expected message", messages.resolveMessage(key),
            Matchers.equalTo(value));
    }

    @Test
    public void returnsAbsentMessage() {
        String key = absentKey();
        String expected = String.format(ABSENT_MESSAGE_FORMAT, key);
        MatcherAssert.assertThat("Does not return proper absent message", messages.absentMessage(key),
            Matchers.equalTo(expected));
    }

    private String absentKey() {
        RandomStrings strings = new RandomStrings();
        String key = strings.alphabetic();
        while (allMessages.containsKey(key)) {
            key = strings.alphabetic();
        }
        return key;
    }

    @Test
    public void returnsErrorTranslation() {
        ErrorCode code = ErrorCode.values()[new Random().nextInt(ErrorCode.values().length)];
        String expected = allMessages.get(ERROR_PREFIX + code.ordinal());
        MatcherAssert.assertThat("Does not return proper error translation",
            messages.translated(code, Locale.getDefault()), Matchers.equalTo(expected));
    }
}
