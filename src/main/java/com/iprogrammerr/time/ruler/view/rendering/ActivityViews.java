package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActivityViews {

    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String INVALID_START_TIME_TEMPLATE = "invalidStartTime";
    private static final String INVALID_END_TIME_TEMPLATE = "invalidEndTime";
    private static final String PLAN_TEMPLATE = "plan";
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

    public String empty(Instant time, boolean plan) {
        Map<String, Object> params = new HashMap<>();
        addActiveTab(params, plan);
        String timeString = formatting.time(time);
        params.put(PLAN_TEMPLATE, plan);
        params.put(NAME_TEMPLATE, "");
        params.put(START_TIME_TEMPLATE, timeString);
        params.put(END_TIME_TEMPLATE, timeString);
        params.put(DESCRIPTION_TEMPLATE, "");
        return templates.rendered(name, params);
    }

    private void addActiveTab(Map<String, Object> params, boolean plan) {
        params.put(ActiveTab.KEY, plan ? ActiveTab.PLAN : ActiveTab.HISTORY);
    }

    public String withErrors(boolean plan, ValidateableName name, ValidateableTime startTime, ValidateableTime endTime) {
        Map<String, Object> params = new HashMap<>();
        addActiveTab(params, plan);
        params.put(PLAN_TEMPLATE, plan);
        params.put(INVALID_NAME_TEMPLATE, name.isValid());
        params.put(NAME_TEMPLATE, name.value());
        params.put(INVALID_START_TIME_TEMPLATE, startTime.isValid());
        params.put(START_TIME_TEMPLATE, startTime.isValid() ? startTime.value() : "");
        params.put(INVALID_END_TIME_TEMPLATE, endTime.isValid());
        params.put(END_TIME_TEMPLATE, endTime.isValid() ? endTime.value() : "");
        return templates.rendered(this.name, params);
    }

    public String filled(DescribedActivity activity, Function<Long, Instant> timeTransformation) {
        Map<String, Object> params = new HashMap<>();
        boolean plan = !activity.activity.done;
        addActiveTab(params, plan);
        params.put(PLAN_TEMPLATE, plan);
        params.put(NAME_TEMPLATE, activity.activity.name);
        String startTime = formatting.time(timeTransformation.apply(activity.activity.startDate));
        params.put(START_TIME_TEMPLATE, startTime);
        String endTime = formatting.time(timeTransformation.apply(activity.activity.endDate));
        params.put(END_TIME_TEMPLATE, endTime);
        params.put(DESCRIPTION_TEMPLATE, activity.description);
        return templates.rendered(name, params);
    }
}
