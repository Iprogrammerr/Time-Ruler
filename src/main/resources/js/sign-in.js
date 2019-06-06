import { endpoints, validations, errors } from "./app.js";
import { FormAction } from "./http/form-action.js";

const emailNameError = document.getElementById("emailNameError");
const nonExistentUser = document.getElementById("nonExistentUser");
const inactiveAccount = document.getElementById("inactiveAccount");
const passwordError = document.getElementById("passwordError");
const notUserPassword = document.getElementById("notUserPassword");
const inputs = document.querySelectorAll("input");
const form = document.querySelector("form");
const signIn = document.getElementById("signIn");

signIn.onclick = () => {
    if (isFormValid()) {
        new FormAction(form).submit(endpoints.signIn);
        signIn.disabled = true;
    }
};

function isFormValid() {
    let valid = true;
    errors.clearAll(emailNameError, nonExistentUser, inactiveAccount, passwordError, notUserPassword);
    let emailOrName = inputs[0].value;
    if (!validations.isEmailValid(emailOrName) && !validations.isNameValid(emailOrName)) {
        valid = false;
        errors.set(emailNameError);
    }
    if (!validations.isPasswordValid(inputs[1].value)) {
        valid = false;
        errors.set(passwordError);
    }
    return valid;
};