export function Confirmation(component, inAnimation = "fadeIn", outAnimation = "fadeOut") {

    const ANIMATION_END_EVENTS = ["animationend", "webkitAnimationEnd"];
    const _component = component;
    const _messageNode = _component.querySelector("span");

    let buttons = _component.querySelectorAll("button");
    const yesButton = buttons[0];
    const noButton = buttons[1];

    const _inAnimation = inAnimation;
    const _outAnimation = outAnimation;
    const _hide = () => _component.style.display = "none";

    this.setup = (onYesClicked, onNoClicked = () => {}) => {
        yesButton.onclick = () => {
            onYesClicked();
            this.hide();
        };
        noButton.onclick = () => {
            onNoClicked();
            this.hide();
        };
    };

    this.setMessage = (message) => {
        _messageNode.replaceChild(document.createTextNode(message), _messageNode.firstChild);
    };

    this.show = () => {
        for (let e of ANIMATION_END_EVENTS) {
            _component.removeEventListener(e, _hide);
        }
        _component.style.display = "block";
        _component.style.animationName = _inAnimation;
    };

    this.hide = () => {        
        for (let e of ANIMATION_END_EVENTS) {
            _component.addEventListener(e, _hide);
        }
        _component.style.animationName = _outAnimation;
    };
}