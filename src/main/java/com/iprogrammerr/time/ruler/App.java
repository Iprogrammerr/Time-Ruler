package com.iprogrammerr.time.ruler;

import com.iprogrammerr.time.ruler.database.Database;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabase;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.email.ConfigurableEmailServer;
import com.iprogrammerr.time.ruler.email.EmailServer;
import com.iprogrammerr.time.ruler.model.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Users;
import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.respondent.UsersRespondent;
import com.iprogrammerr.time.ruler.respondent.WelcomeRespondent;
import com.iprogrammerr.time.ruler.view.HtmlViews;
import com.iprogrammerr.time.ruler.view.Views;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class App {

    //TODO develop/production profiles?
    public static void main(String[] args) throws Exception {
        Configuration configuration = Configuration.fromCmd(args);
        File root = new File(configuration.resourcesPath());

        Javalin app = Javalin.create()
            .enableStaticFiles(root.getPath() + File.separator + "css", Location.EXTERNAL)
            .enableStaticFiles(root.getPath() + File.separator + "js", Location.EXTERNAL);

        TemplateEngine engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setCacheable(false);
        engine.setTemplateResolver(resolver);
        JavalinThymeleaf.configure(engine);
        app.get("test", ctx -> {
            Map<String, String> params = new HashMap<>();
            params.put("table", "TestC");
            ctx.render(configuration.resourcesPath() + File.separator + "template" + File.separator + "test.html",
                params);

        });
        Views views = new HtmlViews(new File(root, "html"));
        Database database = new SqlDatabase(configuration.databaseUser(), configuration.databasePassword(),
            configuration.jdbcUrl());
        DatabaseSession session = new SqlDatabaseSession(database, new QueryTemplates());
        Users users = new DatabaseUsers(session);
        EmailServer emailServer = new ConfigurableEmailServer(
            configuration.adminEmail(), configuration.adminPassword(),
            configuration.smtpHost(), configuration.smtpPort()
        );
        Hashing hashing = new Hashing();

        Respondent welcomeRespondent = new WelcomeRespondent(views);
        Respondent usersRespondent = new UsersRespondent(views, users, hashing, emailServer);

        welcomeRespondent.init(app);
        usersRespondent.init(app);

        //TODO handle exceptions
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.html(e.getMessage());
            e.printStackTrace();
        });
        app.start(configuration.port());
    }
}
