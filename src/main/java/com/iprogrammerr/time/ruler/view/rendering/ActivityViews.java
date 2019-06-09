package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.date.FormattedTimes;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActivityViews {

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
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.planHistory(plan));
        params.put(TemplatesParams.PLAN, plan);
        params.put(TemplatesParams.NAME, "");
        formatting.times(time).put(params);
        params.put(TemplatesParams.DESCRIPTION, "");
        return templates.rendered(name, params);
    }

    public String withErrors(ZonedDateTime time, boolean plan, ValidateableName name, ValidateableTime startTime,
        ValidateableTime endTime, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.planHistory(plan));
        params.put(TemplatesParams.PLAN, plan);
        params.put(TemplatesParams.INVALID_NAME, !name.isValid());
        params.put(TemplatesParams.NAME, name.value());
        formatting.times(time).put(params);
        params.put(TemplatesParams.INVALID_START_TIME, !startTime.isValid());
        if (startTime.isValid()) {
            params.put(TemplatesParams.START_TIME, formatting.time(startTime.value()));
        }
        params.put(TemplatesParams.INVALID_END_TIME, !endTime.isValid());
        if (endTime.isValid()) {
            params.put(TemplatesParams.END_TIME, formatting.time(endTime.value()));
        }
        params.put(TemplatesParams.DESCRIPTION, description);
        return templates.rendered(this.name, params);
    }

    public String filled(DescribedActivity activity, Function<Long, ZonedDateTime> timeTransformation) {
        Map<String, Object> params = new HashMap<>();
        boolean plan = !activity.activity.done;
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.planHistory(plan));
        params.put(TemplatesParams.PLAN, plan);
        params.put(TemplatesParams.NAME, activity.activity.name);
        FormattedTimes times = formatting.times(timeTransformation.apply(activity.activity.startDate),
            timeTransformation.apply(activity.activity.endDate));
        times.put(params);
        params.put(TemplatesParams.DESCRIPTION, activity.description);
        return templates.rendered(name, params);
    }
}
