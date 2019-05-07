export function DeleteConfirmation(component = document.getElementById("deleteConfirmation")) {

    const _component = component;

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
        _component.style.display = "block";
        _component.style.animationName = "fadeIn";
    };

    function hide() {
        _component.style.animationName = "fadeOut";
    };
}