package com.iprogrammerr.time.ruler.model.error;

import java.util.Locale;

public interface ErrorsTranslation {
    String translated(ErrorCode code, Locale locale);
}
