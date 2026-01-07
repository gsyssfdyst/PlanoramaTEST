document.addEventListener('DOMContentLoaded', () => {

    const modalContainer = document.getElementById('modal-container');
    const linksAbrirModal = document.querySelectorAll('.abrir-modal');

    // Função para abrir o modal com o conteúdo de uma URL
    async function abrirModal(url) {
        if (!modalContainer) return;

        try {
            // 1. Usa a função fetch para buscar o conteúdo da URL
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Não foi possível carregar o conteúdo.');
            }
            const content = await response.text();

            // 2. Coloca o conteúdo HTML dentro do contêiner do modal
            modalContainer.innerHTML = content;
            
            // 3. Mostra o modal adicionando a classe 'mostrar'
            modalContainer.classList.add('mostrar');

        } catch (error) {
            console.error('Erro ao abrir o modal:', error);
            modalContainer.innerHTML = `<p style="color:white;">Erro ao carregar o conteúdo. Tente novamente.</p>`;
            modalContainer.classList.add('mostrar');
        }
    }

    // Função para fechar o modal
    function fecharModal() {
        if (modalContainer) {
            modalContainer.classList.remove('mostrar');
            // Limpa o conteúdo para a próxima vez que abrir
            modalContainer.innerHTML = ''; 
        }
    }

    // Adiciona o evento de clique para cada link "Alterar"
    linksAbrirModal.forEach(link => {
        link.addEventListener('click', (event) => {
            // Previne o comportamento padrão do link (que seria navegar para outra página)
            event.preventDefault(); 
            
            const url = link.getAttribute('href');
            if (url) {
                abrirModal(url);
            }
        });
    });

    // Adiciona um evento para fechar o modal
    modalContainer.addEventListener('click', (event) => {
        // Se o clique foi no "X" (fecharX) ou no fundo escuro (o próprio container)
        if (event.target.classList.contains('fecharX') || event.target.id === 'modal-container') {
            fecharModal();
        }
    });

});