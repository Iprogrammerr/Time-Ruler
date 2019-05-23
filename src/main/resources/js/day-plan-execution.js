import { router, routes, tabsNavigation, dateTimeParams, hiddenDataKeys, paramsKeys, parametrizedEndpoints } from "./app.js";
import { SmartDate } from "./date/smart-date.js";
import { HttpConnections } from "./http/http-connections.js";
import { Confirmation } from "./component/confirmation.js";

const yearMonthDay = dateTimeParams.dateFromUrl().asYearMonthDay();
const httpConnections = new HttpConnections();
const confirmation = new Confirmation(document.getElementById("confirmation"));
const deleteConfirmation = document.body.getAttribute(hiddenDataKeys.confirmation);
const doneConfirmation = document.body.getAttribute(hiddenDataKeys.doneConfirmation);
const notDoneConfirmation = document.body.getAttribute(hiddenDataKeys.notDoneConfirmation);

document.getElementById("add").onclick = () => {
    let params = dateTimeParams.yearMonthDayAsDateParam(yearMonthDay.year, yearMonthDay.month, yearMonthDay.day);
    params.set(paramsKeys.plan, new SmartDate().isNow(yearMonthDay.year, yearMonthDay.month, yearMonthDay.day));
    router.forwardWithParams(routes.activity, params);
};
let yesterday = document.getElementsByClassName("yesterday");
if (yesterday.length > 0) {
    yesterday[0].onclick = () => router.forwardWithParams(routes.dayPlanExecution, dateTimeParams.yesterdayAsDateParam());
}
setupTabsNavigation();
setupListNavigation();

function setupTabsNavigation() {
    tabsNavigation.setYearMonth(yearMonthDay.year, yearMonthDay.month);
    let today = new SmartDate().asYearMonthDay();
    let allTabsActive = today.year != yearMonthDay.year || today.month != yearMonthDay.month ||
        today.day != yearMonthDay.day;
    tabsNavigation.setup(document.querySelector("div"), allTabsActive);
};
//TODO page with errors? Dialog? Text?
function setupListNavigation() {
    let activities = document.getElementsByClassName("activities")[0];
    for (let a of activities.children) {
        let id = a.getAttribute(hiddenDataKeys.id);
        a.onclick = () => router.forwardWithParam(routes.activity, paramsKeys.id, id);
        a.getElementsByClassName("close")[0].onclick = (e) => {
            e.stopPropagation();
            confirmation.setMessage(deleteConfirmation);
            confirmation.setup(() => {
                httpConnections.delete(parametrizedEndpoints.deleteActivity(id)).then(r => {
                    removeActivity(activities, a);
                }).catch(e => alert(e));
            });
            confirmation.show();
        };
        let spans = a.querySelectorAll("span");
        setupDoneNotDone(spans[0], spans[1], id);
    }
};

function setupDoneNotDone(done, notDone, id) {
    done.onclick = (e) => {
        e.stopPropagation();
        confirmation.setMessage(notDoneConfirmation);
        confirmation.setup(() => {
            httpConnections.put(parametrizedEndpoints.setActivityNotDone(id)).then(r => {
                notDone.className = "visible";
                done.className = "hidden";
            }).catch(e => alert(e));
        });
        confirmation.show();
    };
    notDone.onclick = (e) => {
        e.stopPropagation();
        confirmation.setMessage(doneConfirmation);
        confirmation.setup(() => {
            httpConnections.put(parametrizedEndpoints.setActivityDone(id)).then(r => {
                done.className = "visible";
                notDone.className = "hidden";
            }).catch(e => alert(e));
        });
        confirmation.show();
    };
};

function removeActivity(activities, activity) {
    activity.remove();
    if (activities.children.length == 0) {
        document.getElementsByClassName("hidden")[0].style.display = "block";
    }
};