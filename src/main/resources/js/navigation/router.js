export function Router(root = "http://127.0.0.1:8080/") {

    const _root = root;

    this.forward = (route) => {
        location.href = this.fullRoute(route);
    };

    this.fullRoute = (route) => `${root}${route}`;

    this.forwardWithParams = (route, params) => {
        location.href = `${this.fullRoute(route)}${paramsString(params)}`;
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

    this.routeWithParams = (route, params) => `${this.fullRoute(route)}${paramsString(params)}`;

    this.replace = (route) => {
        location.replace(this.fullRoute(route));
    };

    this.replaceWithParams = (route, params) => {
        location.replace(`${this.fullRoute(route)}${paramsString(params)}`);
    };
}   