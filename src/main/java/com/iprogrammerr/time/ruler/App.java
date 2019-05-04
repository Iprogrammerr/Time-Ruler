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
import com.iprogrammerr.time.ruler.model.activity.ActivitiesSearch;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivities;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivitiesSearch;
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
import com.iprogrammerr.time.ruler.respondent.CalendarRespondent;
import com.iprogrammerr.time.ruler.respondent.ErrorRespondent;
import com.iprogrammerr.time.ruler.respondent.ProfileRespondent;
import com.iprogrammerr.time.ruler.respondent.WelcomeRespondent;
import com.iprogrammerr.time.ruler.respondent.activity.ActivitiesRespondent;
import com.iprogrammerr.time.ruler.respondent.activity.ActivityRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningInRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningOutRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningUpRespondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanRespondent;
import com.iprogrammerr.time.ruler.view.HtmlViewsTemplates;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;
import com.iprogrammerr.time.ruler.view.rendering.CalendarViews;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionViews;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanViews;
import com.iprogrammerr.time.ruler.view.rendering.ErrorViews;
import com.iprogrammerr.time.ruler.view.rendering.ProfileViews;
import com.iprogrammerr.time.ruler.view.rendering.SigningInViews;
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
        DateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        dateTimeFormat.setTimeZone(dateFormat.getTimeZone());

        DateTimeFormatting formatting = new DateTimeFormatting(dateFormat, timeFormat, dateTimeFormat);
        DateParsing dateParsing = new DateParsing();
        LimitedDate limitedDate = new LimitedDate(dateParsing);
        ServerClientDates serverClientDates = new ServerClientDates();

        TemplateEngine engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setCacheable(false);
        resolver.setPrefix(new File(root, "template").getPath() + File.separator);
        engine.setTemplateResolver(resolver);
        Messages messages = new Messages();
        messages.init("messages.properties");
        engine.setMessageResolver(messages);
        ViewsTemplates viewsTemplates = new HtmlViewsTemplates(engine);

        SigningInViews signingInView = new SigningInViews(viewsTemplates);
        CalendarViews calendarView = new CalendarViews(viewsTemplates);
        DayPlanExecutionViews dayPlanExecutionView = new DayPlanExecutionViews(viewsTemplates, formatting);
        DayPlanViews dayPlanView = new DayPlanViews(viewsTemplates, formatting);
        ActivityViews activityView = new ActivityViews(viewsTemplates, formatting);
        ActivitiesViews activitiesViews = new ActivitiesViews(viewsTemplates, formatting);
        ProfileViews profileViews = new ProfileViews(viewsTemplates);
        ErrorViews errorViews = new ErrorViews(viewsTemplates, messages);

        Database database = new SqlDatabase(configuration.databaseUser(), configuration.databasePassword(),
            configuration.jdbcUrl());
        DatabaseSession session = new SqlDatabaseSession(database, new QueryTemplates());
        Users users = new DatabaseUsers(session);
        Activities activities = new DatabaseActivities(session);
        ActivitiesSearch activitiesSearch = new DatabaseActivitiesSearch(session);
        Dates dates = new DatabaseDates(session);
        Descriptions descriptions = new DatabaseDescriptions(session);

        EmailServer emailServer = new ConfigurableEmailServer(configuration.adminEmail(),
            configuration.adminPassword(), configuration.smtpHost(), configuration.smtpPort());
        Emails emails = new Emails(
            emailServer, configuration.activationLinkBase(), configuration.signUpEmailSubject(),
            configuration.signUpEmailTemplate()
        );
        Hashing hashing = new Hashing();
        Identity<Long> identity = new SessionIdentity();

        WelcomeRespondent welcomeRespondent = new WelcomeRespondent(viewsTemplates);
        CalendarRespondent calendarRespondent = new CalendarRespondent(identity, calendarView, dates,
            serverClientDates);
        ProfileRespondent profileRespondent = new ProfileRespondent(identity, users, profileViews);
        DayPlanExecutionRespondent dayPlanExecutionRespondent = new DayPlanExecutionRespondent(identity,
            dayPlanExecutionView, activitiesSearch, limitedDate, dateParsing, serverClientDates);
        DayPlanRespondent dayPlanRespondent = new DayPlanRespondent(identity, dayPlanView, activitiesSearch,
            limitedDate, dateParsing, serverClientDates);
        ActivityRespondent activityRespondent = new ActivityRespondent(identity, activityView,
            dayPlanExecutionRespondent, dayPlanRespondent, activities, activitiesSearch, descriptions, limitedDate,
            serverClientDates);
        ActivitiesRespondent activitiesRespondent = new ActivitiesRespondent(identity, activitiesViews,
            activitiesSearch, serverClientDates);
        SigningInRespondent signingInRespondent = new SigningInRespondent(dayPlanExecutionRespondent, signingInView,
            users, hashing, identity);
        SigningUpRespondent signingUpRespondent = new SigningUpRespondent(viewsTemplates, users, hashing, emails);
        SigningOutRespondent signingOutRespondent = new SigningOutRespondent(signingInRespondent);
        ErrorRespondent errorRespondent = new ErrorRespondent(errorViews);

        String userGroup = "user/";

        welcomeRespondent.init(app);
        calendarRespondent.init(userGroup, app);
        profileRespondent.init(userGroup, app);
        dayPlanExecutionRespondent.init(userGroup, app);
        dayPlanRespondent.init(userGroup, app);
        activityRespondent.init(userGroup, app);
        activitiesRespondent.init(userGroup, app);
        signingInRespondent.init(app);
        signingUpRespondent.init(app);
        signingOutRespondent.init(app);
        errorRespondent.init(app);

        //TODO Authentication respondent
        app.before(userGroup + "*", ctx -> {
            if (!identity.isValid(ctx.req)) {
                ctx.redirect("/");
            }
        });
        app.start(configuration.port());
    }
}
