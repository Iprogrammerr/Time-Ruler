export function HttpConnections() {

    const methods = {
        get: "GET",
        post: "POST",
        put: "PUT",
        delete: "DELETE"
    };
    const minOkStatus = 200;
    const maxOkStatus = 299;

    //TODO session, cookies?
    function execute(url, method, data) {
        return new Promise((resolve, reject) => {
            let request = new XMLHttpRequest();
            request.onload = function () {
                if (this.status >= minOkStatus && this.status <= maxOkStatus) {
                    resolve(this.response);
                } else {
                    reject(this.response);
                }
            }
            request.onerror = function () {
                reject(new Error(this.response));
            };
            request.open(method, url, true);
            if (data) {
                request.send(data);
            } else {
                request.send();
            }
        });
    }

    this.get = (url) => execute(url, methods.get);
    this.post = (url, data) => execute(url, methods.post, data);
    this.put = (url, data) => execute(url, methods.put, data);
    this.delete = (url) => execute(url, methods.delete);
}