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
import com.iprogrammerr.time.ruler.model.day.DatabaseDays;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.model.session.SessionIdentity;
import com.iprogrammerr.time.ruler.model.session.UtcOffsetAttribute;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.respondent.ActivityRespondent;
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
import com.iprogrammerr.time.ruler.view.rendering.CalendarView;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionView;
import com.iprogrammerr.time.ruler.view.rendering.SigningInView;
import io.javalin.BadRequestResponse;
import io.javalin.ForbiddenResponse;
import io.javalin.Javalin;
import io.javalin.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class App {

    public static void main(String... args) throws Exception {
        Configuration configuration = Configuration.fromCmd(args);
        File root = new File(configuration.resourcesPath());

        Javalin app = Javalin.create()
            .enableStaticFiles(root.getPath() + File.separator + "css", Location.EXTERNAL)
            .enableStaticFiles(root.getPath() + File.separator + "js", Location.EXTERNAL);

        TemplateEngine engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setCacheable(false);
        engine.setTemplateResolver(resolver);
        Messages messages = new Messages();
        messages.init("messages.properties");
        engine.setMessageResolver(messages);
        Views views = new HtmlViews(new File(root, "html"));
        ViewsTemplates viewsTemplates = new HtmlViewsTemplates(new File(root, "template"), engine);

        SigningInView signingInView = new SigningInView(viewsTemplates);
        CalendarView calendarView = new CalendarView(viewsTemplates);
        DayPlanExecutionView dayPlanExecutionView = new DayPlanExecutionView(viewsTemplates);

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
        UtcOffsetAttribute offsetAttribute = new UtcOffsetAttribute();

        WelcomeRespondent welcomeRespondent = new WelcomeRespondent(views);
        TodayRespondent todayRespondent = new TodayRespondent(identity, viewsTemplates);
        SigningInRespondent signingInRespondent = new SigningInRespondent(todayRespondent, signingInView, users,
            hashing, identity, offsetAttribute);
        SigningUpRespondent signingUpRespondent = new SigningUpRespondent(viewsTemplates, users, hashing, emails);
        SigningOutRespondent signingOutRespondent = new SigningOutRespondent(signingInView);
        CalendarRespondent calendarRespondent = new CalendarRespondent(identity, calendarView, days);
        ProfileRespondent profileRespondent = new ProfileRespondent(identity, users, viewsTemplates);
        DayPlanRespondent dayPlanRespondent = new DayPlanRespondent(identity, viewsTemplates, activities, dateFormat);
        ActivityRespondent activityRespondent = new ActivityRespondent(identity, viewsTemplates, dayPlanRespondent,
            days, activities, offsetAttribute);
        DayPlanExecutionRespondent dayPlanExecutionRespondent = new DayPlanExecutionRespondent(identity,
            dayPlanExecutionView, activities, dateFormat);

        String userGroup = "user/";

        welcomeRespondent.init(app);
        signingInRespondent.init(app);
        signingUpRespondent.init(app);
        signingOutRespondent.init(app);
        todayRespondent.init(userGroup, app);
        calendarRespondent.init(userGroup, app);
        profileRespondent.init(userGroup, app);
        dayPlanRespondent.init(userGroup, app);
        activityRespondent.init(userGroup, app);
        dayPlanExecutionRespondent.init(userGroup, app);

        app.before(userGroup + "*", ctx -> {
            if (!identity.isValid(ctx.req)) {
                throw new ForbiddenResponse();
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
        app.exception(ForbiddenResponse.class, (e, ctx) -> {
            ctx.html("You are not allowed to see this page");
        });
        app.exception(BadRequestResponse.class, (e, ctx) -> {
            ctx.html(String.format("Bad request: %s", e.getMessage()));
        });
        app.start(configuration.port());
    }
}
