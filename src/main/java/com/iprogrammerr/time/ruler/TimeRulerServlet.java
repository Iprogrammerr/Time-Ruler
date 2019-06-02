package com.iprogrammerr.time.ruler;

import io.javalin.core.JavalinServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "TimeRuler", displayName = "TimeRuler", urlPatterns = "/")
public class TimeRulerServlet extends HttpServlet {

    private JavalinServlet app;

    @Override
    public void init() {
        try {
            app = App.forContainer().contextPath("time-ruler").createServlet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("Service = " + req.getRequestURI());
        app.service(req, resp);
    }
}
