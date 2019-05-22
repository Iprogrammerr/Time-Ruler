export function TimePicker(root, visibleClassName = "time-options-visible") {

    const _visibleClassName = visibleClassName;

    let times = root.querySelectorAll("button");
    const _hour = times[0];
    const _minute = times[1];

    let timesOptions = root.getElementsByClassName("time-options");
    const _hourOptions = timesOptions[0];
    const _minuteOptions = timesOptions[1];

    let _changeListener = () => { };

    this.setup = () => {
        _hour.onclick = () => {
            if (_hourOptions.classList.toggle(_visibleClassName)) {
                _hourOptions.scroll(0, hourScrollValue());
            }
        };
        _minute.onclick = () => {
            if (_minuteOptions.classList.toggle(_visibleClassName)) {
                _minuteOptions.scroll(0, minuteScrollValue());
            }
        };
        _hourOptions.onclick = e => {
            let idx = idxInCollection(_hourOptions.children, e.target);
            if (idx >= 0) {
                _hour.textContent = formattedTime(idx);
                _changeListener();
            }
        };
        _minuteOptions.onclick = e => {
            let idx = idxInCollection(_minuteOptions.children, e.target);
            if (idx >= 0) {
                _minute.textContent = formattedTime(idx);
                _changeListener();
            }
        };
        window.addEventListener("click", e => {
            if (e.target !== _hour && _hourOptions.classList.contains(_visibleClassName)) {
                _hourOptions.classList.remove(_visibleClassName);
            }
            if (e.target !== _minute && _minuteOptions.classList.contains(_visibleClassName)) {
                _minuteOptions.classList.remove(_visibleClassName);
            }
        });
    };

    function hourScrollValue() {
        let hourHeight = _hourOptions.children[0].clientHeight;
        return hourHeight * parseInt(_hour.textContent);
    };

    function minuteScrollValue() {
        let minuteHeight = _minuteOptions.children[0].clientHeight;
        return minuteHeight * parseInt(_minute.textContent);
    };

    function idxInCollection(collection, target) {
        for (let i = 0; i < collection.length; i++) {
            if (collection[i] == target) {
                return i;
            }
        }
        return -1;
    };

    function formattedTime(idx) {
        let formatted;
        if (idx < 10) {
            formatted = `0${idx}`;
        } else {
            formatted = `${idx}`;
        }
        return formatted;
    };

    this.hour = () => _hour.textContent;

    this.minute = () => _minute.textContent;

    this.setChangeListener = (listener) => _changeListener = listener;

    this.setTime = (hour, minute) => {
        _hour.textContent = `${hour}`;
        _minute.textContent = `${minute}`;
    };
}