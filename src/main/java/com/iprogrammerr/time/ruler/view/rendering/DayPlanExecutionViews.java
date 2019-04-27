package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.ForViewActivity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayPlanExecutionViews {

    private static final String HISTORY_TEMPLATE = "history";
    private static final String PLANNED_ACTIVITIES_TEMPLATE = "plannedActivities";
    private static final String EXECUTED_ACTIVITIES_TEMPLATE = "executedActivities";
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

    public String view(boolean history, List<Activity> activities) {
        Map<String, Object> params = new HashMap<>();
        params.put(HISTORY_TEMPLATE, history);
        List<ForViewActivity> plannedActivities = new ArrayList<>();
        List<ForViewActivity> executedActivities = new ArrayList<>();
        activities.forEach(a -> {
            if (a.done) {
                executedActivities.add(new ForViewActivity(a, formatting));
            } else {
                plannedActivities.add(new ForViewActivity(a, formatting));
            }
        });
        params.put(PLANNED_ACTIVITIES_TEMPLATE, plannedActivities);
        params.put(EXECUTED_ACTIVITIES_TEMPLATE, executedActivities);
        return templates.rendered(name, params);
    }
}
