import {router} from "./app.js";
import {routes} from "./app.js";
import { tabsNavigation } from "./app.js";
import { dateTimeParams } from "./app.js";
import { hiddenDataKeys } from "./app.js";

const yearMonthDay = dateTimeParams.currentYearMonthDayFromUrl();
tabsNavigation.setYearMonth(yearMonthDay.year, yearMonthDay.month);
tabsNavigation.setup(document.querySelector("div"), true);
document.getElementById("add").onclick = () => router.forwardWithParams(routes.activity, dateTimeParams.dateFromUrlAsParam());
setupListNavigation();

function setupListNavigation() {
    let activities = document.getElementsByClassName("activities")[0];
    for (let a of activities.children) {
        let id = a.getAttribute(hiddenDataKeys.id);
        a.onclick = () => router.forwardWithVariable(routes.activity, id);
    }
};