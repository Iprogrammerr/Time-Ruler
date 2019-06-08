import { routes, dateTimeParams } from "../app.js";
import { SmartDate } from "../date/smart-date.js";

export function TabsNavigation(router, activeClassName = "active") {

    const _router = router;
    const _activeClassName = activeClassName;
    var _activeIndex = 0;
    var _yearMonth = new SmartDate().asYearMonth();

    this.setup = (tabsContainer, allActive = false) => {
        let tabs = tabsContainer.children;
        _activeIndex = 0;
        for (let i = 0; i < tabs.length; i++) {
            if (tabs[i].className === _activeClassName) {
                _activeIndex = i;
                break;
            }
        }
        if (_activeIndex != 0 || allActive) {
            tabs[0].onclick = () => _router.forward(routes.today);
        }
        if (_activeIndex != 1 || allActive) {
            tabs[1].onclick = () => _router.forwardWithParams(routes.plan, dateTimeParams.yearMonthAsParams(_yearMonth.year,
                _yearMonth.month));
        }
        if (_activeIndex != 2 || allActive) {
            tabs[2].onclick = () => _router.forwardWithParams(routes.history, dateTimeParams.yearMonthAsParams(_yearMonth.year,
                _yearMonth.month));
        }
        if (_activeIndex != 3 || allActive) {
            tabs[3].onclick = () => _router.forward(routes.profile);
        }
    };

    this.activeIndex = () => _activeIndex;

    this.setYearMonth = (year, month) => {
        _yearMonth.year = year;
        _yearMonth.month = month;
    };
}