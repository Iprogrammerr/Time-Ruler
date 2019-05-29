package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.rendering.DayActivity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DayPlanExecutionViews {

    private static final String DATE_TEMPLATE = "firstDate";
    private static final String HISTORY_TEMPLATE = "history";
    private static final String ACTIVITIES_TEMPLATE = "activities";
    private final ViewsTemplates templates;
    private final DateTimeFormatting formatting;
    private final String name;

    public DayPlanExecutionViews(ViewsTemplates templates, DateTimeFormatting formatting, String name) {
        this.templates = templates;
        this.formatting = formatting;
        this.name = name;
    }

    public DayPlanExecutionViews(ViewsTemplates templates, DateTimeFormatting formatting) {
        this(templates, formatting, "day-plan-execution");
    }

    public String view(Instant date, boolean history, List<Activity> activities, Function<Long, Instant> timeTransformation) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActiveTab.KEY, history ? ActiveTab.HISTORY : ActiveTab.TODAY);
        params.put(DATE_TEMPLATE, formatting.date(date));
        params.put(HISTORY_TEMPLATE, history);
        List<DayActivity> viewActivities = new ArrayList<>();
        activities.forEach(a -> viewActivities.add(new DayActivity(a, formatting, timeTransformation)));
        params.put(ACTIVITIES_TEMPLATE, viewActivities);
        return templates.rendered(name, params);
    }
}
