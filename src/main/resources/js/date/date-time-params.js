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

    this.currentYearMonthDayFromUrlDate = () => {
        let yearMonthDay = new SmartDate().asYearMonthDay();
        return this.yearMonthDayFromUrl(yearMonthDay.year, yearMonthDay.month, yearMonthDay.day);
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

    this.yearMonthDayAsDateParam = (year, month, day) => {
        let date = new SmartDate();
        date.setYearMonthDay(year, month, day);
        return params([_paramsKeys.date], [date.asIsoDateString()]);
    };

    this.yesterdayAsDateParam = () => {
        let date = new Date();
        date.setTime(date.getTime() - 24 * 3600 * 1000);
        let yesterday = new SmartDate(date).asYearMonthDay();
        return this.yearMonthDayAsDateParam(yesterday.year, yesterday.month, yesterday.day);
    };

    this.dateFromUrl = () => {
        let dateParam = _urlParams.getOrDefault(_paramsKeys.date, "");
        let date = new Date(dateParam);
        if (isNaN(date.getTime())) {
            date = new Date();
        }
        return new SmartDate(date);
    };

    this.dateFromUrlAsParam = () => params([_paramsKeys.date], [this.dateFromUrl().asIsoDateString()]);

    this.dateAsParam = (date) => params([_paramsKeys.date], [date]);
}