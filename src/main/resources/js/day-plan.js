import { router } from "./app.js";
import { routes } from "./app.js";
import { tabsNavigation } from "./app.js";
import { dateTimeParams } from "./app.js";
import { hiddenDataKeys } from "./app.js";
import { parametrizedRoutes } from "./app.js";
import { HttpConnections } from "./http/http-connections.js";

const yearMonthDay = dateTimeParams.currentYearMonthDayFromUrl();
tabsNavigation.setYearMonth(yearMonthDay.year, yearMonthDay.month);
tabsNavigation.setup(document.querySelector("div"), true);
document.getElementById("add").onclick = () => router.forwardWithParams(routes.activity, dateTimeParams.dateFromUrlAsParam());
setupListNavigation();

//TODO error handling mechanim
function setupListNavigation() {
    let activities = document.getElementsByClassName("activities")[0];
    for (let a of activities.children) {
        let id = a.getAttribute(hiddenDataKeys.id);
        a.onclick = () => router.forwardWithVariable(routes.activity, id);
        a.getElementsByClassName("close")[0].onclick = (e) => {
            e.stopPropagation();
            new HttpConnections().delete(parametrizedRoutes.deleteActivity(id)).then(r => {
                console.log("Success!");
                location.reload();
            }).catch(e => {
                console.log(`error = ${e}`);
            });
        };
    }
};