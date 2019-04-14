import {Router} from "./navigation/router.js";
import {HttpConnections} from "./http/http-connections.js";
import {Validations} from "./validation/validations.js";

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
    editDayPlan: "plan/day/edit"
};

export const endpoints = {
    signIn: `${host}sign-in`,
    signUp: `${host}sign-up`,
    signOut: `${host}sign-out`
};

export const params = {
    offset: "offset",
    day: "day"
};

export const errors = {
    invalidEmail: "Email should be at least 5 signs of length, contains '@' character, domain and '.' character",
    invalidLogin: "Login should have at least 3 alphanumeric characters",
    invalidPassword: "Password needs to have at least 8 characters",
    passwordsMismatch: "Given passwords differ"
};

export function setupTabsNavigation(tabsContainer, activeIndex) {
    let tabs = tabsContainer.children;
    if (activeIndex != 0) {
        tabs[0].onclick = () => router.forward(routes.today);
    }
    if (activeIndex != 1) {
        tabs[1].onclick = () => router.forward(routes.plan);
     }
     if (activeIndex != 2) {
         tabs[2].onclick = () => router.forward(routes.history);
     }
     if (activeIndex != 3) {
         tabs[3].onclick = () => router.forward(routes.profile);
     }
}

export const router = new Router(host);
export const httpConnections = new HttpConnections();
export const validations = new Validations();