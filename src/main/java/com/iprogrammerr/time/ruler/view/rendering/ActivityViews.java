package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

//TODO client time offset
public class ActivityViews {

    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String INVALID_START_TIME_TEMPLATE = "invalidStartTime";
    private static final String INVALID_END_TIME_TEMPLATE = "invalidEndTime";
    private static final String NAME_TEMPLATE = "name";
    private static final String START_TIME_TEMPLATE = "start";
    private static final String END_TIME_TEMPLATE = "end";
    private static final String DESCRIPTION_TEMPLATE = "description";
    private static final String TIME_FORMAT = "%02d:%02d";
    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final String name;

    public ActivityViews(ViewsTemplates templates, DateTimeFormatting formatting, String name) {
        this.templates = templates;
        this.formatting = formatting;
        this.name = name;
    }

    public ActivityViews(ViewsTemplates templates, DateTimeFormatting formatting) {
        this(templates, formatting, "activity");
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

    public String filled(DescribedActivity activity) {
        Map<String, Object> params = new HashMap<>();
        params.put(NAME_TEMPLATE, activity.activity.name);
        params.put(START_TIME_TEMPLATE, formatting.timeFromSeconds(activity.activity.startTime));
        params.put(END_TIME_TEMPLATE, formatting.timeFromSeconds(activity.activity.endTime));
        params.put(DESCRIPTION_TEMPLATE, activity.description);
        return templates.rendered(name, params);
    }
}
