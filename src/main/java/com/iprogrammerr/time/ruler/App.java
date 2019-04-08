package com.iprogrammerr.time.ruler;

import com.iprogrammerr.time.ruler.database.Database;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabase;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.email.ConfigurableEmailServer;
import com.iprogrammerr.time.ruler.email.EmailServer;
import com.iprogrammerr.time.ruler.email.Emails;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.SessionIdentity;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.respondent.ProfileRespondent;
import com.iprogrammerr.time.ruler.respondent.TodayRespondent;
import com.iprogrammerr.time.ruler.respondent.UsersRespondent;
import com.iprogrammerr.time.ruler.respondent.WelcomeRespondent;
import com.iprogrammerr.time.ruler.view.HtmlViews;
import com.iprogrammerr.time.ruler.view.HtmlViewsTemplates;
import com.iprogrammerr.time.ruler.view.Views;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.net.HttpURLConnection;
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
        ViewsTemplates viewsTemplates = new HtmlViewsTemplates(new File(root, "template"));
        Database database = new SqlDatabase(configuration.databaseUser(), configuration.databasePassword(),
            configuration.jdbcUrl());
        DatabaseSession session = new SqlDatabaseSession(database, new QueryTemplates());
        Users users = new DatabaseUsers(session);
        EmailServer emailServer = new ConfigurableEmailServer(
            configuration.adminEmail(), configuration.adminPassword(),
            configuration.smtpHost(), configuration.smtpPort()
        );
        Emails emails = new Emails(
            emailServer, configuration.activationLinkBase(), configuration.signUpEmailSubject(),
            configuration.signUpEmailTemplate()
        );
        Hashing hashing = new Hashing();
        Identity<Long> identity = new SessionIdentity();

        WelcomeRespondent welcomeRespondent = new WelcomeRespondent(views);
        TodayRespondent dashboardRespondent = new TodayRespondent(identity, viewsTemplates);
        UsersRespondent usersRespondent = new UsersRespondent(
            dashboardRespondent, views, viewsTemplates, users, hashing, emails, identity
        );
        ProfileRespondent profileRespondent = new ProfileRespondent(identity, viewsTemplates);

        welcomeRespondent.init(app);
        dashboardRespondent.init(app);
        usersRespondent.init(app);
        profileRespondent.init(app);

        //TODO handle exceptions
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.html(e.getMessage());
            e.printStackTrace();
        });
        //TODO proper pages per http code
        app.error(HttpURLConnection.HTTP_UNAUTHORIZED, ctx -> {
            ctx.html("You are not allowed to see this page");
        });
        app.start(configuration.port());
    }
}
