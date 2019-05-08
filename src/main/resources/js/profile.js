import { router, routes, tabsNavigation } from "./app.js";

const HIDDEN_DISPLAY = "none";
const passwords = document.getElementById("passwords");
let showPasswords = passwords.style.display == HIDDEN_DISPLAY || passwords.className == "hidden";

tabsNavigation.setup(document.querySelector("div"));
window.addEventListener("submit", e => e.preventDefault());
document.getElementById("save").onclick = () => {
    //save
};
document.getElementById("changePassword").onclick = () => {
    if (showPasswords) {
        passwords.style.display = "block";
    } else {
        passwords.style.display = HIDDEN_DISPLAY;
    }
    showPasswords = !showPasswords;
};
document.getElementById("logout").onclick = () => router.forward(routes.signOut);