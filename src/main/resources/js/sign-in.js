import { endpoints } from "./app.js";
import { validations } from "./app.js";
import { errors } from "./app.js";

const activationParam = "activation=";
const errorsParagraphs = document.getElementsByClassName("error");
const inputs = document.querySelectorAll("input");
const form = document.querySelector("form");
const signIn = document.getElementById("signIn");

let params = window.location.search;
console.log(`Current url params = ${params}`);
if (params.indexOf(activationParam) > -1 ) {
    document.querySelector("h1").style.display = "block";
}

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
    if (!validations.isEmailValid(emailOrLogin) || !validations.isNameValid(emailOrLogin)) {
        valid = false;
        let email = emailOrLogin && emailOrLogin.contains('@');
        setError(errorsParagraphs[0], email ? errors.invalidEmail : errors.invalidLogin);
    }
    if (!validations.isPasswordValid(inputs[1].value)) {
        valid = false;
        setError(errorsParagraphs[1], errors.invalidPassword);
    }
    return valid;
}

function clearErrors() {
    for (let ep of errorsParagraphs) {
        ep.style.display = "none";
    }
}

function setError(errorComponent, message) {
    errorComponent.style.display = "block";
    errorComponent.innerHTML = message;
}