import { tabsNavigation, router, routes, paramsKeys, urlParams, hiddenDataKeys, dateTimeParams } from "./app.js";

const ENTER_KEY_CODE = 13;
const template = urlParams.getOrDefault(paramsKeys.template, 0);
const activityId = urlParams.getOrDefault(paramsKeys.id, 0);
const date = dateTimeParams.dateFromUrl().asIsoDateString();
const plan = urlParams.getOrDefault(paramsKeys.plan, true);
const page = urlParams.getOrDefault(paramsKeys.page, 0);
const pattern = urlParams.getOrDefault(paramsKeys.pattern, "");
const searchInput = document.getElementById("searchInput");

tabsNavigation.setup(document.querySelector("div"), true);
searchInput.onkeyup = (e) => {
    if (e.keyCode == ENTER_KEY_CODE) {
        searchActivities();
    }
};

function searchActivities() {
    let currentPattern = searchInput.value;
    if (currentPattern !== pattern) {
        let params = new Map();
        if (page > 0) {
            params.set(paramsKeys.page, page);
        }
        if (template > 0) {
            params.set(paramsKeys.template, template);
        }
        if (activityId > 0) {
            params.set(paramsKeys.id, activityId);
        } else {
            params.set(paramsKeys.date, date);
            params.set(paramsKeys.plan, plan);
        }
        if (currentPattern.length > 0) {
            params.set(paramsKeys.pattern, currentPattern);
        }
        router.forwardWithParams(routes.activities, params);
    }
};

document.getElementById("search").onclick = () => searchActivities();
setupListNavigation();

function setupListNavigation() {
    let activities = document.getElementsByClassName("activities")[0];
    for (let a of activities.children) {
        let id = a.getAttribute(hiddenDataKeys.id);
        a.onclick = () => {
            let params = new Map([[paramsKeys.template, id]]);
            if (activityId > 0) {
                params.set(paramsKeys.id, activityId);
            } else {
                params.set(paramsKeys.date, date);
                params.set(paramsKeys.plan, plan);
            }
            router.forwardWithParams(routes.activity, params);
        };
    }
};