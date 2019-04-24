package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.rendering.CalendarDay;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarView {

    private static final String PLAN_TEMPLATE = "plan";
    private static final String PREV_TEMPLATE = "prev";
    private static final String NEXT_TEMPLATE = "next";
    private static final String MONTH_TEMPLATE = "month";
    private static final String YEAR_TEMPLATE = "year";
    private static final String DAYS_TEMPLATE = "days";
    private final ViewsTemplates templates;
    private final String name;

    public CalendarView(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public CalendarView(ViewsTemplates templates) {
        this(templates, "calendar");
    }

    public String rendered(boolean plan, boolean hasPrevious, boolean hasNext, String month, int year, List<CalendarDay> days) {
        Map<String, Object> params = new HashMap<>();
        params.put(PLAN_TEMPLATE, plan);
        params.put(PREV_TEMPLATE, hasPrevious);
        params.put(NEXT_TEMPLATE, hasNext);
        params.put(MONTH_TEMPLATE, month);
        params.put(YEAR_TEMPLATE, year);
        params.put(DAYS_TEMPLATE, days);
        return templates.rendered(name, params);
    }
}
