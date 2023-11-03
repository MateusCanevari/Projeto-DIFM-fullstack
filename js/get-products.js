import { refreshToken } from "./refresh.js";

var dados = JSON.parse(localStorage.getItem('local'));

const tbody = document.getElementById('produtos');

var serverURL;

getProducts();

function getProducts() {
    if (dados == null || dados.email == null) {
        window.location.href = 'login.html'
    }

    serverURL = 'http://localhost:8080/api/v1/user/' + dados.email + '/product'

    fetch(serverURL,
        {
            headers: {
                "Accept": "*/*",
                "Content-Type": "application/json",
                "Authorization": "Bearer " + dados.accessToken
            },
            method: "GET"
        })
        .then(function (res) {
            console.log(res)
            res.json().then((produtos) => {

                produtos.map(p => {
                    delete p.links
                    return p
                }).forEach(product => {
                    const tr = document.createElement('tr');

                    Object.keys(product).forEach(key => {
                        const td = document.createElement('td')
                        td.appendChild(document.createTextNode(product[key]))
                        tr.appendChild(td)
                    })

                    tbody.appendChild(tr)
                })
            })
            if (res.status == 403) {
                refreshToken();
            }

        })
        .catch(function (res) { console.log(res) })
}
