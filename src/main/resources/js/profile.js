import { router, routes, tabsNavigation } from "./app.js";

tabsNavigation.setup(document.querySelector("div"));
window.addEventListener("submit", e => e.preventDefault());
document.getElementById("save").onclick = () => {
    //save
};
document.getElementById("resetPassword").onclick = () => {
    //reset password
};
document.getElementById("logout").onclick = () => router.forward(routes.signOut);