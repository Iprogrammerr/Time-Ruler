import { endpoints } from "./app.js";
import { tabsNavigation } from "./app.js";
import { hiddenInputKeys } from "./app.js";
import { FormAction } from "./http/form-action.js";

tabsNavigation.setup(document.querySelector("div"));
const activityId = activityIdFromPath();
console.log(`Activity id = ${activityId}`);
const form = document.querySelector("form");
addEventListener("submit", e => e.preventDefault());
document.getElementById("recent").onclick = () => console.log("Show recent...");
const saveActivity = document.getElementById("save");
saveActivity.onclick = () => {
    if (isFormValid()) {
        new FormAction(form).submit(`${endpoints.saveActivity}`, {key: hiddenInputKeys.done, value: false});
        saveActivity.disabled = true;
    }
};

function activityIdFromPath() {
    let segments = location.pathname.split("/");
    let id = segments[segments.length - 1];
    if (isNaN(id)) {
        id = -1;
    }  else {
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