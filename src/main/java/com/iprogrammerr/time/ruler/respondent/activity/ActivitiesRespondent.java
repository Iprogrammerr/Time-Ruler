package com.iprogrammerr.time.ruler.respondent.activity;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.activity.ActivitiesSearch;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.model.rendering.Page;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivitiesRespondent {

    public static final String ACTIVITIES = "activities";
    private final Identity<Long> identity;
    private final ActivitiesViews views;
    private final ActivitiesSearch search;
    private final ServerClientDates dates;
    private final int pageSize;

    public ActivitiesRespondent(Identity<Long> identity, ActivitiesViews views, ActivitiesSearch search,
        ServerClientDates dates,
        int pageSize) {
        this.identity = identity;
        this.views = views;
        this.search = search;
        this.dates = dates;
        this.pageSize = pageSize;
    }

    public ActivitiesRespondent(Identity<Long> identity, ActivitiesViews views, ActivitiesSearch search,
        ServerClientDates dates) {
        this(identity, views, search, dates, 50);
    }

    public HtmlResponse page(HttpServletRequest request, int page, boolean plan, String pattern,
        Map<String, List<String>> currentParams) {
        long userId = identity.value(request);
        if (page < 1) {
            page = 1;
        }
        List<Activity> activities;
        int allPagesNumber;
        if (pattern.isEmpty()) {
            activities = search.userActivities(userId, (page - 1) * pageSize, pageSize, false);
            allPagesNumber = pagesNumber(search.matches(userId));
        } else {
            activities = search.userActivities(userId, pattern, (page - 1) * pageSize, pageSize, false);
            allPagesNumber = pagesNumber(search.matches(userId, pattern));
        }
        return new HtmlResponse(views.view(plan, pattern, page, pages(allPagesNumber, plan, pattern,
            currentParams), activities, d -> dates.clientDate(request, d)));
    }

    private int pagesNumber(int records) {
        return (int) Math.ceil(((double) records) / pageSize);
    }

    private List<Page> pages(int size, boolean plan, String pattern, Map<String, List<String>> currentParams) {
        List<Page> pages = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int page = i + 1;
            UrlQueryBuilder queryBuilder = new UrlQueryBuilder();
            currentParams.forEach((k, v) -> v.forEach(s -> queryBuilder.put(k, s)));
            queryBuilder.put(QueryParams.PLAN, plan)
                .put(QueryParams.PAGE, page);
            if (pattern.length() > 0) {
                queryBuilder.put(QueryParams.PATTERN, pattern);
            }
            pages.add(new Page(queryBuilder.build(ACTIVITIES), page));
        }
        return pages;
    }
}
