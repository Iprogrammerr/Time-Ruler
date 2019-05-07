import { router, routes, tabsNavigation, dateTimeParams, hiddenDataKeys, paramsKeys, parametrizedEndpoints } from "./app.js";
import { HttpConnections } from "./http/http-connections.js";
import { Confirmation } from "./component/confirmation.js";

const yearMonthDay = dateTimeParams.dateFromUrl().asYearMonthDay();
const confirmation = new Confirmation(document.getElementById("confirmation"));

tabsNavigation.setYearMonth(yearMonthDay.year, yearMonthDay.month);
tabsNavigation.setup(document.querySelector("div"), true);
document.getElementById("add").onclick = () => {
    let params = dateTimeParams.dateFromUrlAsParam();
    params.set(paramsKeys.plan, true);
    router.forwardWithParams(routes.activity, params);
};
setupListNavigation();
const httpConnections = new HttpConnections();

//TODO error handling mechanism
function setupListNavigation() {
    let activities = document.getElementsByClassName("activities")[0];
    for (let a of activities.children) {
        let id = a.getAttribute(hiddenDataKeys.id);
        a.onclick = () => router.forwardWithParam(routes.activity, paramsKeys.id, id);
        a.getElementsByClassName("close")[0].onclick = (e) => {
            e.stopPropagation();
            confirmation.setup(() => {
                httpConnections.delete(parametrizedEndpoints.deleteActivity(id)).then(r => {
                    removeActivity(activities, a);
                }).catch(e => alert(e));
            });
            confirmation.show();
        };
    }
};

function removeActivity(activities, activity) {
    activity.remove();
    if (activities.children.length == 0) {
        document.getElementsByClassName("hidden")[0].style.display = "block";
    }
};