import { endpoints } from "./app.js";
import { tabsNavigation } from "./app.js";

tabsNavigation.setup(document.querySelector("div"));
const activityId = activityIdFromPath();
console.log(`Activity id = ${activityId}`);
const form = document.querySelector("form");
addEventListener("submit", e => e.preventDefault());
document.getElementById("recent").onclick = () => console.log("Show recent...");
const saveActivity = document.getElementById("save");
saveActivity.onclick = () => {
    if (isFormValid()) {
        form.action = `${endpoints.saveActivity}`;
        form.method = "POST";
        console.log(`Method = ${form.method}`);
        let hidden = document.createElement("input");
        hidden.setAttribute("type", "hidden");
        hidden.setAttribute("name", "done");
        hidden.setAttribute("value", false);
        form.appendChild(hidden);
        form.submit();
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
}

function isFormValid() {
    return true;
}