package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ForViewActivity;
import com.iprogrammerr.time.ruler.model.rendering.ForViewPage;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ActivitiesViews {

    private static final String PLAN_TEMPLATE = "plan";
    private static final String CURRENT_PAGE_TEMPLATE = "currentPage";
    private static final String PAGES_TEMPLATE = "pages";
    private static final String ACTIVITIES_TEMPLATE = "activities";
    private static final String PATTERN_TEMPLATE = "pattern";
    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final String name;

    public ActivitiesViews(ViewsTemplates templates, DateTimeFormatting formatting, String name) {
        this.templates = templates;
        this.formatting = formatting;
        this.name = name;
    }

    public ActivitiesViews(ViewsTemplates templates, DateTimeFormatting formatting) {
        this(templates, formatting, "activities");
    }

    public String view(boolean plan, String pattern, int currentPage, List<ForViewPage> pages, List<Activity> activities,
        Function<Long, Instant> dateTransformation) {
        Map<String, Object> params = new HashMap<>();
        params.put(PLAN_TEMPLATE, plan);
        params.put(PATTERN_TEMPLATE, pattern);
        params.put(CURRENT_PAGE_TEMPLATE, currentPage);
        params.put(PAGES_TEMPLATE, pages);
        List<ForViewActivity> viewActivities = new ArrayList<>(activities.size());
        activities.forEach(a -> viewActivities.add(new ForViewActivity(a, formatting, dateTransformation)));
        params.put(ACTIVITIES_TEMPLATE, viewActivities);
        return templates.rendered(name, params);
    }
}
