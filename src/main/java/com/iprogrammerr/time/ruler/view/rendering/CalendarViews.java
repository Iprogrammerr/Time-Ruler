package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.date.ZonedDateTimeBuilder;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.rendering.CalendarDay;
import com.iprogrammerr.time.ruler.model.rendering.DayState;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CalendarViews {

    private static final long DAY_SECONDS = TimeUnit.DAYS.toSeconds(1);
    private final ViewsTemplates templates;
    private final String name;

    public CalendarViews(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public CalendarViews(ViewsTemplates templates) {
        this(templates, "calendar");
    }

    public String pastView(Params params) {
        return view(params, true);
    }

    public String futureView(Params params) {
        return view(params, false);
    }

    private String view(Params viewParams, boolean fromPast) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.PLAN, viewParams.plan);
        params.put(ActiveTab.KEY, ActiveTab.planHistory(viewParams.plan));
        params.put(TemplatesParams.PREV, viewParams.hasPrevious);
        params.put(TemplatesParams.NEXT, viewParams.hasNext);
        params.put(TemplatesParams.MONTH, viewParams.firstDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        params.put(TemplatesParams.YEAR, viewParams.firstDate.getYear());
        int firstDayOffset = monthStart(viewParams.firstDate).getDayOfWeek().getValue() - 1;
        params.put(TemplatesParams.FIRST_DAY_OFFSET, firstDayOffset);
        params.put(TemplatesParams.DAYS, calendarDays(viewParams.days, viewParams.firstDate, viewParams.currentDate,
            fromPast));
        return templates.rendered(name, params);
    }

    private List<CalendarDay> calendarDays(List<Long> days, ZonedDateTime firstDate, long currentDate,
        boolean fromPast) {
        int daysNumber = firstDate.toLocalDate().lengthOfMonth();
        List<CalendarDay> calendarDays = new ArrayList<>(daysNumber);
        int plannedDayIdx = 0;
        long monthStart = monthStart(firstDate).toEpochSecond();
        long firstDateSeconds = firstDate.toEpochSecond();
        for (int i = 0; i < daysNumber; i++) {
            long dayStart = monthStart + (DAY_SECONDS * i);
            long dayEnd = dayStart + (DAY_SECONDS - 1);
            long plannedDay = plannedDayIdx < days.size() ? days.get(plannedDayIdx) : -1;
            DayState state = fromPast ?
                dayStateForPast(dayStart, dayEnd, plannedDay, currentDate, firstDateSeconds) :
                dayStateForFuture(dayStart, dayEnd, plannedDay, currentDate);
            if (state == DayState.PLANNED) {
                plannedDayIdx++;
            }
            calendarDays.add(new CalendarDay(i + 1, state));
        }
        return calendarDays;
    }

    private ZonedDateTime monthStart(ZonedDateTime currentDate) {
        return new ZonedDateTimeBuilder()
            .withYear(currentDate.getYear()).withMonth(currentDate.getMonthValue())
            .withDay(1).withHour(0).withSecond(0)
            .build();
    }

    private DayState dayStateForFuture(long dayStart, long dayEnd, long plannedDay, long currentDay) {
        DayState state;
        if (isBetween(dayStart, dayEnd, plannedDay)) {
            state = DayState.PLANNED;
        } else if (currentDay > dayEnd) {
            state = DayState.NOT_AVAILABLE;
        } else if (isBetween(dayStart, dayEnd, currentDay)) {
            state = DayState.CURRENT;
        } else {
            state = DayState.AVAILABLE;
        }
        return state;
    }

    private DayState dayStateForPast(long dayStart, long dayEnd, long plannedDay, long currentDay, long firstDay) {
        DayState state;
        if (isBetween(dayStart, dayEnd, plannedDay)) {
            state = DayState.PLANNED;
        } else if (isBetween(dayStart, dayEnd, currentDay)) {
            state = DayState.CURRENT;
        } else if (dayEnd > firstDay && dayEnd <= currentDay) {
            state = DayState.AVAILABLE;
        } else {
            state = DayState.NOT_AVAILABLE;
        }
        return state;
    }

    private boolean isBetween(long start, long end, long value) {
        return value >= start && end >= value;
    }

    public static class Params {

        public final boolean plan;
        public final boolean hasPrevious;
        public final boolean hasNext;
        public final long currentDate;
        public final ZonedDateTime firstDate;
        public final List<Long> days;

        public Params(boolean plan, boolean hasPrevious, boolean hasNext, long currentDate,
            ZonedDateTime firstDate, List<Long> days) {
            this.plan = plan;
            this.hasPrevious = hasPrevious;
            this.hasNext = hasNext;
            this.currentDate = currentDate;
            this.firstDate = firstDate;
            this.days = days;
        }
    }
}
