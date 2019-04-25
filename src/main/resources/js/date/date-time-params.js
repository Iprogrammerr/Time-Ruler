import { SmartDate } from "./smart-date.js";

export function DateTimeParams(urlParams, paramsKeys) {

    const _urlParams = urlParams;
    const _paramsKeys = paramsKeys;

    this.currentYearMonthFromUrl = () => {
        let yearMonth = new SmartDate().asYearMonth();
        return this.yearMonthFromUrl(yearMonth.year, yearMonth.month);
    };

    this.yearMonthFromUrl = (defaultYear, defaultMonth) => {
        return {
            year: _urlParams.getOrDefault(_paramsKeys.year, defaultYear),
            month: _urlParams.getOrDefault(_paramsKeys.month, defaultMonth)
        };
    };

    this.currentYearMonthDayFromUrl = () => {
        let yearMonthDay = new SmartDate().asYearMonthDay();
        return this.yearMonthDayFromUrl(yearMonthDay.year, yearMonthDay.month, yearMonthDay.day);
    };

    this.yearMonthDayFromUrl = (defaultYear, defaultMonth, defaultDay) => {
        return {
            year: _urlParams.getOrDefault(_paramsKeys.year, defaultYear),
            month: _urlParams.getOrDefault(_paramsKeys.month, defaultMonth),
            day: _urlParams.getOrDefault(_paramsKeys.day, defaultDay)
        };
    };

    this.yearMonthAsParams = (year, month) => params([_paramsKeys.year, _paramsKeys.month], [year, month])

    this.currentYearMonthAsParams = () => {
        let yearMonth = new SmartDate().asYearMonth();
        return this.yearMonthAsParams(yearMonth.year, yearMonth.month);
    };

    function params(keys, values) {
        let params = new Map();
        for (let i = 0; i < keys.length; i++) {
            params.set(keys[i], values[i]);
        }
        return params;
    };

    this.yearMonthDayAsParams = (year, month, day) => 
        params([_paramsKeys.year, _paramsKeys.month, _paramsKeys.day], [year, month, day]);
}