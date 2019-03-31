package com.iprogrammerr.time.ruler;

import com.iprogrammerr.time.ruler.view.HtmlViews;
import com.iprogrammerr.time.ruler.view.Views;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class App {

    //TODO develop/production profiles?
    public static void main(String[] args) throws Exception {
        Configuration configuration = Configuration.fromCmd(args);
        File root = new File(configuration.resourcesPath());
        Views views = new HtmlViews(new File(root, "html"));
        Javalin app = Javalin.create()
        .enableStaticFiles(root.getPath() + File.separator + "css", Location.EXTERNAL)
        .enableStaticFiles(root.getPath() + File.separator + "js", Location.EXTERNAL);
        app.get("/sign-in", ctx -> {
            ctx.html(views.view("sign-in"));
        });
        app.get("/", ctx -> {
            ctx.html(views.view("index"));
        });
        TemplateEngine engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setCacheable(false);
        engine.setTemplateResolver(resolver);
        JavalinThymeleaf.configure(engine);
        app.get("test", ctx -> {
            Map<String, String> params = new HashMap<>();
            params.put("name", "TestC");
            ctx
            .render(configuration.resourcesPath() + File.separator + "template" + File.separator + "test.html", params);

        });
        //TODO handle exceptions
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.html(e.getMessage());
            e.printStackTrace();
        });
        app.start(configuration.port());
    }
}
