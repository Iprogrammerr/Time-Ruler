package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.rendering.ForViewActivity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiesViews {

    private static final String PLAN_TEMPLATE = "plan";
    private static final String ACTIVITIES_TEMPLATE = "activities";
    private final ViewsTemplates templates;
    private final String name;

    public ActivitiesViews(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public ActivitiesViews(ViewsTemplates templates) {
        this(templates, "activities");
    }

    //TODO render with proper params
    public String view(boolean plan, List<ForViewActivity> activities) {
        Map<String, Object> params = new HashMap<>();
        params.put(PLAN_TEMPLATE, plan);
        params.put(ACTIVITIES_TEMPLATE, activities);
        return templates.rendered(name, params);
    }
}
