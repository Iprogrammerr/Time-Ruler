package com.iprogrammerr.time.ruler.model.rendering;

public enum ActiveTab {
    TODAY, PLAN, HISTORY, PROFILE;

    public static ActiveTab planHistory(boolean plan) {
        return plan ? PLAN : HISTORY;
    }
}
