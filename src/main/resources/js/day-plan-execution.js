import { router, routes, tabsNavigation, dateTimeParams, hiddenDataKeys, paramsKeys} from "./app.js";
import { SmartDate } from "./date/smart-date.js";
import { Confirmation } from "./component/confirmation.js";
import { Activities } from "./component/activities.js";

const yearMonthDay = dateTimeParams.dateFromUrl().asYearMonthDay();
const confirmation = new Confirmation(document.getElementById("confirmation"));
const activities = new Activities(confirmation);
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
activities.setupHistoryNavigation(deleteConfirmation, doneConfirmation, notDoneConfirmation);

function setupTabsNavigation() {
    tabsNavigation.setYearMonth(yearMonthDay.year, yearMonthDay.month);
    let today = new SmartDate().asYearMonthDay();
    let allTabsActive = today.year != yearMonthDay.year || today.month != yearMonthDay.month ||
        today.day != yearMonthDay.day;
    tabsNavigation.setup(document.querySelector("div"), allTabsActive);
};