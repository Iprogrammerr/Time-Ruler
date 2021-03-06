package com.iprogrammerr.time.ruler;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
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
import com.iprogrammerr.time.ruler.model.user.DatabaseUsersActualization;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.model.user.UsersActualization;
import com.iprogrammerr.time.ruler.respondent.CalendarRespondent;
import com.iprogrammerr.time.ruler.respondent.ErrorRespondent;
import com.iprogrammerr.time.ruler.respondent.ProfileRespondent;
import com.iprogrammerr.time.ruler.respondent.WelcomeRespondent;
import com.iprogrammerr.time.ruler.respondent.activity.ActivitiesRespondent;
import com.iprogrammerr.time.ruler.respondent.activity.ActivityRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.PasswordResetRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningInRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningOutRespondent;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningUpRespondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanRespondent;
import com.iprogrammerr.time.ruler.route.CalendarRoutes;
import com.iprogrammerr.time.ruler.route.ErrorRoutes;
import com.iprogrammerr.time.ruler.route.ProfileRoutes;
import com.iprogrammerr.time.ruler.route.WelcomeRoutes;
import com.iprogrammerr.time.ruler.route.activity.ActivitiesRoutes;
import com.iprogrammerr.time.ruler.route.activity.ActivityRoutes;
import com.iprogrammerr.time.ruler.route.authentication.PasswordResetRoutes;
import com.iprogrammerr.time.ruler.route.authentication.SigningInRoutes;
import com.iprogrammerr.time.ruler.route.authentication.SigningOutRoutes;
import com.iprogrammerr.time.ruler.route.authentication.SigningUpRoutes;
import com.iprogrammerr.time.ruler.route.day.DayPlanExecutionRoutes;
import com.iprogrammerr.time.ruler.route.day.DayPlanRoutes;
import com.iprogrammerr.time.ruler.view.HtmlViewsTemplates;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;
import com.iprogrammerr.time.ruler.view.rendering.CalendarViews;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionViews;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanViews;
import com.iprogrammerr.time.ruler.view.rendering.ErrorViews;
import com.iprogrammerr.time.ruler.view.rendering.PasswordResetViews;
import com.iprogrammerr.time.ruler.view.rendering.ProfileViews;
import com.iprogrammerr.time.ruler.view.rendering.SigningInViews;
import com.iprogrammerr.time.ruler.view.rendering.SigningUpViews;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.sql.DataSource;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class App {

    public static void main(String... args) throws Exception {
        Configuration configuration = Configuration.fromCmd(args);
        String root = configuration.resourcesPath();

        Javalin app = Javalin.create(c -> {
            if (root.isEmpty()) {
                c.addStaticFiles("css", Location.CLASSPATH)
                    .addStaticFiles("image", Location.CLASSPATH)
                    .addStaticFiles("js", Location.CLASSPATH);
            } else {
                File rootFile = new File(root);
                c.addStaticFiles(rootFile.getPath() + File.separator + "css", Location.EXTERNAL)
                    .addStaticFiles(rootFile.getPath() + File.separator + "image", Location.EXTERNAL)
                    .addStaticFiles(rootFile.getPath() + File.separator + "js", Location.EXTERNAL);
            }
        });

        DateFormat dateFormat = new SimpleDateFormat("E, dd.MM.yyyy", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(dateFormat.getTimeZone());
        DateFormat dateTimeFormat = new SimpleDateFormat("E, dd.MM.yyyy, HH:mm", Locale.US);
        dateTimeFormat.setTimeZone(dateFormat.getTimeZone());

        DateTimeFormatting formatting = new DateTimeFormatting(dateFormat, timeFormat,
            dateTimeFormat);
        DateParsing dateParsing = new DateParsing();
        LimitedDate limitedDate = new LimitedDate(dateParsing);
        ServerClientDates serverClientDates = new ServerClientDates();

        TemplateEngine engine = new TemplateEngine();
        AbstractConfigurableTemplateResolver resolver;
        if (root.isEmpty()) {
            resolver = new ClassLoaderTemplateResolver();
            resolver.setCacheable(true);
            resolver.setPrefix("template" + File.separator);
        } else {
            resolver = new FileTemplateResolver();
            resolver.setCacheable(false);
            resolver.setPrefix(new File(root, "template").getPath() + File.separator);
        }
        engine.setTemplateResolver(resolver);
        Messages messages = new Messages();
        messages.init("messages.properties");
        engine.setMessageResolver(messages);
        ViewsTemplates viewsTemplates = new HtmlViewsTemplates(engine);

        SigningInViews signingInViews = new SigningInViews(viewsTemplates,
            SigningUpRespondent.SIGN_UP,
            PasswordResetRespondent.PASSWORD_RESET);
        PasswordResetViews passwordResetViews = new PasswordResetViews(viewsTemplates);
        SigningUpViews signingUpViews = new SigningUpViews(viewsTemplates,
            SigningInRespondent.SIGN_IN);
        CalendarViews calendarViews = new CalendarViews(viewsTemplates);
        DayPlanExecutionViews dayPlanExecutionViews = new DayPlanExecutionViews(viewsTemplates,
            formatting);
        DayPlanViews dayPlanViews = new DayPlanViews(viewsTemplates, formatting);
        ActivityViews activityViews = new ActivityViews(viewsTemplates, formatting);
        ActivitiesViews activitiesViews = new ActivitiesViews(viewsTemplates, formatting);
        ProfileViews profileViews = new ProfileViews(viewsTemplates);
        ErrorViews errorViews = new ErrorViews(viewsTemplates, messages);

        HikariConfig config = new HikariConfig();
        config.setUsername(configuration.databaseUser());
        config.setPassword(configuration.databasePassword());
        config.setJdbcUrl(configuration.jdbcUrl());
        DataSource dataSource = new HikariDataSource(config);
        QueryFactory queryFactory = new SmartQueryFactory(dataSource);

        Users users = new DatabaseUsers(queryFactory);
        UsersActualization actualization = new DatabaseUsersActualization(queryFactory);
        Activities activities = new DatabaseActivities(queryFactory);
        ActivitiesSearch activitiesSearch = new DatabaseActivitiesSearch(queryFactory);
        Dates dates = new DatabaseDates(queryFactory);
        Descriptions descriptions = new DatabaseDescriptions(queryFactory);

        EmailServer emailServer = new ConfigurableEmailServer(configuration.adminEmail(),
            configuration.adminPassword(), configuration.smtpHost(), configuration.smtpPort(),
            configuration.smtpSsl());
        Emails emails = new Emails(emailServer, configuration);
        Hashing hashing = new Hashing();
        Identity<Long> identity = new SessionIdentity();

        String userGroup = "user/";
        WelcomeRespondent welcomeRespondent = new WelcomeRespondent(viewsTemplates);
        DayPlanExecutionRespondent dayPlanExecutionRespondent = new DayPlanExecutionRespondent(identity,
            dayPlanExecutionViews, activitiesSearch, limitedDate, dateParsing, serverClientDates);
        DayPlanRespondent dayPlanRespondent = new DayPlanRespondent(identity, dayPlanViews,
            activitiesSearch, limitedDate, dateParsing, serverClientDates);
        CalendarRespondent calendarRespondent = new CalendarRespondent(identity, calendarViews, dates,
            serverClientDates);
        ActivityRespondent activityRespondent = new ActivityRespondent(identity, activityViews,
            dayPlanExecutionRespondent, dayPlanRespondent, activities, activitiesSearch, descriptions,
            limitedDate, serverClientDates);
        ActivitiesRespondent activitiesRespondent = new ActivitiesRespondent(identity, activitiesViews,
            activitiesSearch, serverClientDates);
        SigningInRespondent signingInRespondent = new SigningInRespondent(dayPlanExecutionRespondent,
            signingInViews, users, hashing, identity, userGroup);
        PasswordResetRespondent passwordResetRespondent = new PasswordResetRespondent(signingInRespondent,
            users, actualization, emails, hashing, passwordResetViews);
        SigningUpRespondent signingUpRespondent = new SigningUpRespondent(signingInRespondent,
            signingUpViews, users, actualization, hashing, emails);
        SigningOutRespondent signingOutRespondent = new SigningOutRespondent(signingInRespondent);
        ProfileRespondent profileRespondent = new ProfileRespondent(signingOutRespondent, identity, users,
            actualization, hashing, profileViews);
        ErrorRespondent errorRespondent = new ErrorRespondent(errorViews);

        WelcomeRoutes welcomeRoutes = new WelcomeRoutes(welcomeRespondent);

        SigningUpRoutes signingUpRoutes = new SigningUpRoutes(signingUpRespondent);
        SigningOutRoutes signingOutRoutes = new SigningOutRoutes(signingOutRespondent);
        PasswordResetRoutes passwordResetRoutes = new PasswordResetRoutes(passwordResetRespondent);

        DayPlanExecutionRoutes dayPlanExecutionRoutes = new DayPlanExecutionRoutes(
            dayPlanExecutionRespondent);
        DayPlanRoutes dayPlanRoutes = new DayPlanRoutes(dayPlanRespondent);
        CalendarRoutes calendarRoutes = new CalendarRoutes(calendarRespondent);
        ActivityRoutes activityRoutes = new ActivityRoutes(activityRespondent);
        ActivitiesRoutes activitiesRoutes = new ActivitiesRoutes(activitiesRespondent);
        ProfileRoutes profileRoutes = new ProfileRoutes(profileRespondent);

        SigningInRoutes signingInRoutes = new SigningInRoutes(identity, signingInRespondent, dayPlanExecutionRoutes);

        ErrorRoutes errorRoutes = new ErrorRoutes(errorRespondent);

        welcomeRoutes.init(app);

        signingInRoutes.init(app);
        passwordResetRoutes.init(app);
        signingUpRoutes.init(app);
        signingOutRoutes.init(app);

        calendarRoutes.init(userGroup, app);
        dayPlanExecutionRoutes.init(userGroup, app);
        dayPlanRoutes.init(userGroup, app);
        activityRoutes.init(userGroup, app);
        activitiesRoutes.init(userGroup, app);
        profileRoutes.init(userGroup, app);

        errorRoutes.init(app);

        app.before(userGroup + "*", ctx -> {
            if (!identity.isValid(ctx.req)) {
                ctx.redirect("/");
            }
        });

        app.start(configuration.port());
    }
}
