export function Confirmation(component, messageNode = component.querySelector("span"), inAnimation = "fadeIn", outAnimation = "fadeOut") {

    const _animationEndEvents = ["animationend", "webkitAnimationEnd"];
    const _component = component;
    const _messageNode = messageNode;
    const _inAnimation = inAnimation;
    const _outAnimation = outAnimation;
    const _hide = () => _component.style.display = "none";

    this.setup = (onYesClicked, onNoClicked = () => {}) => {
        document.getElementById("yes").onclick = () => {
            onYesClicked();
            this.hide();
        };
        document.getElementById("no").onclick = () => {
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