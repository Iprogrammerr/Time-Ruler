import { router, routes, tabsNavigation } from "./app.js";

const HIDDEN_DISPLAY = "none";
const invalidEmail = document.getElementById("invalidEmail");
const usedEmail = document.getElementById("usedEmail");
const emailConfirmationSent = document.getElementById("emailConfirmationSent");
const invalidName = document.getElementById("invalidName");
const usedName = document.getElementById("usedName");
const passwordForm = document.getElementById("passwordForm");
let showPasswordForm = passwordForm.style.display == HIDDEN_DISPLAY || passwordForm.className == "hidden";

tabsNavigation.setup(document.querySelector("div"));
window.addEventListener("submit", e => e.preventDefault());
document.getElementById("saveEmail").onclick = () => {
    //saveEmail
};
document.getElementById("saveName").onclick = () => {
    //saveName
};
document.getElementById("savePassword").onclick = () => {
    //savePassword
};
document.getElementById("changePassword").onclick = () => {
    if (showPasswordForm) {
        passwordForm.style.display = "block";
    } else {
        passwordForm.style.display = HIDDEN_DISPLAY;
    }
    showPasswordForm = !showPasswordForm;
};
document.getElementById("logout").onclick = () => router.forward(routes.signOut);