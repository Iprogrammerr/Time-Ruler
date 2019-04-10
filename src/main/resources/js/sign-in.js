import { endpoints } from "./app.js";
import { validations } from "./app.js";
import { errors } from "./app.js";

const emailLoginError = document.getElementById("emailLoginError");
const passwordError = document.getElementById("passwordError");
const inputs = document.querySelectorAll("input");
const form = document.querySelector("form");
const signIn = document.getElementById("signIn");

addEventListener("submit", e => e.preventDefault());
signIn.onclick = () => {
    if (isFormValid()) {
        form.action = `${endpoints.signIn}`;
        form.method = "POST";
        form.submit();
        signIn.disabled = true;
    }
};

function isFormValid() {
    let valid = true;
    clearErrors();
    let emailOrLogin = inputs[0].value;
    if (!validations.isEmailValid(emailOrLogin) && !validations.isNameValid(emailOrLogin)) {
        valid = false;
        let email = emailOrLogin && emailOrLogin.includes('@');
        setError(emailLoginError, email ? errors.invalidEmail : errors.invalidLogin);
    }
    if (!validations.isPasswordValid(inputs[1].value)) {
        valid = false;
        setError(passwordError, errors.invalidPassword);
    }
    return valid;
}

function clearErrors() {
    emailLoginError.style.display = "none";
    passwordError.style.display = "none";
}

function setError(errorComponent, message) {
    errorComponent.style.display = "block";
    errorComponent.innerHTML = message;
}