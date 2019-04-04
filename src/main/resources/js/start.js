import {router} from "./app.js";

document.getElementById("continue").onclick = () => {
    if (Math.random() < 0.5) {
        router.forward("sign-in");
    } else {
        router.forward("dashboard");
    }
};
document.getElementById("start").onclick = () => router.forward("sign-up");