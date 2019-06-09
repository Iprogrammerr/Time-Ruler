package com.iprogrammerr.time.ruler.matcher.rendering;

import com.iprogrammerr.time.ruler.matcher.ViewsMismatchDescription;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.rendering.DayActivity;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanViews;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DayPlanViewsMatcher extends TypeSafeMatcher<DayPlanViews> {

    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final Instant date;
    private final Function<Long, Instant> timeTransformation;
    private final List<Activity> activities;

    public DayPlanViewsMatcher(ViewsTemplates templates, DateTimeFormatting formatting, Instant date,
        Function<Long, Instant> timeTransformation, List<Activity> activities) {
        this.templates = templates;
        this.formatting = formatting;
        this.date = date;
        this.timeTransformation = timeTransformation;
        this.activities = activities;
    }

    @Override
    protected boolean matchesSafely(DayPlanViews item) {
        return rendered(item.name).equals(item.view(date, timeTransformation, activities));
    }

    private String rendered(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.PLAN);
        params.put(TemplatesParams.DATE, formatting.date(date));
        List<DayActivity> viewActivities = new ArrayList<>(activities.size());
        activities.forEach(a -> viewActivities.add(new DayActivity(a, formatting, timeTransformation)));
        params.put(TemplatesParams.ACTIVITIES, viewActivities);
        return templates.rendered(name, params);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getClass().getSimpleName());
    }

    @Override
    protected void describeMismatchSafely(DayPlanViews item, Description mismatchDescription) {
        new ViewsMismatchDescription(item.view(date, timeTransformation, activities),
            rendered(item.name)).append(mismatchDescription);
    }
}
