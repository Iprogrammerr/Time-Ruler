import { router, routes, tabsNavigation, endpoints, validations, errors } from "./app.js";
import { FormAction } from "./http/form-action.js";
import { Confirmation } from "./component/confirmation.js";

const HIDDEN_DISPLAY = "none";
const emailForm = document.getElementById("emailForm");
const emailChanged = document.getElementById("emailChanged");
const invalidEmail = document.getElementById("invalidEmail");
const usedEmail = document.getElementById("usedEmail");
const saveEmail = document.getElementById("saveEmail");

const nameForm = document.getElementById("nameForm");
const nameChanged = document.getElementById("nameChanged");
const invalidName = document.getElementById("invalidName");
const usedName = document.getElementById("usedName");
const saveName = document.getElementById("saveName");

const passwordForm = document.getElementById("passwordForm");
const invalidOldPassword = document.getElementById("invalidOldPassword");
const notUserPassword = document.getElementById("notUserPassword");
const invalidNewPassword = document.getElementById("invalidNewPassword");
const savePassword = document.getElementById("savePassword");
let showPasswordForm = passwordForm.style.display == HIDDEN_DISPLAY || passwordForm.className == "hidden";

const confirmation = new Confirmation(document.getElementById("confirmation"));

let allInputs = document.querySelectorAll("input");
const inputs = {
    email: allInputs[0],
    name: allInputs[2],
    oldPassword: allInputs[4],
    newPassword: allInputs[5]
};

const previousEmail = inputs.email.value;
const previousName = inputs.name.value;

tabsNavigation.setup(document.querySelector("div"));
window.addEventListener("submit", e => e.preventDefault());

saveEmail.onclick = () => {
    let email = inputs.email.value;
    if (email === previousEmail) {
        return;
    }
    clearMessages();
    errors.clearAll(invalidEmail, usedEmail);
    if (validations.isEmailValid(email)) {
        confirmation.setup(() => {
            new FormAction(emailForm).submit(endpoints.profileEmail);
            saveEmail.disabled = true;
        });
        confirmation.show();
    } else {
        errors.set(invalidEmail);
    }
};

function clearMessages() {
    emailChanged.style.display = HIDDEN_DISPLAY;
    nameChanged.style.display = HIDDEN_DISPLAY;
};

saveName.onclick = () => {
    let name = inputs.name.value;
    if (name === previousName) {
        return;
    }
    clearMessages();
    errors.clearAll(invalidName, usedName);
    if (validations.isNameValid(name)) {
        confirmation.setup(() => {
            new FormAction(nameForm).submit(endpoints.profileName);
            saveName.disabled = true;
        });
        confirmation.show();
    } else {
        errors.set(invalidName);
    }
};

savePassword.onclick = () => {
    clearMessages();
    errors.clearAll(invalidOldPassword, notUserPassword, invalidNewPassword);
    let oldValid = validations.isPasswordValid(inputs.oldPassword.value);
    let newValid = validations.isPasswordValid(inputs.newPassword.value);
    if (oldValid && newValid) {
        confirmation.setup(() => {
            new FormAction(passwordForm).submit(endpoints.profilePassword);
            savePassword.disabled = true;
        });
        confirmation.show();
    } else {
        if (!oldValid) {
            errors.set(invalidOldPassword);
        }
        if (!newValid) {
            errors.set(invalidNewPassword);
        }
    }
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