export function Router(root = "http://127.0.0.1:8080/") {

    const _root = root;
    
    this.forward = (route) => {
        location.href = `${root}${route}`;
    };

    this.replace = (route) => {
        location.replace(`${root}${route}`);
    };
}