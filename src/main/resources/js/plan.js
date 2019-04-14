import { setupTabsNavigation } from "./app.js";
import { router } from "./app.js";
import { routes } from "./app.js";
import { params } from "./app.js";

const offset = offsetFromQuery();
setupTabsNavigation(document.querySelector("div"), 1);
setupMonthsNavigation();
setupDaysNavigation();

function offsetFromQuery() {
    let offset;
    let query = location.search;
    if (query.length > 0) {
        let queryParams = query.substring(1).split("&");
        for (let p of queryParams) {
            let keyValue = p.split("=");
            if (keyValue[0] === params.offset) {
                offset = isNaN(keyValue[1]) ? 0 : parseInt(keyValue[1]);
            }
        }
    } else {
        offset = 0;
    }
    return offset;
}

function setupMonthsNavigation() {
    if (offset > 0) {
        let prev = document.getElementsByClassName("prev");
        if (prev.length > 0) {
            prev[0].onclick = () => router.replaceWithParams(routes.plan, { key: params.offset, value: offset - 1 });
        }
    }
    let next = document.getElementsByClassName("next");
    if (next.length > 0) {
        next[0].onclick = () => router.replaceWithParams(routes.plan, { key: params.offset, value: offset + 1 });
    }
}

function setupDaysNavigation() {
    let notAvailableClass = "not-available";
    let days = document.getElementsByClassName("days")[0].children;
    for (let i = 0; i < days.length; i++) {
        let className = days[i].children[0].className;
        if (className !== notAvailableClass) {
            days[i].onclick = () => router.forwardWithParams(
                routes.dayPlan, { key: params.offset, value: offset }, { key: params.day, value: i + 1 }
            );
        }
    }
}