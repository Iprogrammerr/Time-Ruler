import { router } from "./app.js";
import { routes } from "./app.js";
import { endpoints } from "./app.js";
import { httpConnections } from "./app.js";

addEventListener("submit", e => e.preventDefault());
document.getElementById("signUp").onclick = () => {
    console.log("Sign up!");
    httpConnections.post(endpoints.signUp, {})
        .then(r => {
            console.log(r);
            //TODO show modal
            router.replace(routes.signIn);
        })
        .catch(e => console.log(e));
};