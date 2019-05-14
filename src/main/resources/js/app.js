import { Router } from "./navigation/router.js";
import { HttpConnections } from "./http/http-connections.js";
import { UrlParams } from "./http/url-params.js";
import { DateTimeParams } from "./date/date-time-params.js";
import { Validations } from "./validation/validations.js";
import { TabsNavigation } from "./navigation/tabs-navigation.js";
import { Cookies } from "./http/cookies.js";
import { Errors } from "./validation/errors.js";

const host = `${document.location.origin}/`;
const userPrefix = "user/";

export const routes = {
    signIn: "sign-in",
    signUp: "sign-up",
    signOut: "sign-out",
    today: `${userPrefix}today`,
    plan: `${userPrefix}plan`,
    history: `${userPrefix}history`,
    profile: `${userPrefix}profile`,
    activity: `${userPrefix}activity`,
    activities: `${userPrefix}activities`,
    dayPlanExecution: `${userPrefix}day-plan-execution`,
    dayPlan: `${userPrefix}plan/day`
};

const hostWithUser = `${host}${userPrefix}`;
export const endpoints = {
    signIn: `${host}sign-in`,
    passwordReset: `${host}password-reset`,
    signUp: `${host}sign-up`,
    signOut: `${host}sign-out`,
    profileEmail: `${hostWithUser}profile/email`,
    profileName: `${hostWithUser}profile/name`,
    profilePassword: `${hostWithUser}profile/password`
};

export const paramsKeys = {
    year: "year",
    month: "month",
    date: "date",
    plan: "plan",
    template: "template",
    page: "page",
    id: "id",
    pattern: "pattern"
};

export const hiddenInputKeys = {
    done: "done",
    utcOffset: "utcOffset"
};

export const hiddenDataKeys = {
    id: "data-id",
    plan: "data-plan",
    date: "data-date",
    confirmation: "data-confirmation",
    doneConfirmation: "data-done-confirmation",
    notDoneConfirmation: "data-not-done-confirmation"
};

const cookiesKeys = {
    utcOffset: "utcOffset"
};

export const router = new Router(host);
export const tabsNavigation = new TabsNavigation(router);
export const httpConnections = new HttpConnections();
export const urlParams = new UrlParams();
export const dateTimeParams = new DateTimeParams(urlParams, paramsKeys);
export const validations = new Validations();
export const errors = new Errors();

export const parametrizedEndpoints = {
    createActivity: (date) => router.routeWithParams(routes.activity, dateTimeParams.dateAsParam(date)),
    updateActivity: (id) => `${router.fullRoute(routes.activity)}/${id}`,
    deleteActivity: (id) => `${router.fullRoute(routes.activity)}/${id}`,
    setActivityDone: (id) => `${router.fullRoute(routes.activity)}/done/${id}`,
    setActivityNotDone: (id) => `${router.fullRoute(routes.activity)}/not-done/${id}`
};

attachUtcOffset();

function attachUtcOffset() {
    let cookies = new Cookies();
    let oldOffset = 0;
    if (cookies.has(cookiesKeys.utcOffset)) {
        oldOffset = cookies.get(cookiesKeys.utcOffset);
        if (isNaN(oldOffset)) {
            oldOffset = 0;
        } else {
            oldOffset = parseInt(oldOffset);
        }
    }
    let date = new Date();
    let offset = ((date.getUTCHours() - date.getHours()) * 60 + date.getUTCMinutes() - date.getMinutes()) * 60;
    if (oldOffset !== offset) {
        cookies.put(cookiesKeys.utcOffset, offset);
    }
};