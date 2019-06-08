import { router, routes, tabsNavigation, dateTimeParams, paramsKeys } from "./app.js";
import { Confirmation } from "./component/confirmation.js";
import { Activities } from "./component/activities.js";

const yearMonthDay = dateTimeParams.dateFromUrl().asYearMonthDay();
const confirmation = new Confirmation(document.getElementById("confirmation"));
const activities = new Activities(confirmation);

tabsNavigation.setYearMonth(yearMonthDay.year, yearMonthDay.month);
tabsNavigation.setup(document.querySelector("div"), true);
document.getElementById("add").onclick = () => {
    let params = dateTimeParams.dateFromUrlAsParam();
    params.set(paramsKeys.plan, true);
    router.forwardWithParams(routes.activity, params);
};
activities.setupPlanNavigation();
