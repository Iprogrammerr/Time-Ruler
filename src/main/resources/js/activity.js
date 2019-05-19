import { tabsNavigation, router, routes, dateTimeParams, parametrizedEndpoints, paramsKeys, urlParams } from "./app.js";
import { FormAction } from "./http/form-action.js";

const activityId = urlParams.getOrDefault(paramsKeys.id, 0);
const date = activityId > 0 ? {} : dateTimeParams.dateFromUrl().asIsoDateString();
const plan = urlParams.getOrDefault(paramsKeys.plan, "false");
const form = document.querySelector("form");
const description = document.querySelector("textarea");
const saveActivity = document.getElementById("save");

tabsNavigation.setup(document.querySelector("div"), true);
addEventListener("submit", e => e.preventDefault());
document.getElementById("recent").onclick = () => {
    let params = new Map([[paramsKeys.plan, plan]]);
    if (activityId > 0) {
        params.set(paramsKeys.id, activityId);
    } else {
        params.set(paramsKeys.date, date);
    }
    router.forwardWithParams(routes.activities, params);
};
description.oninput = () => {
    if (description.clientHeight != description.scrollHeight) {
        description.style.height = `${description.scrollHeight}px`;
    }
};
saveActivity.onclick = () => {
    if (isFormValid()) {
        let endpoint;
        if (activityId > 0) {
            endpoint = parametrizedEndpoints.updateActivity(activityId);
        } else {
            endpoint = parametrizedEndpoints.createActivity(date);
        }
        new FormAction(form).submit(endpoint);
        saveActivity.disabled = true;
    }
};

//TODO validate form
function isFormValid() {
    return true;
};

