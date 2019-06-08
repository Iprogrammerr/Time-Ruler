package com.iprogrammerr.time.ruler.matcher.rendering;

import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class ActivityErrorsViewMatcher extends TypeSafeMatcher<ActivityViews> {

    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final ZonedDateTime time;
    private final boolean plan;
    private final ValidateableName name;
    private final ValidateableTime startTime;
    private final ValidateableTime endTime;
    private final String description;

    public ActivityErrorsViewMatcher(ViewsTemplates templates, DateTimeFormatting formatting, ZonedDateTime time,
        boolean plan, ValidateableName name, ValidateableTime startTime, ValidateableTime endTime,
        String description) {
        this.templates = templates;
        this.formatting = formatting;
        this.time = time;
        this.plan = plan;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    @Override
    protected boolean matchesSafely(ActivityViews item) {
        return rendered(item.name).equals(item.withErrors(time, plan, name, startTime, endTime, description));
    }

    private String rendered(String viewName) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActiveTab.KEY, ActiveTab.planHistory(plan));
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
        return templates.rendered(viewName, params);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getClass().getSimpleName());
    }
}
