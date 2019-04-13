export function Router(root = "http://127.0.0.1:8080/") {

    const _root = root;

    this.forward = (route) => {
        location.href = `${root}${route}`;
    };

    this.forwardWithParams = (route, ...params) => {
        location.href = `${root}${route}${paramsString(params)}`;
    }

    function paramsString(params) {
        let paramsString = params.length > 0 ? "?" : "";
        for (let [i, p] of params.entries()) {
            if (i > 0) {
                paramsString += `&${p.key}=${p.value}`;
            } else {
                paramsString += `${p.key}=${p.value}`;
            }
        }
        return paramsString;
    }

    this.replace = (route) => {
        location.replace(`${root}${route}`);
    };

    this.replaceWithParams = (route, ...params) => {
        location.replace(`${root}${route}${paramsString(params)}`);
    };
}   