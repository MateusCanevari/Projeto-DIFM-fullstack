import { refreshToken } from "./refresh.js";

const btn = document.querySelector('.btn-del');
const id = document.getElementById('id');

var dados = JSON.parse(localStorage.getItem('local'));

var serverURL;

btn.addEventListener('click', () => {

    if (dados == null || dados.email == null) {
        window.location.href = 'login.html'
    }

    serverURL = 'http://localhost:8080/api/v1/user/' + dados.email + '/product/'

    deleteProduct();

});

function deleteProduct() {
    fetch(serverURL + id.value,
        {
            headers: {
                "Authorization": "Bearer " + dados.accessToken
            },
            method: "DELETE",
        })
        .then(function (res) {
            console.log(res)
            if (res.status >= 200 && res.status <= 299) {
                window.location.href = 'index.html'
            }
            if (res.status == 403) {
                refreshToken();
            }
        })
        .catch(function (res) { console.log(res) })
}