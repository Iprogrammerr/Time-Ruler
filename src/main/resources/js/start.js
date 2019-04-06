import {router} from "./app.js";
import {routes} from "./app.js";
import {cookiesKeys} from "./app.js";
import {cookies} from "./app.js";

document.getElementById("continue").onclick = () => {
    if (cookies.get(cookiesKeys.signedIn) == true) {
        router.forward(routes.dashboard);
    } else {
        router.forward(routes.signIn);
    }
};
document.getElementById("start").onclick = () => router.forward(routes.signUp);