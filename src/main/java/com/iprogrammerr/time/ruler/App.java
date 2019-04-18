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
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivities;
import com.iprogrammerr.time.ruler.model.day.DatabaseDays;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.respondent.ActivityRespondent;
import com.iprogrammerr.time.ruler.respondent.CalendarRespondent;
import com.iprogrammerr.time.ruler.respondent.ProfileRespondent;
import com.iprogrammerr.time.ruler.respondent.TodayRespondent;
import com.iprogrammerr.time.ruler.respondent.UsersRespondent;
import com.iprogrammerr.time.ruler.respondent.WelcomeRespondent;
import com.iprogrammerr.time.ruler.respondent.plan.DayPlanRespondent;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
        Views views = new HtmlViews(new File(root, "html"));
        ViewsTemplates viewsTemplates = new HtmlViewsTemplates(new File(root, "template"));
        Database database = new SqlDatabase(configuration.databaseUser(), configuration.databasePassword(),
            configuration.jdbcUrl());
        DatabaseSession session = new SqlDatabaseSession(database, new QueryTemplates());
        Users users = new DatabaseUsers(session);
        Days days = new DatabaseDays(session);
        Activities activities = new DatabaseActivities(session);

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

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        WelcomeRespondent welcomeRespondent = new WelcomeRespondent(views);
        TodayRespondent dashboardRespondent = new TodayRespondent(identity, viewsTemplates);
        UsersRespondent usersRespondent = new UsersRespondent(
            dashboardRespondent, views, viewsTemplates, users, hashing, emails, identity
        );
        CalendarRespondent calendarRespondent = new CalendarRespondent(identity, viewsTemplates, days);
        ProfileRespondent profileRespondent = new ProfileRespondent(identity, users, viewsTemplates);
        DayPlanRespondent dayPlanRespondent = new DayPlanRespondent(identity, viewsTemplates, activities, dateFormat);
        ActivityRespondent activityRespondent = new ActivityRespondent(identity, viewsTemplates);

        welcomeRespondent.init(app);
        dashboardRespondent.init(app);
        usersRespondent.init(app);
        calendarRespondent.init(app);
        profileRespondent.init(app);
        dayPlanRespondent.init(app);
        activityRespondent.init(app);

        //TODO handle exceptions
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            e.printStackTrace();
            String message = e.getMessage() == null ? "UNKNOWN" : e.getMessage();
            ctx.html(message);
        });
        //TODO proper pages per http code
        app.error(HttpURLConnection.HTTP_UNAUTHORIZED, ctx -> {
            ctx.html("You are not allowed to see this page");
        });
        app.start(configuration.port());
    }
}
