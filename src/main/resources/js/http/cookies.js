export function Cookies() {

    const source = new Map();

    function read() {
        source.clear();
        let cookies = document.cookie;
        console.log(`Raw cookies = ${cookies}`);
        let keyValues = cookies.split("; ");
        console.log(`Split cookies = ${keyValues}`);
        keyValues.forEach(kv => {
            let keyValue = kv.split("=");
            console.log(`Adding cookies = ${keyValue}`);
            source.set(keyValue[0], keyValue[1]);
        });
    }

    this.get = (key) => {
        if (source.size < 1) {
            read();
        }
        if (source.has(key)) {
            return source.get(key);
        }
        throw new Error(`There is no cookie associated with ${key} key`);
    }
    this.refresh = () => read();
}