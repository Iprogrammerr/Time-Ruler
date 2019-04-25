import { Router } from "./navigation/router.js";
import { HttpConnections } from "./http/http-connections.js";
import { UrlParams } from "./http/url-params.js";
import { DateTimeParams } from "./date/date-time-params.js";
import { Validations } from "./validation/validations.js";
import { TabsNavigation } from "./navigation/tabs-navigation.js";

const host = "http://127.0.0.1:8080/";
const userRoutePrefix = "user/";

export const routes = {
    signIn: "sign-in",
    signUp: "sign-up",
    signOut: "sign-out",
    today: `${userRoutePrefix}today`,
    plan: `${userRoutePrefix}plan`,
    history: `${userRoutePrefix}history`,
    profile: `${userRoutePrefix}profile`,
    dayPlan: `${userRoutePrefix}plan/day`,
    activity: `${userRoutePrefix}activity`,
    dayPlanExecution: `${userRoutePrefix}day-plan-execution`
};

export const endpoints = {
    signIn: `${host}sign-in`,
    signUp: `${host}sign-up`,
    signOut: `${host}sign-out`,
    saveActivity: `${host}${userRoutePrefix}activity`
};

export const paramsKeys = {
    year: "year",
    month: "month",
    day: "day"
};

export const hiddenInputKeys = {
    done: "done",
    utcOffset: "utcOffset"
};


export const router = new Router(host);
export const tabsNavigation = new TabsNavigation(router);
export const httpConnections = new HttpConnections();
export const urlParams = new UrlParams();
export const dateTimeParams = new DateTimeParams(urlParams, paramsKeys);
export const validations = new Validations();