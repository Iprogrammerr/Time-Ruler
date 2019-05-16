package com.iprogrammerr.time.ruler.matcher;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.FoundActivity;
import com.iprogrammerr.time.ruler.model.rendering.Page;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

public class ActivitiesViewsMatcher extends TypeSafeMatcher<ActivitiesViews> {

    private final boolean plan;
    private final String pattern;
    private final int currentPage;
    private final List<Page> pages;
    private final List<Activity> activities;
    private final DateTimeFormatting formatting;
    private final Function<Long, Instant> dateTransformation;

    public ActivitiesViewsMatcher(boolean plan, String pattern, int currentPage,
        List<Page> pages, List<Activity> activities, DateTimeFormatting formatting,
        Function<Long, Instant> dateTransformation) {
        this.plan = plan;
        this.pattern = pattern;
        this.currentPage = currentPage;
        this.pages = pages;
        this.activities = activities;
        this.formatting = formatting;
        this.dateTransformation = dateTransformation;
    }

    public ActivitiesViewsMatcher(boolean plan, String pattern, int currentPage,
        List<Page> pages, List<Activity> activities, DateTimeFormatting formatting) {
        this(plan, pattern, currentPage, pages, activities, formatting, Instant::ofEpochMilli);
    }

    @Override
    protected boolean matchesSafely(ActivitiesViews item) {
        boolean matched;
        try {
            Document document = Jsoup.parse(item.view(plan, pattern, currentPage, pages, activities,
                dateTransformation));
            int activeIdx = plan ? 1 : 2;
            matched = document.getElementsByTag(TemplatesParam.BUTTON_TAG).get(activeIdx)
                .hasClass(TemplatesParam.ACTIVE_CLASS) &&
                document.getElementById(TemplatesParam.SEARCH_ID).attr(TemplatesParam.VALUE_ATTRIBUTE)
                    .equals(pattern) && matchesPages(document) && matchesActivities(document);
        } catch (Exception e) {
            e.printStackTrace();
            matched = false;
        }
        return matched;
    }

    private boolean matchesPages(Document document) {
        boolean matched;
        Elements pagesA = document.getElementsByTag(TemplatesParam.A_TAG);
        matched = pages.isEmpty() && pagesA.isEmpty();
        if (!matched) {
            for (int i = 0; i < pages.size(); i++) {
                Page p = pages.get(i);
                Element a = pagesA.get(i);
                matched = a.attr(TemplatesParam.HREF_ATTRIBUTE).equals(p.url) &&
                    a.text().equals(String.valueOf(p.number));
                if ((i + 1) == currentPage) {
                    matched = a.hasClass(TemplatesParam.ACTIVE_CLASS);
                }
                if (!matched) {
                    break;
                }
            }
        }
        return matched;
    }

    private boolean matchesActivities(Document document) {
        boolean matched;
        Elements activitiesLi = document.getElementsByTag(TemplatesParam.LI_TAG);
        matched = activities.isEmpty() && activitiesLi.isEmpty();
        if (!matched) {
            for (int i = 0; i < activities.size(); i++) {
                Activity a = activities.get(i);
                FoundActivity fa = new FoundActivity(a, formatting, dateTransformation);
                Element li = activitiesLi.get(i);
                Elements divs = li.getElementsByTag(TemplatesParam.DIV_TAG);
                matched = li.attr(TemplatesParam.ID_ATTRIBUTE).equals(String.valueOf(a.id)) &&
                    divs.get(0).text().equals(fa.date) && divs.get(1).text().equals(fa.name);
                if (!matched) {
                    break;
                }
            }
        }
        return matched;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(ActivitiesViewsMatcher.class.getSimpleName());
    }
}
