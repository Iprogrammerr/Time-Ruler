export function DeleteConfirmation(component = document.getElementById("deleteConfirmation"), 
    inAnimation = "fadeIn", outAnimation = "fadeOut") {

    const _animationEndEvents = ["animationend", "webkitAnimationEnd"];
    const _component = component;
    const _inAnimation = inAnimation;
    const _outAnimation = outAnimation;
    const _hide = () => _component.style.display = "none";

    this.setup = (onYesClicked, onNoClicked = () => {}) => {
        document.getElementById("yes").onclick = () => {
            onYesClicked();
            hide();
        };
        document.getElementById("no").onclick = () => {
            onNoClicked();
            hide();
        };
    };

    this.show = () => {
        for (let e of _animationEndEvents) {
            _component.removeEventListener(e, _hide);
        }
        _component.style.display = "block";
        _component.style.animationName = _inAnimation;
    };

    function hide() {        
        for (let e of _animationEndEvents) {
            _component.addEventListener(e, _hide);
        }
        _component.style.animationName = _outAnimation;
    };
}