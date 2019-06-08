package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.date.FormattedTimes;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActivityViews {

    public static final String INVALID_NAME_TEMPLATE = "invalidName";
    public static final String INVALID_START_TIME_TEMPLATE = "invalidStartTime";
    public static final String INVALID_END_TIME_TEMPLATE = "invalidEndTime";
    public static final String PLAN_TEMPLATE = "plan";
    public static final String NAME_TEMPLATE = "name";
    public static final String START_HOUR_TEMPLATE = "startHour";
    public static final String START_MINUTE_TEMPLATE = "startMinute";
    public static final String START_TIME_TEMPLATE = "start";
    public static final String END_HOUR_TEMPLATE = "endHour";
    public static final String END_MINUTE_TEMPLATE = "endMinute";
    public static final String END_TIME_TEMPLATE = "end";
    public static final String DESCRIPTION_TEMPLATE = "description";
    public final String name;
    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;

    public ActivityViews(ViewsTemplates templates, DateTimeFormatting formatting, String name) {
        this.templates = templates;
        this.formatting = formatting;
        this.name = name;
    }

    public ActivityViews(ViewsTemplates templates, DateTimeFormatting formatting) {
        this(templates, formatting, "activity");
    }

    public String empty(ZonedDateTime time, boolean plan) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActiveTab.KEY, ActiveTab.planHistory(plan));
        params.put(PLAN_TEMPLATE, plan);
        params.put(NAME_TEMPLATE, "");
        formatting.times(time).put(params);
        params.put(DESCRIPTION_TEMPLATE, "");
        return templates.rendered(name, params);
    }

    public String withErrors(ZonedDateTime time, boolean plan, ValidateableName name, ValidateableTime startTime,
        ValidateableTime endTime, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActiveTab.KEY, ActiveTab.planHistory(plan));
        params.put(PLAN_TEMPLATE, plan);
        params.put(INVALID_NAME_TEMPLATE, !name.isValid());
        params.put(NAME_TEMPLATE, name.value());
        formatting.times(time).put(params);
        params.put(INVALID_START_TIME_TEMPLATE, !startTime.isValid());
        if (startTime.isValid()) {
            params.put(START_TIME_TEMPLATE, formatting.time(startTime.value()));
        }
        params.put(INVALID_END_TIME_TEMPLATE, !endTime.isValid());
        if (endTime.isValid()) {
            params.put(END_TIME_TEMPLATE, formatting.time(endTime.value()));
        }
        params.put(DESCRIPTION_TEMPLATE, description);
        return templates.rendered(this.name, params);
    }

    public String filled(DescribedActivity activity, Function<Long, ZonedDateTime> timeTransformation) {
        Map<String, Object> params = new HashMap<>();
        boolean plan = !activity.activity.done;
        params.put(ActiveTab.KEY, ActiveTab.planHistory(plan));
        params.put(PLAN_TEMPLATE, plan);
        params.put(NAME_TEMPLATE, activity.activity.name);
        FormattedTimes times = formatting.times(timeTransformation.apply(activity.activity.startDate),
            timeTransformation.apply(activity.activity.endDate));
        times.put(params);
        params.put(DESCRIPTION_TEMPLATE, activity.description);
        return templates.rendered(name, params);
    }
}
