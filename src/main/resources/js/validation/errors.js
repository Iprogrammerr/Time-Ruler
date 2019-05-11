export function Errors(errorClassName = "error") {

    const _errorClassName = errorClassName;

    this.set = (element) => {
        element.className = _errorClassName;
        element.style.display = "block";
    };

    this.clear = (element) => element.style.display = "none";

    this.clearAll = (...elements) => {
        for (let e of elements) {
            console.log(e);
            this.clear(e);
        }
    };
}