package com.iprogrammerr.time.ruler.matcher.rendering;

import com.iprogrammerr.time.ruler.matcher.ViewsMismatchDescription;
import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
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
import java.util.function.Function;

public class FilledActivityViewMatcher extends TypeSafeMatcher<ActivityViews> {

    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final DescribedActivity activity;
    private final Function<Long, ZonedDateTime> timeTransformation;

    public FilledActivityViewMatcher(ViewsTemplates templates, DateTimeFormatting formatting,
        DescribedActivity activity, Function<Long, ZonedDateTime> timeTransformation) {
        this.templates = templates;
        this.formatting = formatting;
        this.activity = activity;
        this.timeTransformation = timeTransformation;
    }

    @Override
    protected boolean matchesSafely(ActivityViews item) {
        return rendered(item.name).equals(item.filled(activity, timeTransformation));
    }

    private String rendered(String name) {
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

    @Override
    public void describeTo(Description description) {
        description.appendText(getClass().getSimpleName());
    }

    @Override
    protected void describeMismatchSafely(ActivityViews item, Description mismatchDescription) {
        new ViewsMismatchDescription(item.filled(activity, timeTransformation), rendered(item.name))
            .append(mismatchDescription);
    }
}
