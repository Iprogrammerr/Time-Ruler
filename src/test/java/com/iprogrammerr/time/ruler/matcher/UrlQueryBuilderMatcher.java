package com.iprogrammerr.time.ruler.matcher;

import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;

public class UrlQueryBuilderMatcher extends TypeSafeMatcher<UrlQueryBuilder> {

    private static final String START = "?";
    private static final String PARAMS_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private final Map<String, Object> params;
    private final String base;

    public UrlQueryBuilderMatcher(Map<String, Object> params, String base) {
        this.params = params;
        this.base = base;
    }

    @Override
    protected boolean matchesSafely(UrlQueryBuilder item) {
        return (params.isEmpty() && item.build(base).equals(base)) || checkParams(item);
    }

    private boolean checkParams(UrlQueryBuilder item) {
        boolean matched;
        try {
            String query = item.build(base);
            int startIndex = query.indexOf(START);
            matched = startIndex > -1;
            if (matched && base.equals(query.substring(0, startIndex))) {
                String[] params = query.substring(startIndex + 1, query.length()).split(PARAMS_SEPARATOR);
                for (String kv : params) {
                    String[] kAndV = kv.split(KEY_VALUE_SEPARATOR);
                    matched = this.params.getOrDefault(kAndV[0], "").toString()
                        .equals(kAndV[1]);
                    if (!matched) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            matched = false;
        }
        return matched;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(UrlQueryBuilder.class.getSimpleName());
    }

    @Override
    protected void describeMismatchSafely(UrlQueryBuilder item, Description mismatchDescription) {
        mismatchDescription.appendText(String.format("%s is not a proper query", item.build(base)));
    }
}
