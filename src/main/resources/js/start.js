import {router} from "./app.js";
import {routes} from "./app.js";

document.getElementById("continue").onclick = () => router.forward(routes.signIn);
document.getElementById("start").onclick = () => router.forward(routes.signUp);