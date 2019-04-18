import { setupTabsNavigation } from "./app.js";
import { router } from "./app.js";
import { routes } from "./app.js";
import { dateTimeParams } from "./app.js";
import { SmartDate } from "./smart-date.js";

const yearMonth = dateTimeParams.currentYearMonthFromUrl();
setupTabsNavigation(document.querySelector("div"), 1);
setupMonthsNavigation();
setupDaysNavigation();

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
            prev[0].onclick = () => router.replaceWithParams(routes.plan, 
                dateTimeParams.yearMonthAsParams(newYearMonth.year, newYearMonth.month));
        }
    }
    let next = document.getElementsByClassName("next");
    if (next.length > 0) {
        date.addMonth(1);
        let newYearMonth = date.asYearMonth();
        next[0].onclick = () => router.replaceWithParams(routes.plan,
            dateTimeParams.yearMonthAsParams(newYearMonth.year, newYearMonth.month));
    }
}

function setupDaysNavigation() {
    let notAvailableClass = "not-available";
    let days = document.getElementsByClassName("days")[0].children;
    for (let i = 0; i < days.length; i++) {
        let className = days[i].children[0].className;
        if (className !== notAvailableClass) {
            days[i].onclick = () => router.forwardWithParams(
                routes.dayPlan,  dateTimeParams.yearMonthDayAsParams(yearMonth.year, yearMonth.month, i + 1)
            );
        }
    }
}