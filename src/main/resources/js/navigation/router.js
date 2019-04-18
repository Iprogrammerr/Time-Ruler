export function Router(root = "http://127.0.0.1:8080/") {

    const _root = root;

    this.forward = (route) => {
        location.href = `${_root}${route}`;
    };

    this.forwardWithParams = (route, params) => {
        location.href = `${_root}${route}${paramsString(params)}`;
    };

    function paramsString(params) {
        let paramsString = params.size > 0 ? "?" : "";
        for (let [k, v] of params) {
            if (paramsString.length > 1) {
                paramsString += `&${k}=${v}`;
            } else {
                paramsString += `${k}=${v}`;
            }
        }
        return paramsString;
    };

    this.replace = (route) => {
        location.replace(`${_root}${route}`);
    };

    this.replaceWithParams = (route, params) => {
        location.replace(`${_root}${route}${paramsString(params)}`);
    };
}   