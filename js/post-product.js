import { refreshToken } from "./refresh.js";

const btn = document.querySelector('.btn-add');
const nomeProduto = document.getElementById('produto');
const descricao = document.getElementById('descricao');
const quantidade = document.getElementById('quantidade');
const preco = document.getElementById('preco');

var dados = JSON.parse(localStorage.getItem('local'));

var serverURL;

btn.addEventListener('click', () => {

  if (dados == null || dados.email == null) {
    window.location.href = 'login.html'
  }

  serverURL = 'http://localhost:8080/api/v1/user/' + dados.email + '/product'

  postProduct();

});

function postProduct() {
  fetch(serverURL,
    {
      headers: {
        "Accept": "*/*",
        "Content-Type": "application/json",
        "Authorization": "Bearer " + dados.accessToken
      },
      method: "POST",
      body: JSON.stringify({
        nomeProduto: nomeProduto.value,
        descricao: descricao.value,
        quantidade: quantidade.value,
        preco: preco.value
      })
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