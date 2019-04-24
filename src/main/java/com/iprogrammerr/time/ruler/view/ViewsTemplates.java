package com.iprogrammerr.time.ruler.view;

import java.util.Map;

public interface ViewsTemplates {

    String rendered(String name, Map<String, Object> params);

    String rendered(String name);
}
