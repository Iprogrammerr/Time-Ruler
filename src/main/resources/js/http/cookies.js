export function Cookies() {

    const source = new Map();

    function read() {
        source.clear();
        let cookies = document.cookie;
        let keyValues = cookies.split("; ");
        keyValues.forEach(kv => {
            let keyValue = kv.split("=");
            source.set(keyValue[0], keyValue[1]);
        });
    };

    this.get = (key) => {
        if (source.size < 1) {
            read();
        }
        if (source.has(key)) {
            return source.get(key);
        }
        throw new Error(`There is no cookie associated with ${key} key`);
    };

    this.has = (key) => {
        if (source.size < 1) {
            read();
        }
        return source.has(key);
    };

    this.put = (key, value) => {
        document.cookie = `${key} = ${value};`;
        read();
    };

    this.refresh = () => read();
}