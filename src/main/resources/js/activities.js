import { tabsNavigation, router, routes, paramsKeys, urlParams, hiddenDataKeys, dateTimeParams } from "./app.js";

const template = urlParams.getOrDefault(paramsKeys.template, 0);
const activityId = urlParams.getOrDefault(paramsKeys.id, 0);
const date = dateTimeParams.dateFromUrl().asIsoDateString();
const page = urlParams.getOrDefault(paramsKeys.page, 0);
const pattern = urlParams.getOrDefault(paramsKeys.pattern, "");
const enterKeyCode = 13;
const searchInput = document.getElementById("search");

tabsNavigation.setup(document.querySelector("div"), true);
searchInput.onkeyup = (e) => {
    if (e.keyCode == enterKeyCode) {
        searchActivities();
    }
};
document.getElementById("searchIcon").onclick = () => searchActivities();
setupListNavigation();

function setupListNavigation() {
    let activities = document.getElementsByClassName("activities")[0];
    let plan = urlParams.getOrDefault(paramsKeys.plan, "true");
    for (let a of activities.children) {
        let id = a.getAttribute(hiddenDataKeys.id);
        a.onclick = () => {
            let params = new Map([[paramsKeys.template, id], [paramsKeys.plan, plan]]);
            if (activityId > 0) {
                params.set(paramsKeys.id, activityId);
            } else {
                params.set(paramsKeys.date, date);
            }
            router.forwardWithParams(routes.activity, params);
        };
    }
};

function searchActivities() {
    let currentPattern = searchInput.value;
    if (currentPattern.length > 0 || currentPattern !== pattern) {
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
        }
        if (currentPattern.length > 0) {
            params.set(paramsKeys.pattern, currentPattern);
        }
        router.forwardWithParams(routes.activities, params);
    }
};