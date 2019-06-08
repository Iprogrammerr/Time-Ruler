package com.iprogrammerr.time.ruler.model.date;

import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;

import java.util.Map;

public class FormattedTimes {

    public final String startHour;
    public final String startMinute;
    public final String startTime;
    public final String endHour;
    public final String endMinute;
    public final String endTime;

    public FormattedTimes(String startHour, String startMinute, String startTime, String endHour, String endMinute,
        String endTime) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.startTime = startTime;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.endTime = endTime;
    }

    public void put(Map<String, Object> params, String startHourKey, String startMinuteKey, String startTimeKey,
        String endHourKey, String endMinuteKey, String endTimeKey) {
        params.put(startHourKey, startHour);
        params.put(startMinuteKey, startMinute);
        params.put(startTimeKey, startTime);
        params.put(endHourKey, endHour);
        params.put(endMinuteKey, endMinute);
        params.put(endTimeKey, endTime);
    }

    public void put(Map<String, Object> params) {
        put(params, ActivityViews.START_HOUR_TEMPLATE, ActivityViews.START_MINUTE_TEMPLATE,
            ActivityViews.START_TIME_TEMPLATE, ActivityViews.END_HOUR_TEMPLATE,
            ActivityViews.END_MINUTE_TEMPLATE, ActivityViews.END_TIME_TEMPLATE);
    }
}
