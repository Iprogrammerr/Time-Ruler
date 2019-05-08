export function SmartDate(date = new Date()) {

    const MAX_UTC_MONTH = 11;
    const _date = date;

    this.base = () => _date;

    this.isAfter = (year, month) => {
        let currentYear = _date.getUTCFullYear();
        return (currentYear == year && getUTCMonth() > month) || currentYear > year;
    };

    function getUTCMonth() {
        return _date.getUTCMonth() + 1;
    };

    this.isBefore = (year, month) => {
        let currentYear = _date.getUTCFullYear();
        return (currentYear == year && getUTCMonth() < month) || currentYear < year;
    };

    this.setYearMonth = (year, month) => {
        _date.setUTCFullYear(year);
        _date.setUTCMonth(month - 1);
    };

    this.addMonth = (value) => {
        let newMonth = _date.getUTCMonth() + value;
        if (newMonth > MAX_UTC_MONTH) {
            newMonth = 0;
            _date.setUTCFullYear(_date.getUTCFullYear() + 1);
        }
        _date.setUTCMonth(newMonth);
    };

    this.subtractMonth = (value) => {
        let newMonth = _date.getUTCMonth() - value;
        if (newMonth < 0) {
            newMonth = MAX_UTC_MONTH;
            _date.setUTCFullYear(_date.getUTCFullYear() - 1);
        }
        _date.setUTCMonth(newMonth);
    };

    this.asYearMonth = () => {
        return {
            year: _date.getUTCFullYear(),
            month: getUTCMonth()
        };
    };

    this.asYearMonthDay = () => {
        return {
            year: _date.getUTCFullYear(),
            month: getUTCMonth(),
            day: _date.getUTCDate()
        };
    };

    this.asZonedYearMonthDay = () => {
        return {
            year: _date.getFullYear(),
            month: _date.getMonth() + 1,
            day: _date.getDate()
        };
    };

    this.isNow = (year, month, day) => {
        let yearMonthDay = this.asZonedYearMonthDay();
        return year == yearMonthDay.year && month == yearMonthDay.month && day == yearMonthDay.day;
    };

    this.setYearMonthDay = (year, month, day) => _date.setUTCFullYear(year, month - 1, day);

    this.asIsoDateString = ()  => {
        let isoDate = _date.toISOString();
        return isoDate.substring(0, isoDate.indexOf("T"));
    };
}