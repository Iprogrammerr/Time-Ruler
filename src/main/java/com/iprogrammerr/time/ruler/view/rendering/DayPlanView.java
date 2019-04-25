package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.rendering.ForListActivity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayPlanView {

    private static final String DATE_TEMPLATE = "date";
    private static final String ACTIVITIES_TEMPLATE = "activities";
    private final ViewsTemplates templates;
    private final String name;

    public DayPlanView(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public DayPlanView(ViewsTemplates templates) {
        this(templates, "day-plan");
    }

    public String rendered(String date, List<ForListActivity> activities) {
        Map<String, Object> params = new HashMap<>();
        params.put(DATE_TEMPLATE, date);
        params.put(ACTIVITIES_TEMPLATE, activities);
        return templates.rendered(name, params);
    }
}
