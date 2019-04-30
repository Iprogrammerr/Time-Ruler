import { router } from "./app.js";
import { routes } from "./app.js";
import { tabsNavigation } from "./app.js";
import { dateTimeParams } from "./app.js";
import { hiddenDataKeys } from "./app.js";
import { paramsKeys } from "./app.js";
import { parametrizedEndpoints } from "./app.js";
import { HttpConnections } from "./http/http-connections.js";

const yearMonthDay = dateTimeParams.currentYearMonthDayFromUrl();
tabsNavigation.setYearMonth(yearMonthDay.year, yearMonthDay.month);
tabsNavigation.setup(document.querySelector("div"), true);
document.getElementById("add").onclick = () => {
    let params = dateTimeParams.dateFromUrlAsParam();
    params.set(paramsKeys.plan, false);
    router.forwardWithParams(routes.activity, params);
};
setupListNavigation();
const httpConnections = new HttpConnections();

//TODO page with errors? Dialog? Text?
function setupListNavigation() {
    let activities = document.getElementsByClassName("activities")[0];
    for (let a of activities.children) {
        let id = a.getAttribute(hiddenDataKeys.id);
        a.onclick = () => router.forwardWithVariable(routes.activity, id);
        a.getElementsByClassName("close")[0].onclick = (e) => {
            e.stopPropagation();
            httpConnections.delete(parametrizedEndpoints.deleteActivity(id)).then(r => {
                removeActivity(activities, a);
            }).catch(e => alert(e));
        };
        let spans = a.querySelectorAll("span");
        setupDoneNotDone(spans[0], spans[1], id);
     }
};

function setupDoneNotDone(done, notDone, id) {
    done.onclick = (e) => {
        e.stopPropagation();
        httpConnections.put(parametrizedEndpoints.setActivityNotDone(id)).then(r => {
            notDone.className = "visible";
            done.className = "hidden";
        }).catch(e => alert(e));
    };
    notDone.onclick = (e) => {
        e.stopPropagation();
        httpConnections.put(parametrizedEndpoints.setActivityDone(id)).then(r => {
            done.className = "visible";
            notDone.className = "hidden";
        }).catch(e => alert(e));
    };
};

function removeActivity(activities, activity) {
    activity.remove();
    if (activities.children.length == 0) {
        document.getElementsByClassName("hidden")[0].style.display = "block";
    }
};