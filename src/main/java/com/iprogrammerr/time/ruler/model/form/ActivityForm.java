package com.iprogrammerr.time.ruler.model.form;

import com.iprogrammerr.time.ruler.model.TypedMap;

import java.util.List;
import java.util.Map;

public class ActivityForm {

    public final String name;
    public final String startTime;
    public final String endTime;
    public final String description;
    public final boolean done;

    public ActivityForm(String name, String startTime, String endTime, String description, boolean done) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.done = done;
    }

    public ActivityForm(String name, String startTime, String endTime, String description, String done) {
        this(name, startTime, endTime, description, done.equalsIgnoreCase(Boolean.toString(true)));
    }

    public static ActivityForm parsed(Map<String, List<String>> form) {
        TypedMap map = new TypedMap(form);
        return new ActivityForm(map.stringValue(FormParams.NAME), map.stringValue(FormParams.START_TIME),
            map.stringValue(FormParams.END_TIME), map.stringValue(FormParams.DESCRIPTION),
            map.stringValue(FormParams.DONE));
    }
}
