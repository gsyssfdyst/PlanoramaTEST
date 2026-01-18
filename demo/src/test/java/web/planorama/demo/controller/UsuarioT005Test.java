package web.planorama.demo.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import web.planorama.demo.config.CustomAuthenticationSuccessHandler;
import web.planorama.demo.service.UsuarioService;
import web.planorama.demo.repository.PapelRepository;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(AlterarDadosEstudanteController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioT005Test {

    @Autowired private MockMvc mockMvc;

    @MockBean private UsuarioService usuarioService;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private PapelRepository papelRepository;
    @MockBean private CustomAuthenticationSuccessHandler successHandler;

    // ==================== TESTES DE EMAIL ====================

    @Test
    @Order(1)
    @DisplayName("T005-1: Editar e-mail com campo novo e-mail vazio (senha atual correta)")
    void editarEmailNovoVazio() throws Exception {
        // senha atual preenchida, novoEmail vazio
        mockMvc.perform(post("/minha-conta/alterar-email")
                .param("novoEmail", "")
                .param("senhaAtual", "senha123"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-1 SUCESSO: Campo do novo e-mail vazio foi rejeitado. <<<\n");
    }

    @Test
    @Order(2)
    @DisplayName("T005-2: Editar e-mail com e-mail já existente (senha atual correta)")
    void editarEmailDuplicado() throws Exception {
        doThrow(new RuntimeException("E-mail duplicado")).when(usuarioService).alterarEmail(eq("existente@teste.com"), anyString());

        mockMvc.perform(post("/minha-conta/alterar-email")
                .param("novoEmail", "existente@teste.com")
                .param("senhaAtual", "senhaAtualCorreta"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-2 SUCESSO: Tentativa de usar e-mail duplicado foi rejeitada. <<<\n");
    }

    @Test
    @Order(3)
    @DisplayName("T005-3: Editar e-mail com senha atual vazia")
    void editarEmailSenhaVazia() throws Exception {
        mockMvc.perform(post("/minha-conta/alterar-email")
                .param("novoEmail", "novo@teste.com")
                .param("senhaAtual", ""))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-3 SUCESSO: Campo de senha atual vazio foi rejeitado. <<<\n");
    }

    @Test
    @Order(4)
    @DisplayName("T005-4: Editar e-mail com senha atual incorreta")
    void editarEmailSenhaIncorreta() throws Exception {
        doThrow(new RuntimeException("A senha atual está incorreta")).when(usuarioService).alterarEmail(eq("novo@teste.com"), eq("senhaErrada"));

        mockMvc.perform(post("/minha-conta/alterar-email")
                .param("novoEmail", "novo@teste.com")
                .param("senhaAtual", "senhaErrada"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-4 SUCESSO: Senha atual incorreta foi rejeitada na alteração de e-mail. <<<\n");
    }

    @Test
    @Order(5)
    @DisplayName("T005-5: Editar e-mail com campos corretos")
    void editarEmailSucesso() throws Exception {
        // Simula que o usuário está autenticado para que o controller faça logout e redirecione para /login
        Authentication auth = mock(Authentication.class);
        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        doNothing().when(usuarioService).alterarEmail(eq("novo@teste.com"), eq("senhaCorreta"));

        mockMvc.perform(post("/minha-conta/alterar-email")
                .param("novoEmail", "novo@teste.com")
                .param("senhaAtual", "senhaCorreta"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(redirectedUrl("/login"));

        System.out.println("\n>>> T005-5 SUCESSO: E-mail alterado com sucesso e usuário foi deslogado. <<<\n");
    }

    // ==================== TESTES DE SENHA ====================

    @Test
    @Order(6)
    @DisplayName("T005-6: Editar senha com campos vazios")
    void editarSenhaCamposVazios() throws Exception {
        mockMvc.perform(post("/minha-conta/alterar-senha")
                .param("senhaAtual", "")
                .param("novaSenha", ""))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-6 SUCESSO: Campos de senha vazios foram rejeitados. <<<\n");
    }

    @Test
    @Order(7)
    @DisplayName("T005-7: Editar senha com nova senha igual à atual")
    void editarSenhaIgual() throws Exception {
        doThrow(new RuntimeException("As senhas não podem ser iguais")).when(usuarioService).alterarSenha(eq("senhaAtual"), eq("senhaAtual"));

        mockMvc.perform(post("/minha-conta/alterar-senha")
                .param("senhaAtual", "senhaAtual")
                .param("novaSenha", "senhaAtual"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-7 SUCESSO: Alteração rejeitada quando nova senha é igual à atual. <<<\n");
    }

    @Test
    @Order(8)
    @DisplayName("T005-8: Editar senha com senha atual incorreta")
    void editarSenhaAtualIncorreta() throws Exception {
        doThrow(new RuntimeException("A senha atual está incorreta")).when(usuarioService).alterarSenha(eq("senhaErrada"), anyString());

        mockMvc.perform(post("/minha-conta/alterar-senha")
                .param("senhaAtual", "senhaErrada")
                .param("novaSenha", "novaSenha123"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-8 SUCESSO: Senha atual incorreta foi rejeitada na alteração de senha. <<<\n");
    }

    @Test
    @Order(9)
    @DisplayName("T005-9: Editar senha com campos corretos")
    void editarSenhaSucesso() throws Exception {
        doNothing().when(usuarioService).alterarSenha(eq("senhaCorreta"), eq("novaSenha123"));

        mockMvc.perform(post("/minha-conta/alterar-senha")
                .param("senhaAtual", "senhaCorreta")
                .param("novaSenha", "novaSenha123"))
                .andExpect(flash().attributeExists("success"));

        System.out.println("\n>>> T005-9 SUCESSO: Senha alterada com sucesso. <<<\n");
    }

    // ==================== TESTES DE NOME DE USUÁRIO ====================

    @Test
    @Order(10)
    @DisplayName("T005-10: Editar nome com campos vazios")
    void editarNomeCamposVazios() throws Exception {
        mockMvc.perform(post("/minha-conta/alterar-nome-usuario")
                .param("novoNome", "")
                .param("senhaAtual", ""))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-10 SUCESSO: Campos de nome ou senha vazios foram rejeitados. <<<\n");
    }

    @Test
    @Order(11)
    @DisplayName("T005-11: Editar nome com senha atual vazia")
    void editarNomeSenhaVazia() throws Exception {
        mockMvc.perform(post("/minha-conta/alterar-nome-usuario")
                .param("novoNome", "Novo Nome")
                .param("senhaAtual", ""))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-11 SUCESSO: Campo senha atual vazio foi rejeitado na alteração de nome. <<<\n");
    }

    @Test
    @Order(12)
    @DisplayName("T005-12: Editar nome com senha atual incorreta")
    void editarNomeSenhaIncorreta() throws Exception {
        doThrow(new RuntimeException("A senha atual está incorreta")).when(usuarioService).alterarNomeUsuario(eq("Novo Nome"), eq("senhaErrada"));

        mockMvc.perform(post("/minha-conta/alterar-nome-usuario")
                .param("novoNome", "Novo Nome")
                .param("senhaAtual", "senhaErrada"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-12 SUCESSO: Senha atual incorreta foi rejeitada na alteração de nome. <<<\n");
    }

    @Test
    @Order(13)
    @DisplayName("T005-13: Editar nome com campos corretos")
    void editarNomeSucesso() throws Exception {
        doNothing().when(usuarioService).alterarNomeUsuario(eq("Novo Nome"), eq("senhaCorreta"));

        mockMvc.perform(post("/minha-conta/alterar-nome-usuario")
                .param("novoNome", "Novo Nome")
                .param("senhaAtual", "senhaCorreta"))
                .andExpect(flash().attributeExists("success"));

        System.out.println("\n>>> T005-13 SUCESSO: Nome de usuário alterado com sucesso. <<<\n");
    }

    // ==================== TESTES DE FOTO ====================

    @Test
    @Order(14)
    @DisplayName("T005-14: Editar foto com arquivo válido e usuário autenticado")
    void editarFotoSucesso() throws Exception {
        // Simula usuário autenticado para capturar email
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        MockMultipartFile file = new MockMultipartFile("novaFoto", "foto.png", "image/png", "conteudo".getBytes());

        doNothing().when(usuarioService).alterarFotoUsuario(eq("usuario@teste.com"), anyString());

        mockMvc.perform(multipart("/minha-conta/alterar-foto-perfil").file(file))
                .andExpect(flash().attributeExists("success"));

        System.out.println("\n>>> T005-14 SUCESSO: Foto alterada com sucesso. <<<\n");
    }

    @Test
    @Order(15)
    @DisplayName("T005-15: Editar foto sem enviar arquivo")
    void editarFotoArquivoVazio() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("novaFoto", "", "", new byte[0]);

        mockMvc.perform(multipart("/minha-conta/alterar-foto-perfil").file(emptyFile))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-15 SUCESSO: Arquivo vazio foi rejeitado. <<<\n");
    }

    @Test
    @Order(16)
    @DisplayName("T005-16: Editar foto com usuário autenticado mas serviço indica senha incorreta")
    void editarFotoSenhaIncorreta() throws Exception {
        // Embora o controller não receba senha, permitimos que o service lance exceção para simular validação adicional
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        MockMultipartFile file = new MockMultipartFile("novaFoto", "foto.png", "image/png", "conteudo".getBytes());

        doThrow(new RuntimeException("A senha está incorreta")).when(usuarioService).alterarFotoUsuario(eq("usuario@teste.com"), anyString());

        mockMvc.perform(multipart("/minha-conta/alterar-foto-perfil").file(file))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-16 SUCESSO: Serviço rejeitou alteração de foto por senha incorreta (simulado). <<<\n");
    }

    // ==================== TESTES DE DESCRIÇÃO ====================

    @Test
    @Order(17)
    @DisplayName("T005-17: Editar descrição com campo vazio e senha correta")
    void editarDescricaoCampoVazio() throws Exception {
        mockMvc.perform(post("/minha-conta/alterar-descricao")
                .param("novaDescricao", "")
                .param("senhaAtual", "senhaCorreta"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-17 SUCESSO: Campo de descrição vazio foi rejeitado. <<<\n");
    }

    @Test
    @Order(18)
    @DisplayName("T005-18: Editar descrição com senha incorreta")
    void editarDescricaoSenhaIncorreta() throws Exception {
        doThrow(new RuntimeException("A senha atual está incorreta")).when(usuarioService).alterarDescricao(eq("Nova descrição"), eq("senhaErrada"));

        mockMvc.perform(post("/minha-conta/alterar-descricao")
                .param("novaDescricao", "Nova descrição")
                .param("senhaAtual", "senhaErrada"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-18 SUCESSO: Senha incorreta foi rejeitada na alteração de descrição. <<<\n");
    }

    @Test
    @Order(19)
    @DisplayName("T005-19: Editar descrição com campos vazios")
    void editarDescricaoCamposVazios() throws Exception {
        mockMvc.perform(post("/minha-conta/alterar-descricao")
                .param("novaDescricao", "")
                .param("senhaAtual", ""))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> T005-19 SUCESSO: Campos de descrição/senha vazios foram rejeitados. <<<\n");
    }

    @Test
    @Order(20)
    @DisplayName("T005-20: Editar descrição com campos corretos")
    void editarDescricaoSucesso() throws Exception {
        doNothing().when(usuarioService).alterarDescricao(eq("Nova descrição"), eq("senhaCorreta"));

        mockMvc.perform(post("/minha-conta/alterar-descricao")
                .param("novaDescricao", "Nova descrição")
                .param("senhaAtual", "senhaCorreta"))
                .andExpect(flash().attributeExists("success"));

        System.out.println("\n>>> T005-20 SUCESSO: Descrição alterada com sucesso. <<<\n");
    }

}
