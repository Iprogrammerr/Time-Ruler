package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class ActivityView {

    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String INVALID_START_TIME_TEMPLATE = "invalidStartTime";
    private static final String INVALID_END_TIME_TEMPLATE = "invalidEndTime";
    private static final String NAME_TEMPLATE = "name";
    private static final String START_TIME_TEMPLATE = "start";
    private static final String END_TIME_TEMPLATE = "end";
    private static final String DESCRIPTION_TEMPLATE = "description";
    private static final String TIME_FORMAT = "%02d:%02d";
    private final ViewsTemplates templates;
    private final String name;

    public ActivityView(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public ActivityView(ViewsTemplates templates) {
        this(templates, "activity");
    }

    public String empty(int hour, int minute) {
        Map<String, Object> params = new HashMap<>();
        String time = String.format(TIME_FORMAT, hour, minute);
        params.put(START_TIME_TEMPLATE, time);
        params.put(END_TIME_TEMPLATE, time);
        return templates.rendered(name, params);
    }

    public String withErrors(boolean nameError, boolean startError, boolean endError) {
        Map<String, Object> params = new HashMap<>();
        params.put(INVALID_NAME_TEMPLATE, nameError);
        params.put(INVALID_START_TIME_TEMPLATE, startError);
        params.put(INVALID_END_TIME_TEMPLATE, endError);
        return templates.rendered(name, params);
    }

    public String filled(String name, String startTime, String endTime, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put(NAME_TEMPLATE, name);
        params.put(START_TIME_TEMPLATE, startTime);
        params.put(END_TIME_TEMPLATE, endTime);
        params.put(DESCRIPTION_TEMPLATE, description);
        return templates.rendered(name, params);
    }
}
