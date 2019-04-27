import { tabsNavigation } from "./app.js";
import { router } from "./app.js";
import { routes } from "./app.js";
import { dateTimeParams } from "./app.js";
import { SmartDate } from "./date/smart-date.js";

const STATE = {
    PLAN: "plan",
    HISTORY: "history"
};

const yearMonth = dateTimeParams.currentYearMonthFromUrl();
tabsNavigation.setup(document.querySelector("div"));
const state = stateFromActive(tabsNavigation.activeIndex());
setupMonthsNavigation();
setupDaysNavigation();

function stateFromActive(activeIndex) {
    let name, mainRoute, detailRoute;
    //TODO proper routes
    if (activeIndex == 1) {
        name = STATE.PLAN;
        mainRoute = routes.plan;
        detailRoute = routes.dayPlan;
    } else {
        name = STATE.HISTORY;
        mainRoute = routes.history;
        detailRoute = routes.dayPlanExecution;
    }
    return {
        name: name,
        mainRoute: mainRoute,
        detailRoute: detailRoute
    };
}

function setupMonthsNavigation() {
    let date = new SmartDate();
    let currentYearMonth = date.asYearMonth();
    date.setYearMonth(yearMonth.year, yearMonth.month);
    if (date.isAfter(currentYearMonth.year, currentYearMonth.month)) {
        let prev = document.getElementsByClassName("prev");
        if (prev.length > 0) {
            date.subtractMonth(1);
            let newYearMonth = date.asYearMonth();
            date.addMonth(1);
            prev[0].onclick = () => router.replaceWithParams(state.mainRoute,
                dateTimeParams.yearMonthAsParams(newYearMonth.year, newYearMonth.month));
        }
    }
    let next = document.getElementsByClassName("next");
    if (next.length > 0) {
        date.addMonth(1);
        let newYearMonth = date.asYearMonth();
        next[0].onclick = () => router.replaceWithParams(state.mainRoute,
            dateTimeParams.yearMonthAsParams(newYearMonth.year, newYearMonth.month));
    }
}

function setupDaysNavigation() {
    let notAvailableClass = "not-available";
    let days = document.getElementsByClassName("days")[0].children;
    for (let i = 0; i < days.length; i++) {
        let className = days[i].children[0].className;
        if (className !== notAvailableClass) {
            let param = dateTimeParams.yearMonthDayAsDateParam(yearMonth.year, yearMonth.month, i + 1);
            days[i].onclick = () => router.forwardWithParams(state.detailRoute, param);
        }
    }
}