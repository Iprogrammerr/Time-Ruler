import {router} from "./app.js";
import {routes} from "./app.js";

document.getElementById("add").onclick = () => router.forward(routes.editDayPlan);