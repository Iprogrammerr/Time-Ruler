import { setupTabsNavigation } from "./app.js";
import { router } from "./app.js";
import { routes } from "./app.js";
import { params } from "./app.js";

setupTabsNavigation(document.querySelector("div"), 1);
const offset = offsetFromQuery();
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

function offsetFromQuery() {
    let offset;
    let query = location.search;
    if (query.length > 0) {
        let queryParams = query.substring(1).split("&");
        for (let p of queryParams) {
            let keyValue = p.split("=");
            console.log(`Key value = ${keyValue}`);
            if (keyValue[0] === params.offset) {
                offset = isNaN(keyValue[1]) ? 0 : parseInt(keyValue[1]);
            }
        }
    } else {
        offset = 0;
    }
    return offset;
}