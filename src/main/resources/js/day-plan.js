import {router} from "./app.js";
import {routes} from "./app.js";
import { tabsNavigation } from "./app.js";
import { dateTimeParams } from "./app.js";

const yearMonthDay = dateTimeParams.currentYearMonthDayFromUrl();
tabsNavigation.setYearMonth(yearMonthDay.year, yearMonthDay.month);
tabsNavigation.setup(document.querySelector("div"), true);
document.getElementById("add").onclick = () => router.forwardWithParams(routes.activity, dateTimeParams.dateFromUrlAsParam());