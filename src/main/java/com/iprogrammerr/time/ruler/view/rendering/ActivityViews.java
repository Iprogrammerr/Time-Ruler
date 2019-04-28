package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//TODO client time offset
public class ActivityViews {

    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String INVALID_START_TIME_TEMPLATE = "invalidStartTime";
    private static final String INVALID_END_TIME_TEMPLATE = "invalidEndTime";
    private static final String NAME_TEMPLATE = "name";
    private static final String START_TIME_TEMPLATE = "start";
    private static final String END_TIME_TEMPLATE = "end";
    private static final String DESCRIPTION_TEMPLATE = "description";
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

    public String empty(Instant time) {
        Map<String, Object> params = new HashMap<>();
        String timeString = formatting.time(time);
        params.put(START_TIME_TEMPLATE, timeString);
        params.put(END_TIME_TEMPLATE, timeString);
        return templates.rendered(name, params);
    }

    public String withErrors(boolean nameError, boolean startError, boolean endError) {
        Map<String, Object> params = new HashMap<>();
        params.put(INVALID_NAME_TEMPLATE, nameError);
        params.put(INVALID_START_TIME_TEMPLATE, startError);
        params.put(INVALID_END_TIME_TEMPLATE, endError);
        return templates.rendered(name, params);
    }

    public String filled(DescribedActivity activity, Function<Long, Instant> timeTransformation) {
        Map<String, Object> params = new HashMap<>();
        params.put(NAME_TEMPLATE, activity.activity.name);
        String startTime = formatting.time(timeTransformation.apply(activity.activity.startDate));
        params.put(START_TIME_TEMPLATE, startTime);
        String endTime = formatting.time(timeTransformation.apply(activity.activity.endDate));
        params.put(END_TIME_TEMPLATE, endTime);
        params.put(DESCRIPTION_TEMPLATE, activity.description);
        return templates.rendered(name, params);
    }
}
