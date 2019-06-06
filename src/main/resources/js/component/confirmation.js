export function Confirmation(component, inAnimation = "fadeIn", outAnimation = "fadeOut") {

    const _animationEndEvents = ["animationend", "webkitAnimationEnd"];
    const _component = component;
    const _messageNode = _component.querySelector("span");

    let buttons = _component.querySelectorAll("button");
    const yes = buttons[0];
    const no = buttons[1];

    const _inAnimation = inAnimation;
    const _outAnimation = outAnimation;
    const _hide = () => _component.style.display = "none";

    this.setup = (onYesClicked, onNoClicked = () => {}) => {
        yes.onclick = () => {
            onYesClicked();
            this.hide();
        };
        no.onclick = () => {
            onNoClicked();
            this.hide();
        };
    };

    this.setMessage = (message) => {
        _messageNode.replaceChild(document.createTextNode(message), _messageNode.firstChild);
    };

    this.show = () => {
        for (let e of _animationEndEvents) {
            _component.removeEventListener(e, _hide);
        }
        _component.style.display = "block";
        _component.style.animationName = _inAnimation;
    };

    this.hide = () => {        
        for (let e of _animationEndEvents) {
            _component.addEventListener(e, _hide);
        }
        _component.style.animationName = _outAnimation;
    };
}