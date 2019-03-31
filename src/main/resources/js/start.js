import {Router} from "./router.js";

const router = new Router();
document.getElementById("continue").onclick = () => {
    if (Math.random() < 0.5) {
        router.forward("sign-in.html");
    } else {
        router.forward("dashboard.html");
    }
};
document.getElementById("start").onclick = () => router.forward("sign-up.html");