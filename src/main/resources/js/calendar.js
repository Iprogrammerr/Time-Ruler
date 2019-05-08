import { tabsNavigation, router, routes, dateTimeParams } from "./app.js";
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
};

function setupMonthsNavigation() {
    let date = new SmartDate();
    date.setYearMonth(yearMonth.year, yearMonth.month);
    let prev = document.getElementsByClassName("prev");
    if (prev.length > 0) {
        date.subtractMonth(1);
        let newYearMonth = date.asYearMonth();
        date.addMonth(1);
        prev[0].onclick = () => router.replaceWithParams(state.mainRoute,
            dateTimeParams.yearMonthAsParams(newYearMonth.year, newYearMonth.month));
    }
    let next = document.getElementsByClassName("next");
    if (next.length > 0) {
        date.addMonth(1);
        let newYearMonth = date.asYearMonth();
        next[0].onclick = () => router.replaceWithParams(state.mainRoute,
            dateTimeParams.yearMonthAsParams(newYearMonth.year, newYearMonth.month));
    }
};

function setupDaysNavigation() {
    let notAvailableClass = "not-available";
    let days = document.getElementsByClassName("days")[0].children;
    let date = new SmartDate();
    for (let i = 0; i < days.length; i++) {
        let className = days[i].children[0].className;
        if (className !== notAvailableClass) {
            let day = i + 1;
            if (date.isNow(yearMonth.year, yearMonth.month, day)) {
                days[i].onclick = () => router.forward(routes.today);
            } else {
                let param = dateTimeParams.yearMonthDayAsDateParam(yearMonth.year, yearMonth.month, day);
                days[i].onclick = () => router.forwardWithParams(state.detailRoute, param);
            }
        }
    }
};
