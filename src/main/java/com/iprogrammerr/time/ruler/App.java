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
import com.iprogrammerr.time.ruler.model.Messages;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivities;
import com.iprogrammerr.time.ruler.model.activity.DatabaseDates;
import com.iprogrammerr.time.ruler.model.activity.Dates;
import com.iprogrammerr.time.ruler.model.date.DateParsing;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.description.DatabaseDescriptions;
import com.iprogrammerr.time.ruler.model.description.Descriptions;
import com.iprogrammerr.time.ruler.model.session.SessionIdentity;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.respondent.activity.ActivitiesRespondent;
import com.iprogrammerr.time.ruler.respondent.activity.ActivityRespondent;
import com.iprogrammerr.time.ruler.respondent.CalendarRespondent;
import com.iprogrammerr.time.ruler.respondent.ProfileRespondent;
import com.iprogrammerr.time.ruler.respondent.TodayRespondent;
import com.iprogrammerr.time.ruler.respondent.WelcomeRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningInRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningOutRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningUpRespondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanRespondent;
import com.iprogrammerr.time.ruler.view.HtmlViews;
import com.iprogrammerr.time.ruler.view.HtmlViewsTemplates;
import com.iprogrammerr.time.ruler.view.Views;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;
import com.iprogrammerr.time.ruler.view.rendering.CalendarViews;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionViews;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanViews;
import com.iprogrammerr.time.ruler.view.rendering.SigningInViews;
import io.javalin.BadRequestResponse;
import io.javalin.Javalin;
import io.javalin.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class App {

    public static void main(String... args) throws Exception {
        Configuration configuration = Configuration.fromCmd(args);
        File root = new File(configuration.resourcesPath());

        Javalin app = Javalin.create()
            .enableStaticFiles(root.getPath() + File.separator + "css", Location.EXTERNAL)
            .enableStaticFiles(root.getPath() + File.separator + "js", Location.EXTERNAL);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(dateFormat.getTimeZone());
        DateTimeFormatting formatting = new DateTimeFormatting(dateFormat, timeFormat);
        DateParsing dateParsing = new DateParsing();
        LimitedDate limitedDate = new LimitedDate(dateParsing);
        ServerClientDates serverClientDates = new ServerClientDates();

        TemplateEngine engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setCacheable(false);
        engine.setTemplateResolver(resolver);
        Messages messages = new Messages();
        messages.init("messages.properties");
        engine.setMessageResolver(messages);
        Views views = new HtmlViews(new File(root, "html"));
        ViewsTemplates viewsTemplates = new HtmlViewsTemplates(new File(root, "template"), engine);

        SigningInViews signingInView = new SigningInViews(viewsTemplates);
        CalendarViews calendarView = new CalendarViews(viewsTemplates);
        DayPlanExecutionViews dayPlanExecutionView = new DayPlanExecutionViews(viewsTemplates, formatting);
        DayPlanViews dayPlanView = new DayPlanViews(viewsTemplates, formatting);
        ActivityViews activityView = new ActivityViews(viewsTemplates, formatting);
        ActivitiesViews activitiesViews = new ActivitiesViews(viewsTemplates);

        Database database = new SqlDatabase(configuration.databaseUser(), configuration.databasePassword(),
            configuration.jdbcUrl());
        DatabaseSession session = new SqlDatabaseSession(database, new QueryTemplates());
        Users users = new DatabaseUsers(session);
        Activities activities = new DatabaseActivities(session);
        Dates dates = new DatabaseDates(session);
        Descriptions descriptions = new DatabaseDescriptions(session);

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
        TodayRespondent todayRespondent = new TodayRespondent(identity, viewsTemplates);
        SigningInRespondent signingInRespondent = new SigningInRespondent(todayRespondent, signingInView, users,
            hashing, identity);
        SigningUpRespondent signingUpRespondent = new SigningUpRespondent(viewsTemplates, users, hashing, emails);
        SigningOutRespondent signingOutRespondent = new SigningOutRespondent(signingInRespondent);
        CalendarRespondent calendarRespondent = new CalendarRespondent(identity, calendarView, dates,
            serverClientDates);
        ProfileRespondent profileRespondent = new ProfileRespondent(identity, users, viewsTemplates);
        DayPlanExecutionRespondent dayPlanExecutionRespondent = new DayPlanExecutionRespondent(identity,
            dayPlanExecutionView, activities, limitedDate, dateParsing, serverClientDates);
        DayPlanRespondent dayPlanRespondent = new DayPlanRespondent(identity, dayPlanView, activities,
            limitedDate, dateParsing, serverClientDates);
        ActivityRespondent activityRespondent = new ActivityRespondent(identity, activityView,
            dayPlanExecutionRespondent, dayPlanRespondent, activities, descriptions, limitedDate, serverClientDates);
        ActivitiesRespondent activitiesRespondent = new ActivitiesRespondent(activitiesViews);

        String userGroup = "user/";

        welcomeRespondent.init(app);
        signingInRespondent.init(app);
        signingUpRespondent.init(app);
        signingOutRespondent.init(app);
        todayRespondent.init(userGroup, app);
        calendarRespondent.init(userGroup, app);
        profileRespondent.init(userGroup, app);
        dayPlanExecutionRespondent.init(userGroup, app);
        dayPlanRespondent.init(userGroup, app);
        activityRespondent.init(userGroup, app);
        activitiesRespondent.init(userGroup, app);

        //TODO is it a good idea?
        app.before(userGroup + "*", ctx -> {
            if (!identity.isValid(ctx.req)) {
                ctx.redirect("/");
            }
        });
        //TODO handle exceptions
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            e.printStackTrace();
            String message = e.getMessage() == null ? "UNKNOWN" : e.getMessage();
            ctx.html(message);
        });
        //TODO proper pages per http code
        app.exception(BadRequestResponse.class, (e, ctx) -> {
            ctx.html(String.format("Bad request: %s", e.getMessage()));
        });
        app.start(configuration.port());
    }
}
