import { tabsNavigation, router, routes, dateTimeParams, parametrizedEndpoints, paramsKeys, urlParams, 
    errors, validations } from "./app.js";
import { FormAction } from "./http/form-action.js";
import { TimePicker } from "./component/time-picker.js";

const activityId = urlParams.getOrDefault(paramsKeys.id, 0);
const date = activityId > 0 ? "" : dateTimeParams.dateFromUrl().asIsoDateString();

const form = document.querySelector("form");
let formInputs = form.querySelectorAll("input");
const inputs = {
    name: formInputs[0],
    startTime: formInputs[1],
    endTime: formInputs[2]
};
const nameError = document.getElementById("invalidName");
const startTimePicker = new TimePicker(document.getElementById("startTimePicker"));
const endTimePicker = new TimePicker(document.getElementById("endTimePicker"));

const description = document.querySelector("textarea");
const saveActivity = document.getElementById("save");

tabsNavigation.setup(document.querySelector("div"), true);
document.getElementById("recent").onclick = () => {
    let params = new Map();
    if (activityId > 0) {
        params.set(paramsKeys.id, activityId);
    } else {
        params.set(paramsKeys.date, date);
        params.set(paramsKeys.plan, urlParams.getOrDefault(paramsKeys.plan, true));
    }
    router.forwardWithParams(routes.activities, params);
};

startTimePicker.setup();
endTimePicker.setup();
startTimePicker.setChangeListener(() => {
    if (isStartTimeGreater()) {
        endTimePicker.setTime(startTimePicker.hour(), startTimePicker.minute());
    }
});

function isStartTimeGreater() {
    return (endTimePicker.hour() < startTimePicker.hour()) ||
        (endTimePicker.hour() == startTimePicker.hour() && endTimePicker.minute() < startTimePicker.minute());
};

endTimePicker.setChangeListener(() => {
    if (isStartTimeGreater()) {
        startTimePicker.setTime(endTimePicker.hour(), endTimePicker.minute());
    }
});

description.oninput = () => {
    if (description.clientHeight != description.scrollHeight) {
        description.style.height = `${description.scrollHeight}px`;
    }
};

saveActivity.onclick = () => {
    if (isFormValid()) {
        let endpoint;
        if (activityId > 0) {
            endpoint = parametrizedEndpoints.updateActivity(activityId);
        } else {
            endpoint = parametrizedEndpoints.createActivity(date);
        }
        setFormTimes();
        new FormAction(form).submit(endpoint);
        saveActivity.disabled = true;
    }
};

function isFormValid() {
    errors.clear(nameError);
    let nameValid = validations.isNameValid(inputs.name.value, true);
    if (!nameValid) {
        errors.set(nameError);
    }
    return nameValid;
};

function setFormTimes() {
    inputs.startTime.value = `${startTimePicker.hour()}:${startTimePicker.minute()}`;
    inputs.endTime.value = `${endTimePicker.hour()}:${endTimePicker.minute()}`;
};

