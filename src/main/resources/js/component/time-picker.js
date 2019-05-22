export function TimePicker(root, visibleClassName = "time-options-visible") {

    const _visibleClassName = visibleClassName;

    let times = root.querySelectorAll("button");
    const _hour = times[0];
    const _minute = times[1];

    let timesOptions = root.getElementsByClassName("time-options");
    const _hourOptions = timesOptions[0];
    const _minuteOptions = timesOptions[1];

    this.setup = () => {
        _hour.onclick = () => {
            _hourOptions.classList.toggle(_visibleClassName);
        };
        _minute.onclick = () => _minuteOptions.classList.toggle(_visibleClassName);
        _hourOptions.onclick = e => {
            let idx = idxInCollection(_hourOptions.children, e.target);
            if (idx >= 0) {
                _hour.textContent = formattedTime(idx);
            }
        };
        _minuteOptions.onclick = e => {
            let idx = idxInCollection(_minuteOptions.children, e.target);
            if (idx >= 0) {
                _minute.textContent = formattedTime(idx);
            }
        };
        window.addEventListener("click", e => {
            if (e.target !== _hour) {
                if (_hourOptions.classList.contains(_visibleClassName)) {
                    _hourOptions.classList.remove(_visibleClassName);
                } 
            }
            if (e.target !== _minute) {
                if (_minuteOptions.classList.contains(_visibleClassName)) {
                    _minuteOptions.classList.remove(_visibleClassName);
                }
            }
        });
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
}