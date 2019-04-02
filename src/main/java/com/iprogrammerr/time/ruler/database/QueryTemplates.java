package com.iprogrammerr.time.ruler.database;

public class QueryTemplates {

    private static final char PARAM_SIGN = '?';

    public String query(String template, Object... values) {
        int[] paramsIndices = paramsIndices(template, values.length);
        StringBuilder builder = new StringBuilder();
        int beginIndex = 0;
        for (int i = 0; i < paramsIndices.length; i++) {
            builder.append(template.substring(beginIndex, paramsIndices[i])).append(toStringValue(values[i]));
            beginIndex = paramsIndices[i] + 1;
        }
        builder.append(template.substring(paramsIndices[paramsIndices.length - 1] + 1));
        return builder.toString();
    }

    private String toStringValue(Object value) {
        if (String.class.isAssignableFrom(value.getClass())) {
            return "'" + value + "'";
        }
        return value.toString();
    }

    private int[] paramsIndices(String template, int size) {
        int[] indices = new int[size];
        try {
            char[] chars = template.toCharArray();
            int j = 0;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == PARAM_SIGN) {
                    indices[j] = i;
                    j++;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(String.format("Incorrect number of params in template, expected: %d ", size), e);
        }
        return indices;
    }

    public String insert(Record record) {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ").append(record.table()).append(" (");
        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        keysBuilder.append(record.keys().get(0));
        valuesBuilder.append(toStringValue(record.values().get(0)));
        for (int i = 1; i < record.keys().size(); i++) {
            keysBuilder.append(", ").append(record.keys().get(i));
            valuesBuilder.append(", ").append(record.values().get(i));
        }
        keysBuilder.append(")");
        valuesBuilder.append(")");
        return builder.append(keysBuilder).append(" values (").append(valuesBuilder).toString();
    }

    public String update(Record record, String whereTemplate, Object... values) {
        StringBuilder builder = new StringBuilder();
        builder.append("update ").append(record.table()).append(" set ");
        builder.append(record.keys().get(0)).append("=").append(toStringValue(record.values().get(0)));
        for (int i = 1; i < record.keys().size(); i++) {
            builder.append(", ").append(record.keys().get(i)).append("=").append(toStringValue(record.values().get(i)));
        }
        return builder.append(" where ").append(query(whereTemplate, values)).toString();
    }
}
