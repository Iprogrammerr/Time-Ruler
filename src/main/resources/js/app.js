import {Router} from "./router.js";
import {HttpConnections} from "./http/http-connections.js";
const host = "http://127.0.0.1:8080/";

export const routes = {
    signIn: "sign-in",
    signUp: "sign-up",
    dashboard: "dashboard"
};

const endpointsGroups = {
    user: "user"
};

export const endpoints = {
    signIn: `${host}${endpointsGroups.user}/sign-in`,
    signUp: `${host}${endpointsGroups.user}/sign-up`,
    signOut: `${host}${endpointsGroups.user}/sign-out`
};

export const router = new Router(host);
export const httpConnections = new HttpConnections();