import { errors, validations } from "./app.js";

const change = document.getElementById("change");
const invalidPassword = document.getElementById("invalidPassword");
const passwordInput = document.querySelector("input");
const form = document.querySelector("form");

window.addEventListener("submit", e => e.preventDefault());
change.onclick = () => {
    errors.clear(invalidPassword);
    if (validations.isPasswordValid(passwordInput.value)) {
        form.submit();
        send.disabled = true;
    } else {
        errors.set(invaliPassword);
    }
};