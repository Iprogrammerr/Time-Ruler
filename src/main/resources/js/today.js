import {routes} from "./app.js";
import {router} from "./app.js";

let tabs = document.querySelectorAll("button");
tabs[1].onclick = () => router.replace(routes.plan);
tabs[2].onclick = () => router.replace(routes.history);
tabs[3].onclick = () => router.replace(routes.profile);