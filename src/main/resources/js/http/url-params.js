export function UrlParams() {

    const params = new Map();

    this.get = (key) => {
        if (this.has(key)) {
            return params.get(key);
        }
        throw new Error(`There is no value associated with ${key} key`);
    }

    this.has = (key) => {
        readIfEmpty();
        return params.has(key);
    };

    function readIfEmpty() {
        if (params.size == 0) {
            read();
        }
    };

    function read() {
        let query = location.search;
        if (query.length > 0) {
            let queryParams = query.substring(1).split("&");
            for (let qp of queryParams) {
                let keyValue = qp.split("=");
                if (keyValue.length > 0) {
                    params.set(keyValue[0], keyValue[1]);
                }
            }
        }
    };

    this.getOrDefault = (key, defaultValue) => {
        let value;
        if (this.has(key)) {
            value = this.get(key);
        } else {
            value = defaultValue;
        }
        return value;
    };
}