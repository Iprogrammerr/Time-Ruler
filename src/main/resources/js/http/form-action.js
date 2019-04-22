export function FormAction(form, method = "POST") {

    const _form = form;
    const _method = method;

    this.submit = (endpoint, ...hiddenInputs) => {
        _form.action = endpoint;
        _form.method = _method;
        for (let hi of hiddenInputs) {
            let hidden = document.createElement("input");
            hidden.setAttribute("type", "hidden");
            hidden.setAttribute("name", hi.key);
            hidden.setAttribute("value", hi.value);
            _form.appendChild(hidden);
        }
        _form.submit();
    };
}