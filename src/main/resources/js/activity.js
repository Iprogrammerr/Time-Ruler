import { tabsNavigation, router, routes, dateTimeParams, parametrizedEndpoints, paramsKeys, urlParams } from "./app.js";
import { FormAction } from "./http/form-action.js";

const activityId = activityIdFromPath();
const date = activityId > 0 ? {} : dateTimeParams.dateFromUrl();
const plan = urlParams.getOrDefault(paramsKeys.plan, "false");
const form = document.querySelector("form");
const saveActivity = document.getElementById("save");

tabsNavigation.setup(document.querySelector("div"), true);
addEventListener("submit", e => e.preventDefault());
document.getElementById("recent").onclick = () => router.forwardWithParams(routes.activities, new Map([[paramsKeys.plan, plan]]));
saveActivity.onclick = () => {
    if (isFormValid()) {
        let endpoint;
        if (activityId > 0) {
            endpoint = parametrizedEndpoints.updateActivity(activityId);
        }
         else {
            endpoint = parametrizedEndpoints.createActivity(date);
        }
        new FormAction(form).submit(endpoint);
        saveActivity.disabled = true;
    }
};

function activityIdFromPath() {
    let segments = location.pathname.split("/");
    let id = segments[segments.length - 1];
    if (isNaN(id)) {
        id = -1;
    } else {
        id = parseInt(id);
        if (id < 0) {
            id = -1;
        }
    }
    return id;
};

//TODO validate form
function isFormValid() {
    return true;
};