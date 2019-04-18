import { Router } from "./navigation/router.js";
import { HttpConnections } from "./http/http-connections.js";
import { UrlParams } from "./http/url-params.js";
import { DateTimeParams } from "./http/date-time-params.js";
import { Validations } from "./validation/validations.js";
import { TabsNavigation } from "./navigation/tabs-navigation.js";

const host = "http://127.0.0.1:8080/";

export const routes = {
    signIn: "sign-in",
    signUp: "sign-up",
    signOut: "sign-out",
    today: "today",
    plan: "plan",
    history: "history",
    profile: "profile",
    dayPlan: "plan/day",
    activity: "activity"
};

export const endpoints = {
    signIn: `${host}sign-in`,
    signUp: `${host}sign-up`,
    signOut: `${host}sign-out`
};

export const paramsKeys = {
    year: "year",
    month: "month",
    day: "day"
};

export const errors = {
    invalidEmail: "Email should be at least 5 signs of length, contains '@' character, domain and '.' character",
    invalidLogin: "Login should have at least 3 alphanumeric characters",
    invalidPassword: "Password needs to have at least 8 characters",
    passwordsMismatch: "Given passwords differ"
};



export const router = new Router(host);
export const tabsNavigation = new TabsNavigation(router);
export const httpConnections = new HttpConnections();
export const urlParams = new UrlParams();
export const dateTimeParams = new DateTimeParams(urlParams, paramsKeys);
export const validations = new Validations();