import { endpoints, errors, validations } from "./app.js";
import { FormAction } from "./http/form-action.js";

const send = document.getElementById("send");
const invalidEmail = document.getElementById("invalidEmail");
const unknownEmail = document.getElementById("unknownEmail");
const inactiveAccount = document.getElementById("inactiveAccount");
const emailInput = document.querySelector("input");
const formAction = new FormAction(document.querySelector("form"));

send.onclick = () => {
    errors.clearAll(invalidEmail, unknownEmail, inactiveAccount);
    if (validations.isEmailValid(emailInput.value)) {
        formAction.submit(endpoints.passwordReset);
        send.disabled = true;
    } else {
        errors.set(invalidEmail);
    }
};