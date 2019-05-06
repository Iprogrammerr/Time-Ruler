import { endpoints, validations, errors } from "./app.js";
import { FormAction } from "./http/form-action.js";

const emailError = document.getElementById("invalidEmail");
const emailTaken = document.getElementById("emailTaken");
const nameError = document.getElementById("invalidName");
const nameTaken = document.getElementById("nameTaken");
const passwordError = document.getElementById("invalidPassword");
const passwordsMismatch = document.getElementById("passwordsMismatch");
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
    errors.clearAll(emailError, emailTaken, nameError, nameTaken, passwordError, passwordsMismatch);
    if (!validations.isEmailValid(inputs[0].value)) {
        valid = false;
        errors.set(emailError);
    }
    if (!validations.isNameValid(inputs[1].value)) {
        valid = false;
        errors.set(nameError);
    }
    if (!validations.isPasswordValid(inputs[2].value)) {
        valid = false;
        errors.set(passwordError);
    }
    if (!validations.areInputsEqual(inputs[2].value, inputs[3].value)) {
       valid = false;
       errors.set(passwordsMismatch); 
    }
    return valid;
};