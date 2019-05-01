import { endpoints, validations } from "./app.js";
import { FormAction } from "./http/form-action.js";

const errorsParagraphs = document.getElementsByClassName("error");
const inputs = document.querySelectorAll("input");
const form = document.querySelector("form");
const signUp = document.getElementById("signUp");

addEventListener("submit", e => e.preventDefault());
signUp.onclick = () => {
    if (isFormValid()) {
        new FormAction(form).submit(`${endpoints.signUp}`);
        signUp.disabled = true;
    }
};

function isFormValid() {
    let valid = true;
    clearErrors();
    if (!validations.isEmailValid(inputs[0].value)) {
        valid = false;
        setError(errorsParagraphs[0]);
    }
    if (!validations.isNameValid(inputs[1].value)) {
        valid = false;
        setError(errorsParagraphs[1]);
    }
    if (!validations.isPasswordValid(inputs[2].value)) {
        valid = false;
        setError(errorsParagraphs[2]);
    }
    if (!validations.areInputsEqual(inputs[2].value, inputs[3].value)) {
       valid = false;
       setError(errorsParagraphs[3]); 
    }
    return valid;
};

function clearErrors() {
    for (let ep of errorsParagraphs) {
        ep.style.display = "none";
    }
};

function setError(errorComponent) {
    errorComponent.style.display = "block";
};