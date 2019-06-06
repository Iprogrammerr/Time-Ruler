import { endpoints, validations, errors } from "./app.js";
import { FormAction } from "./http/form-action.js";

const emailError = document.getElementById("invalidEmail");
const emailTaken = document.getElementById("emailTaken");
const nameError = document.getElementById("invalidName");
const nameTaken = document.getElementById("nameTaken");
const passwordError = document.getElementById("invalidPassword");
const passwordsMismatch = document.getElementById("passwordsMismatch");

const form = document.querySelector("form");
let inputsFields = form.querySelectorAll("input");
const inputs = {
    email: inputsFields[0],
    name: inputsFields[1],
    password: inputsFields[2],
    repassword: inputsFields[3]
};

const signUp = document.getElementById("signUp");

signUp.onclick = () => {
    if (isFormValid()) {
        new FormAction(form).submit(`${endpoints.signUp}`);
        signUp.disabled = true;
    }
};

function isFormValid() {
    let valid = true;
    errors.clearAll(emailError, emailTaken, nameError, nameTaken, passwordError, passwordsMismatch);
    if (!validations.isEmailValid(inputs.email.value)) {
        valid = false;
        errors.set(emailError);
    }
    if (!validations.isNameValid(inputs.name.value)) {
        valid = false;
        errors.set(nameError);
    }
    if (!validations.isPasswordValid(inputs.password.value)) {
        valid = false;
        errors.set(passwordError);
    }
    if (!validations.areInputsEqual(inputs.password.value, inputs.repassword.value)) {
       valid = false;
       errors.set(passwordsMismatch); 
    }
    return valid;
};