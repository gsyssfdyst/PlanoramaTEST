package web.planorama.demo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import web.planorama.demo.dto.UsuarioDTO;
import web.planorama.demo.exceptions.RecursoDuplicadoException;
import web.planorama.demo.service.UsuarioService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CadastroUsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // <--- ESSA LINHA RESOLVE O ERRO 403 (Desliga a segurança no teste)
class CriarUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso (Todos os campos + Foto) e redirecionar para login")
    void deveCadastrarUsuarioComSucesso() throws Exception {
        // Arrange
        MockMultipartFile foto = new MockMultipartFile(
                "fotoFile", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, "imagem_bytes".getBytes()
        );

        when(usuarioService.save(any(UsuarioDTO.class))).thenReturn(new UsuarioDTO(null, "Liz", "liz@teste.com", "123", null, null));

        // Act & Assert
        mockMvc.perform(multipart("/cadastro")
                .file(foto)
                .param("nome", "Lizandra Reis")
                .param("email", "lizandra@example.com")
                .param("senha", "senhaForte123")
                .param("descricaoUsuario", "Desenvolvedora Java"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));
        
        verify(usuarioService, times(1)).save(any(UsuarioDTO.class));
        System.out.println("\n>>> CONTROLLER - TESTE 1 SUCESSO: Requisição válida redirecionou para /login corretamente! <<<");
    }

    @Test
    @DisplayName("Deve falhar validação ao cadastrar SEM NOME (Campo obrigatório)")
    void deveFalharSemNome() throws Exception {
        MockMultipartFile foto = new MockMultipartFile("fotoFile", new byte[0]);

        mockMvc.perform(multipart("/cadastro")
                .file(foto)
                .param("nome", "") 
                .param("email", "teste@teste.com")
                .param("senha", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeHasFieldErrors("estudante", "nome"));
        
        verify(usuarioService, never()).save(any());
        System.out.println("\n>>> CONTROLLER - TESTE 2 SUCESSO: O sistema barrou cadastro sem NOME! <<<");
    }

    @Test
    @DisplayName("Deve falhar validação ao cadastrar SEM EMAIL")
    void deveFalharSemEmail() throws Exception {
        MockMultipartFile foto = new MockMultipartFile("fotoFile", new byte[0]);

        mockMvc.perform(multipart("/cadastro")
                .file(foto)
                .param("nome", "Lizandra")
                .param("email", "") 
                .param("senha", "123"))
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeHasFieldErrors("estudante", "email"));

        System.out.println("\n>>> CONTROLLER - TESTE 3 SUCESSO: O sistema barrou cadastro sem EMAIL! <<<");
    }

    @Test
    @DisplayName("Deve falhar validação ao cadastrar SEM SENHA")
    void deveFalharSemSenha() throws Exception {
        MockMultipartFile foto = new MockMultipartFile("fotoFile", new byte[0]);

        mockMvc.perform(multipart("/cadastro")
                .file(foto)
                .param("nome", "Lizandra")
                .param("email", "liz@teste.com")
                .param("senha", "")) 
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeHasFieldErrors("estudante", "senha"));

        System.out.println("\n>>> CONTROLLER - TESTE 4 SUCESSO: O sistema barrou cadastro sem SENHA! <<<");
    }

    @Test
    @DisplayName("Deve cadastrar com sucesso SEM FOTO e SEM DESCRIÇÃO (Campos opcionais)")
    void deveCadastrarSemCamposOpcionais() throws Exception {
        MockMultipartFile fotoVazia = new MockMultipartFile("fotoFile", "", "application/octet-stream", new byte[0]);

        mockMvc.perform(multipart("/cadastro")
                .file(fotoVazia)
                .param("nome", "Lizandra")
                .param("email", "liz@opcional.com")
                .param("senha", "123")
                .param("descricaoUsuario", "")) 
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(usuarioService, times(1)).save(any());
        System.out.println("\n>>> CONTROLLER - TESTE 5 SUCESSO: Cadastro realizado sem campos opcionais (Foto/Desc)! <<<");
    }

    @Test
    @DisplayName("Deve tratar erro quando Service lança RecursoDuplicadoException (Email já existe)")
    void deveTratarEmailDuplicado() throws Exception {
        MockMultipartFile foto = new MockMultipartFile("fotoFile", new byte[0]);

        doThrow(new RecursoDuplicadoException("E-mail duplicado"))
                .when(usuarioService).save(any(UsuarioDTO.class));

        mockMvc.perform(multipart("/cadastro")
                .file(foto)
                .param("nome", "Lizandra")
                .param("email", "duplicado@teste.com")
                .param("senha", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeExists("email_error"))
                .andExpect(model().attribute("email_error", "Este e-mail já está em uso. Tente outro."));
        
        System.out.println("\n>>> CONTROLLER - TESTE 6 SUCESSO: A exceção de e-mail duplicado foi capturada e exibida no HTML! <<<");
    }
}