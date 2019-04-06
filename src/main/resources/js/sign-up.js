import { endpoints } from "./app.js";
import { validations } from "./app.js";
import { errors } from "./app.js";

const errorsParagraphs = document.getElementsByClassName("error");
const inputs = document.querySelectorAll("input");
const form = document.querySelector("form");
const signUp = document.getElementById("signUp");

addEventListener("submit", e => e.preventDefault());
signUp.onclick = () => {
    if (isFormValid()) {
        form.action = `${endpoints.signUp}`;
        form.method = "POST";
        form.submit();
        signUp.disabled = true;
    }
};

function isFormValid() {
    let valid = true;
    clearErrors();
    if (!validations.isEmailValid(inputs[0].value)) {
        valid = false;
        setError(errorsParagraphs[0], errors.invalidEmail);
    }
    if (!validations.isNameValid(inputs[1].value)) {
        valid = false;
        setError(errorsParagraphs[1], errors.invalidLogin);
    }
    if (!validations.isPasswordValid(inputs[2].value)) {
        valid = false;
        setError(errorsParagraphs[2], errors.invalidPassword);
    }
    if (!validations.areInputsEqual(inputs[2].value, inputs[3].value)) {
       valid = false;
       setError(errorsParagraphs[2], errors.passwordsMismatch); 
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