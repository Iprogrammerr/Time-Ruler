import {Router} from "./router.js";
import {HttpConnections} from "./http/http-connections.js";
import {Cookies} from "./http/cookies.js";
import {Validations} from "./validation/validations.js";

const host = "http://127.0.0.1:8080/";

export const routes = {
    signIn: "sign-in",
    signUp: "sign-up",
    dashboard: "dashboard"
};

export const endpoints = {
    signIn: `${host}sign-in`,
    signUp: `${host}sign-up`,
    signOut: `${host}sign-out`
};

export const cookiesKeys = {
    signedIn: "signedIn"
};

export const errors = {
    invalidEmail: "Email should be at least 5 signs of length, contains '@' character, domain and '.' character",
    invalidLogin: "Login should have at least 3 alphanumeric characters",
    invalidPassword: "Password needs to have at least 6 characters",
    passwordsMismatch: "Given passwords differ"
};


export const router = new Router(host);
export const httpConnections = new HttpConnections();
export const cookies = new Cookies();
export const validations = new Validations();