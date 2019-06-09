package com.iprogrammerr.time.ruler.matcher.rendering;

import com.iprogrammerr.time.ruler.matcher.ViewsMismatchDescription;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.rendering.DayActivity;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionViews;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DayPlanExecutionViewsMatcher extends TypeSafeMatcher<DayPlanExecutionViews> {

    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final Instant date;
    private final boolean history;
    private final List<Activity> activities;
    private final Function<Long, Instant> timeTransformation;

    public DayPlanExecutionViewsMatcher(ViewsTemplates templates, DateTimeFormatting formatting, Instant date,
        boolean history, List<Activity> activities, Function<Long, Instant> timeTransformation) {
        this.templates = templates;
        this.formatting = formatting;
        this.date = date;
        this.history = history;
        this.activities = activities;
        this.timeTransformation = timeTransformation;
    }

    @Override
    protected boolean matchesSafely(DayPlanExecutionViews item) {
        return rendered(item.name).equals(item.view(date, history, activities, timeTransformation));
    }

    private String rendered(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.ACTIVE_TAB, history ? ActiveTab.HISTORY : ActiveTab.TODAY);
        params.put(TemplatesParams.DATE, formatting.date(date));
        params.put(TemplatesParams.HISTORY, history);
        List<DayActivity> viewActivities = new ArrayList<>();
        activities.forEach(a -> viewActivities.add(new DayActivity(a, formatting, timeTransformation)));
        params.put(TemplatesParams.ACTIVITIES, viewActivities);
        return templates.rendered(name, params);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getClass().getSimpleName());
    }

    @Override
    protected void describeMismatchSafely(DayPlanExecutionViews item, Description mismatchDescription) {
        new ViewsMismatchDescription(item.view(date, history, activities, timeTransformation),
            rendered(item.name)).append(mismatchDescription);
    }
}
