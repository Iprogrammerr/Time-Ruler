package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActivityViews {

    private static final int MAX_HOUR = 23;
    private static final String HOUR_MINUTE_FORMAT = "%02d";
    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String INVALID_START_TIME_TEMPLATE = "invalidStartTime";
    private static final String INVALID_END_TIME_TEMPLATE = "invalidEndTime";
    private static final String PLAN_TEMPLATE = "plan";
    private static final String NAME_TEMPLATE = "name";
    private static final String START_HOUR_TEMPLATE = "startHour";
    private static final String START_MINUTE_TEMPLATE = "startMinute";
    private static final String START_TIME_TEMPLATE = "start";
    private static final String END_HOUR_TEMPLATE = "endHour";
    private static final String END_MINUTE_TEMPLATE = "endMinute";
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

    public String empty(ZonedDateTime time, boolean plan) {
        Map<String, Object> params = new HashMap<>();
        addActiveTab(params, plan);
        params.put(PLAN_TEMPLATE, plan);
        params.put(NAME_TEMPLATE, "");
        setTimes(params, time);
        params.put(DESCRIPTION_TEMPLATE, "");
        return templates.rendered(name, params);
    }

    private void addActiveTab(Map<String, Object> params, boolean plan) {
        params.put(ActiveTab.KEY, plan ? ActiveTab.PLAN : ActiveTab.HISTORY);
    }

    private void setTimes(Map<String, Object> params, ZonedDateTime time) {
        setTime(params, time, true);
        setTime(params, time.getHour() < MAX_HOUR ? time.plusHours(1) : time, false);
    }

    private void setTime(Map<String, Object> params, ZonedDateTime time, boolean start) {
        String hour = String.format(HOUR_MINUTE_FORMAT, time.getHour());
        String minute = String.format(HOUR_MINUTE_FORMAT, time.getMinute());
        String formattedTime = formatting.time(time.toInstant());
        if (start) {
            params.put(START_HOUR_TEMPLATE, hour);
            params.put(START_MINUTE_TEMPLATE, minute);
            params.put(START_TIME_TEMPLATE, formattedTime);
        } else {
            params.put(END_HOUR_TEMPLATE, hour);
            params.put(END_MINUTE_TEMPLATE, minute);
            params.put(END_TIME_TEMPLATE, formattedTime);
        }
    }

    public String withErrors(ZonedDateTime time, boolean plan, ValidateableName name, ValidateableTime startTime,
        ValidateableTime endTime, String description) {
        Map<String, Object> params = new HashMap<>();
        addActiveTab(params, plan);
        params.put(PLAN_TEMPLATE, plan);
        params.put(INVALID_NAME_TEMPLATE, !name.isValid());
        params.put(NAME_TEMPLATE, name.value());
        params.put(INVALID_START_TIME_TEMPLATE, !startTime.isValid());
        params.put(START_TIME_TEMPLATE, startTime.value());
        params.put(INVALID_END_TIME_TEMPLATE, !endTime.isValid());
        params.put(END_TIME_TEMPLATE, endTime.value());
        setTimes(params, time);
        params.put(DESCRIPTION_TEMPLATE, description);
        return templates.rendered(this.name, params);
    }

    public String filled(DescribedActivity activity, Function<Long, ZonedDateTime> timeTransformation) {
        Map<String, Object> params = new HashMap<>();
        boolean plan = !activity.activity.done;
        addActiveTab(params, plan);
        params.put(PLAN_TEMPLATE, plan);
        params.put(NAME_TEMPLATE, activity.activity.name);
        setTime(params, timeTransformation.apply(activity.activity.startDate), true);
        setTime(params, timeTransformation.apply(activity.activity.endDate), false);
        params.put(DESCRIPTION_TEMPLATE, activity.description);
        return templates.rendered(name, params);
    }
}
