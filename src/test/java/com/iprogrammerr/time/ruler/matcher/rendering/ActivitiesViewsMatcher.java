package com.iprogrammerr.time.ruler.matcher.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.rendering.FoundActivity;
import com.iprogrammerr.time.ruler.model.rendering.Page;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ActivitiesViewsMatcher extends TypeSafeMatcher<ActivitiesViews> {

    private final ViewsTemplates templates;
    private final boolean plan;
    private final String pattern;
    private final int currentPage;
    private final List<Page> pages;
    private final List<Activity> activities;
    private final DateTimeFormatting formatting;
    private final Function<Long, Instant> dateTransformation;

    public ActivitiesViewsMatcher(ViewsTemplates templates, boolean plan, String pattern, int currentPage,
        List<Page> pages, List<Activity> activities, DateTimeFormatting formatting,
        Function<Long, Instant> dateTransformation) {
        this.templates = templates;
        this.plan = plan;
        this.pattern = pattern;
        this.currentPage = currentPage;
        this.pages = pages;
        this.activities = activities;
        this.formatting = formatting;
        this.dateTransformation = dateTransformation;
    }

    public ActivitiesViewsMatcher(ViewsTemplates templates, boolean plan, String pattern, int currentPage,
        List<Page> pages, List<Activity> activities, DateTimeFormatting formatting) {
        this(templates, plan, pattern, currentPage, pages, activities, formatting, Instant::ofEpochMilli);
    }

    @Override
    protected boolean matchesSafely(ActivitiesViews item) {
        boolean matched;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(ActiveTab.KEY, ActiveTab.planHistory(plan));
            params.put(ActivitiesViews.PATTERN_TEMPLATE, pattern);
            params.put(ActivitiesViews.CURRENT_PAGE_TEMPLATE, currentPage);
            List<FoundActivity> viewActivities = new ArrayList<>(activities.size());
            activities.forEach(a -> viewActivities.add(new FoundActivity(a, formatting, dateTransformation)));
            params.put(ActivitiesViews.ACTIVITIES_TEMPLATE, viewActivities);
            params.put(ActivitiesViews.PAGES_TEMPLATE, pages);
            matched = templates.rendered(item.name, params).equals(item.view(plan, pattern, currentPage,
                pages, activities, dateTransformation));
        } catch (Exception e) {
            e.printStackTrace();
            matched = false;
        }
        return matched;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getClass().getSimpleName());
    }
}
