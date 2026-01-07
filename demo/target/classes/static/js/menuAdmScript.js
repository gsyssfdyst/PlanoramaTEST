//SCRIPT PARA APARECER O MENU QUANDO CLICAR
// NO MENU HAMBURGUER----------------------

document.addEventListener("DOMContentLoaded", function () {
    let botaoAbrirMenu = document.getElementById("divMenuHamburguer");
    let botaoFecharMenu = document.getElementById("fecharMenu");
    let containerMenu = document.getElementById("sectionAparecerMenu");
    let conteudoPrincipal = document.querySelectorAll("conteudoPrincipal");

    //COMO ESTÁ EM UM HTML DIFERENTE, UTILIZAMOS O fetch PARA CARREGAR
    //A TELA DE MENU
    fetch("../templates/menuAdm.html").then(response =>{
        if(!response.ok){
            throw new Error("Erro ao carregar o menu");
        }
        return response.text();
    }).then(menuHtml => {
        containerMenu.innerHTML = menuHtml;

        let menu = document.querySelector(".sectionMenu");
        let botaoFecharMenu = document.getElementById("fecharMenu");

        let abrirMenu = () => {
            menu.classList.add("menuAtivo");
            conteudoPrincipal.forEach(conteudo => {
                conteudo.classList.add("conteudoBlur");
            });
        }

        let fechandoMenu = () => {
            menu.classList.remove("menuAtivo");
            conteudoPrincipal.forEach(conteudo => {
                conteudo.classList.remove("conteudoBlur");
            });
        }

        if(botaoAbrirMenu){
            botaoAbrirMenu.addEventListener("click", abrirMenu);
        }
        if(botaoFecharMenu){
            botaoFecharMenu.addEventListener("click", fechandoMenu);
        }
    }).catch(error => {
        console.error("Falha na operação de fetch", error);
    });

});