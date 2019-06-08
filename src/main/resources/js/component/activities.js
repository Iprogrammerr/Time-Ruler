import { hiddenDataKeys, paramsKeys, parametrizedEndpoints } from "../app.js";
import { HttpConnections } from "../http/http-connections.js";

export function Activities(confirmation, httpConnections = new HttpConnections()) {

    const ACTIVITIES_CLASS = "activities";
    const CLOSE_CLASS = "close";
    const VISIBLE_CLASS = "visible";
    const HIDDEN_CLASS = "hidden";
    const _confirmation = confirmation;
    const _httpConnections = httpConnections;

    this.setupPlanNavigation = () => setupNavigation("", "", "");

    this.setupHistoryNavigation = (deleteConfirmation, doneConfirmation, notDoneConfirmation) =>
        setupNavigation(deleteConfirmation, doneConfirmation, notDoneConfirmation);

    function setupNavigation(deleteConfirmation, doneConfirmation, notDoneConfirmation) {
        let activities = document.getElementsByClassName(ACTIVITIES_CLASS)[0];
        for (let a of activities.children) {
            let id = a.getAttribute(hiddenDataKeys.id);
            a.onclick = () => router.forwardWithParam(routes.activity, paramsKeys.id, id);
            a.getElementsByClassName(CLOSE_CLASS)[0].onclick = (e) => {
                e.stopPropagation();
                if (deleteConfirmation.length > 0) {
                    _confirmation.setMessage(deleteConfirmation);
                }
                _confirmation.setup(() => {
                    _httpConnections.delete(parametrizedEndpoints.deleteActivity(id)).then(r => {
                        removeActivity(activities, a);
                    }).catch(e => alert(e));
                });
                _confirmation.show();
            };
            if (doneConfirmation.length > 0 && notDoneConfirmation.length > 0) {
                let spans = a.querySelectorAll("span");
                setupDoneNotDone(id, spans[0], spans[1], doneConfirmation, notDoneConfirmation);
            }
        }
    };

    function setupDoneNotDone(id, done, notDone, doneConfirmation, notDoneConfirmation) {
        done.onclick = e => {
            e.stopPropagation();
            _confirmation.setMessage(notDoneConfirmation);
            _confirmation.setup(() => {
                _httpConnections.put(parametrizedEndpoints.setActivityNotDone(id)).then(r => {
                    notDone.className = VISIBLE_CLASS;
                    done.className = HIDDEN_CLASS;
                }).catch(e => alert(e));
            });
            _confirmation.show();
        };
        notDone.onclick = (e) => {
            e.stopPropagation();
            _confirmation.setMessage(doneConfirmation);
            _confirmation.setup(() => {
                httpConnections.put(parametrizedEndpoints.setActivityDone(id)).then(r => {
                    done.className = VISIBLE_CLASS;
                    notDone.className = HIDDEN_CLASS;
                }).catch(e => alert(e));
            });
            _confirmation.show();
        };
    };

    function removeActivity(activities, activity) {
        activity.remove();
        if (activities.children.length == 0) {
            document.getElementsByClassName(HIDDEN_CLASS)[0].className = VISIBLE_CLASS;
        }
    };
}