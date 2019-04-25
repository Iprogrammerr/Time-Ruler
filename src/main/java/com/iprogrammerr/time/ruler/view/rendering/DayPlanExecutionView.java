package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.rendering.ForListActivity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayPlanExecutionView {

    private static final String HISTORY_TEMPLATE = "history";
    private static final String PLANNED_ACTIVITIES_TEMPLATE = "plannedActivities";
    private static final String EXECUTED_ACTIVITIES_TEMPLATE = "executedActivities";
    private final ViewsTemplates templates;
    private final String name;

    public DayPlanExecutionView(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public DayPlanExecutionView(ViewsTemplates templates) {
        this(templates, "day-plan-execution");
    }

    public String rendered(boolean history, List<ForListActivity> plannedActivities, List<ForListActivity> executedActivities) {
        Map<String, Object> params = new HashMap<>();
        params.put(HISTORY_TEMPLATE, history);
        params.put(PLANNED_ACTIVITIES_TEMPLATE, plannedActivities);
        params.put(EXECUTED_ACTIVITIES_TEMPLATE, executedActivities);
        return templates.rendered(name, params);
    }
}
