package com.iprogrammerr.time.ruler.mock;

import io.javalin.http.Context;
import io.javalin.http.util.ContextUtil;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;

public class JavalinContext {

    private final Context context;

    public JavalinContext(Context context) {
        this.context = context;
    }

    public JavalinContext() {
        this(ContextUtil.init(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class)));
    }

    public String read() {
        try (BufferedInputStream is = new BufferedInputStream(context.resultStream())) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer, 0, buffer.length);
            return new String(buffer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Context context() {
        return context;
    }
}
