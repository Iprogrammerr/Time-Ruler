package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.rendering.FoundActivity;
import com.iprogrammerr.time.ruler.model.rendering.Page;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ActivitiesViews {

    public static final String CURRENT_PAGE_TEMPLATE = "currentPage";
    public static final String PAGES_TEMPLATE = "pages";
    public static final String ACTIVITIES_TEMPLATE = "activities";
    public static final String PATTERN_TEMPLATE = "pattern";
    public final String name;
    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;

    public ActivitiesViews(ViewsTemplates templates, DateTimeFormatting formatting, String name) {
        this.templates = templates;
        this.formatting = formatting;
        this.name = name;
    }

    public ActivitiesViews(ViewsTemplates templates, DateTimeFormatting formatting) {
        this(templates, formatting, "activities");
    }

    public String view(boolean plan, String pattern, int currentPage, List<Page> pages, List<Activity> activities,
        Function<Long, Instant> dateTransformation) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActiveTab.KEY, ActiveTab.planHistory(plan));
        params.put(PATTERN_TEMPLATE, pattern);
        params.put(CURRENT_PAGE_TEMPLATE, currentPage);
        params.put(PAGES_TEMPLATE, pages);
        List<FoundActivity> viewActivities = new ArrayList<>(activities.size());
        activities.forEach(a -> viewActivities.add(new FoundActivity(a, formatting, dateTransformation)));
        params.put(ACTIVITIES_TEMPLATE, viewActivities);
        return templates.rendered(name, params);
    }
}
