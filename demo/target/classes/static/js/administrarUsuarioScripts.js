//FILTRO PARA PEGAR O TIPO DO SELECT

document.addEventListener("DOMContentLoaded", function (){
    let filtro = document.getElementById("filtro-tipo-usuario");

    filtro.addEventListener("change", function (){
        let tipoUsuario = this.value;

        let novaUrl = "/admin/listar-usuarios?tipo=" + tipoUsuario;

        //Manda para a url criada com o tipo do usuário
        window.location.href = novaUrl;
    });
});