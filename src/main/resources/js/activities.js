import { tabsNavigation, router, routes, paramsKeys, urlParams, hiddenDataKeys, dateTimeParams } from "./app.js";

const activityId = urlParams.getOrDefault(paramsKeys.id, 0);
const date = dateTimeParams.dateFromUrl();

tabsNavigation.setup(document.querySelector("div"), true);
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