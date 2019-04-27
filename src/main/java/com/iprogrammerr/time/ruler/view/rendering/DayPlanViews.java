package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ForViewActivity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayPlanViews {

    private static final String DATE_TEMPLATE = "date";
    private static final String ACTIVITIES_TEMPLATE = "activities";
    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final String name;

    public DayPlanViews(ViewsTemplates templates, DateTimeFormatting formatting, String name) {
        this.templates = templates;
        this.formatting = formatting;
        this.name = name;
    }

    public DayPlanViews(ViewsTemplates templates, DateTimeFormatting formatting) {
        this(templates, formatting, "day-plan");
    }

    public String view(Instant date, List<Activity> activities) {
        Map<String, Object> params = new HashMap<>();
        params.put(DATE_TEMPLATE, formatting.dateFromSeconds(date.getEpochSecond()));
        List<ForViewActivity> viewActivities = new ArrayList<>(activities.size());
        activities.forEach(a -> viewActivities.add(new ForViewActivity(a, formatting)));
        params.put(ACTIVITIES_TEMPLATE, viewActivities);
        return templates.rendered(name, params);
    }
}
