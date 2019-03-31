package com.iprogrammerr.time.ruler;

import com.iprogrammerr.time.ruler.view.HtmlViews;
import com.iprogrammerr.time.ruler.view.Views;
import io.javalin.Javalin;
import io.javalin.staticfiles.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class App {

    public static void main(String[] args) throws Exception {
        Configuration configuration = configuration(args);
        File root = new File(notCompiledResourcesPath());
        Views views = new HtmlViews(root);
        Javalin app = Javalin.create().enableStaticFiles(root.getPath(), Location.EXTERNAL);
        //TODO go with class path in production
        /*.enableStaticFiles(".", Location.CLASSPATH)*/
        app.get("/sign-in", ctx -> {
            ctx.html(views.view("sign-in"));
        });
        //TODO handle exceptions
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.html(e.getMessage());
            e.printStackTrace();
        });
        app.start(configuration.port());
    }

    private static String notCompiledResourcesPath() {
        String classPath = App.class.getResource(".").getPath();
        return classPath.substring(0, classPath.indexOf("target")) + "src/main/resources/view";
    }

    private static Configuration configuration(String[] args) throws Exception {
        try (InputStream is = args.length == 0 ? App.class.getResourceAsStream("/application.properties") : new FileInputStream(args[0])) {
            Properties properties = new Properties();
            properties.load(is);
            return new Configuration(properties);
        }
    }
}
