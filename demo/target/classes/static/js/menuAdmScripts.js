//SCRIPT PARA APARECER O MENU ADM QUANDO CLICAR
// NO MENU HAMBURGUER----------------------

document.addEventListener("DOMContentLoaded", function () {
    let botaoAbrirMenu = document.getElementById("divMenuHamburguer");
    let botaoFecharMenu = document.getElementById("fecharMenu");
    let containerMenu = document.querySelector(".sectionMenu");
    let conteudoPrincipal = document.querySelectorAll(".conteudoPrincipal");


    const abrirMenu = () => {
        if (containerMenu) {
            containerMenu.classList.add("menuAtivo");
        }
        conteudoPrincipal.forEach(conteudo => {
            conteudo.classList.add("conteudoBlur");
        });
    };

    const fechandoMenu = () => {
        if (containerMenu) {
            containerMenu.classList.remove("menuAtivo");
        }
        conteudoPrincipal.forEach(conteudo => {
            conteudo.classList.remove("conteudoBlur");
        });
    };

    // Adiciona os eventos de clique
    if (botaoAbrirMenu) {
        botaoAbrirMenu.addEventListener("click", abrirMenu);
    }
    if (botaoFecharMenu) {
        botaoFecharMenu.addEventListener("click", fechandoMenu);
    }

});