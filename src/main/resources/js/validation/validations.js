export function Validations(minNameLength = 3, minPasswordLength = 6) {
    //TODO check if they are valid
    const alphanumericRegex = /^[a-zA-Z0-9]+$/;
    const emailRegex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    const _minNameLength = minNameLength;
    const _minPasswordLength = minPasswordLength;

    this.isNameValid = (name) => name && alphanumericRegex.test(name) && name.length >= _minNameLength;
    this.isEmailValid = (email) => email && emailRegex.test(email);
    this.isPasswordValid = (password) => password.length >= _minPasswordLength;
    this.areInputsEqual = (first, second) => first.trim() === second.trim();
}