import { routes } from "../app.js";
import { dateTimeParams } from "../app.js";

export function TabsNavigation(router, activeClassName = "active") {

    const _router = router;
    const _activeClassName = activeClassName;
    var _activeIndex = 0;

    this.setup = (tabsContainer) => {
        let tabs = tabsContainer.children;
        _activeIndex = 0;
        for (let i = 0; i < tabs.length; i++) {
            if (tabs[i].className === _activeClassName) {
                _activeIndex = i;
            }
        }
        if (_activeIndex != 0) {
            tabs[0].onclick = () => router.forward(routes.today);
        }
        if (_activeIndex != 1) {
            tabs[1].onclick = () => router.forwardWithParams(routes.plan, dateTimeParams.currentYearMonthAsParams());
        }
        if (_activeIndex != 2) {
            tabs[2].onclick = () => router.forwardWithParams(routes.history, dateTimeParams.currentYearMonthAsParams());
        }
        if (_activeIndex != 3) {
            tabs[3].onclick = () => router.forward(routes.profile);
        }
    };

    this.activeIndex = () => _activeIndex;
}