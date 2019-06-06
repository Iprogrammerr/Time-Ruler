import { errors, validations } from "./app.js";

const save = document.getElementById("save");
const invalidPassword = document.getElementById("invalidPassword");
const passwordInput = document.querySelector("input");
const form = document.querySelector("form");

save.onclick = () => {
    errors.clear(invalidPassword);
    if (validations.isPasswordValid(passwordInput.value)) {
        form.submit();
        save.disabled = true;
    } else {
        errors.set(invalidPassword);
    }
};