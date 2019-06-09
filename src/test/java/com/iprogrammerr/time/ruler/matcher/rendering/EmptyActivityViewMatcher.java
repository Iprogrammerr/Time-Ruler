package com.iprogrammerr.time.ruler.matcher.rendering;

import com.iprogrammerr.time.ruler.matcher.ViewsMismatchDescription;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.date.FormattedTimes;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class EmptyActivityViewMatcher extends TypeSafeMatcher<ActivityViews> {

    private static final int MAX_HOUR = 23;
    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final ZonedDateTime time;
    private final boolean plan;

    public EmptyActivityViewMatcher(ViewsTemplates templates, DateTimeFormatting formatting, ZonedDateTime time,
        boolean plan) {
        this.templates = templates;
        this.formatting = formatting;
        this.time = time;
        this.plan = plan;
    }

    @Override
    protected boolean matchesSafely(ActivityViews item) {
        return rendered(item.name).equals(item.empty(time, plan));
    }

    private String rendered(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.planHistory(plan));
        params.put(TemplatesParams.PLAN, plan);
        FormattedTimes times = formatting.times(time);
        times.put(params);
        return templates.rendered(name, params);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getClass().getSimpleName());
    }

    @Override
    protected void describeMismatchSafely(ActivityViews item, Description mismatchDescription) {
        new ViewsMismatchDescription(item.empty(time, plan), rendered(item.name)).append(mismatchDescription);
    }
}
