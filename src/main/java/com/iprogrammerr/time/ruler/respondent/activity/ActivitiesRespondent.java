package com.iprogrammerr.time.ruler.respondent.activity;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.activity.ActivitiesSearch;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.rendering.ForViewPage;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;
import io.javalin.Context;
import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivitiesRespondent implements GroupedRespondent {

    private static final String PLAN_PARAM = "plan";
    private static final String PAGE_PARAM = "page";
    private static final String PATTERN_PARAM = "pattern";
    private static final String ACTIVITIES = "activities";
    private final Identity<Long> identity;
    private final ActivitiesViews views;
    private final ActivitiesSearch search;
    private final ServerClientDates dates;
    private final int pageSize;
    private String withGroupPath;

    public ActivitiesRespondent(Identity<Long> identity, ActivitiesViews views, ActivitiesSearch search, ServerClientDates dates,
        int pageSize) {
        this.identity = identity;
        this.views = views;
        this.search = search;
        this.dates = dates;
        this.pageSize = pageSize;
        this.withGroupPath = "";
    }

    public ActivitiesRespondent(Identity<Long> identity, ActivitiesViews views, ActivitiesSearch search, ServerClientDates dates) {
        this(identity, views, search, dates, 50);
    }

    @Override
    public void init(String group, Javalin app) {
        withGroupPath = group + ACTIVITIES;
        app.get(withGroupPath, this::showPage);
    }

    private void showPage(Context context) {
        long userId = identity.value(context.req);
        int page = context.queryParam(PAGE_PARAM, Integer.class, Integer.toString(1)).get();
        if (page < 1) {
            page = 1;
        }
        boolean plan = context.queryParam(PLAN_PARAM, Boolean.class, Boolean.toString(true)).get();
        String pattern = context.queryParam(PATTERN_PARAM, "");
        List<Activity> activities;
        int allPagesNumber;
        if (pattern.isEmpty()) {
            activities = search.userActivities(userId, (page - 1) * pageSize, pageSize, false);
            allPagesNumber = pagesNumber(search.matches(userId));
        } else {
            activities = search.userActivities(userId, pattern, (page - 1) * pageSize, pageSize, false);
            allPagesNumber = pagesNumber(search.matches(userId, pattern));
        }
        context.html(views.view(plan, pattern, page, pages(allPagesNumber, plan, pattern, context.queryParamMap()),
            activities, d -> dates.clientDate(context.req, d)));
    }

    private int pagesNumber(int records) {
        return (int) Math.ceil(((double) records) / pageSize);
    }

    private List<ForViewPage> pages(int size, boolean plan, String pattern, Map<String, List<String>> currentParams) {
        List<ForViewPage> pages = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int page = i + 1;
            UrlQueryBuilder queryBuilder = new UrlQueryBuilder().put(PLAN_PARAM, plan).put(PAGE_PARAM, page);
            if (pattern.length() > 0) {
                queryBuilder.put(PATTERN_PARAM, pattern);
            }
            currentParams.forEach((k, v) -> v.forEach(s -> queryBuilder.put(k, s)));
            pages.add(new ForViewPage(queryBuilder.build(ACTIVITIES), i + 1));
        }
        return pages;
    }
}
