package com.iprogrammerr.time.ruler;

import io.javalin.Javalin;
import io.javalin.staticfiles.Location;

public class App {

    public static void main(String[] args) {
        Javalin app = Javalin.create().enableStaticFiles(developmentResources(), Location.EXTERNAL)
        //TODO go with class path in production
        /*.enableStaticFiles(".", Location.CLASSPATH)*/
        .start(8080);
    }

    private static String developmentResources() {
        String classPath = App.class.getResource(".").getPath();
        return classPath.substring(0, classPath.indexOf("target")) + "src/main/resources";
    }
}
