package web.planorama.demo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import web.planorama.demo.config.CustomAuthenticationSuccessHandler;
import web.planorama.demo.dto.RegistrarEstudoDTO;
import web.planorama.demo.mapping.*;
import web.planorama.demo.repository.*;
import web.planorama.demo.service.*;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrarEstudoController.class)
@AutoConfigureMockMvc(addFilters = false) // Desativa filtros de segurança para focar na validação
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ordena os testes (Teste 1, 2, 3...)
class RegistrarEstudoControllerTest {

    @Autowired private MockMvc mockMvc;

    // --- Mocks dos Serviços usados pelo Controller ---
    @MockBean private RegistrarEstudoService registrarEstudoService;
    @MockBean private MateriaService materiaService;
    @MockBean private PlanejamentoService planejamentoService;
    @MockBean private MateriaPlanejamentoService materiaPlanejamentoService;
    @MockBean private AssuntoRepository assuntoRepository;
    @MockBean private MateriaPlanejamentoRepository materiaPlanejamentoRepository;
    @MockBean private MateriaMapper materiaMapper;
    @MockBean private AssuntoMapper assuntoMapper;
    @MockBean private MateriaPlanejamentoMapper materiaPlanejamentoMapper;

    // --- Mocks EXTRAS (Essenciais para evitar erro de ApplicationContext) ---
    // Estes mocks satisfazem o GlobalControllerAdvice e o SecurityConfig
    @MockBean private UsuarioService usuarioService;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private PapelRepository papelRepository;
    @MockBean private CustomAuthenticationSuccessHandler successHandler;

    private final String URL_POST = "/registrar-estudo/registrar";

    // =================================================================================
    // TESTES DE LIMITAÇÕES DE PARTIÇÕES
    // =================================================================================

    @Test
    @Order(1)
    @DisplayName("CT004 - Teste 1: 25h horas estudadas (Inválido)")
    void deveFalharCom25Horas() throws Exception {
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                .param("planejamentoId", UUID.randomUUID().toString())
                .param("idMateriaPlanejamento", UUID.randomUUID().toString())
                .param("horasEstudadas", "25") // LIMITE EXCEDIDO (MAX 24)
                .param("minutosEstudados", "0"))
                .andExpect(flash().attributeExists("error")); // Espera erro

        System.out.println("\n>>> TESTE 1 SUCESSO: Mensagem no terminal informando que não é possível cadastrar 25h horas diárias. <<<\n");
    }

    @Test
    @Order(2)
    @DisplayName("CT004 - Teste 2: 0h horas estudadas (com 0min)")
    void deveFalharCom0Horas() throws Exception {
        // Testa 0h 0m -> Deve falhar pela validação isTempoValido
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                .param("horasEstudadas", "0")
                .param("minutosEstudados", "0"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 2 SUCESSO: Mensagem no terminal informando que é necessário cadastrar pelo menos 1h (ou 1min) de estudo. <<<\n");
    }

    @Test
    @Order(3)
    @DisplayName("CT004 - Teste 3: 0min estudados sem colocar as horas")
    void deveFalharCom0MinSemHoras() throws Exception {
        // Testa horas=0 e minutos=0
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                .param("horasEstudadas", "0")
                .param("minutosEstudados", "0"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 3 SUCESSO: Mensagem no terminal informando que deverá ser cadastrado pelo menos 1min de estudo. <<<\n");
    }

    // =================================================================================
    // ENTRADAS INVÁLIDAS, VÁLIDAS E COMBINAÇÕES
    // =================================================================================

    @Test
    @Order(4)
    @DisplayName("CT004 - Teste 4: Não passar id de assunto")
    void deveFalharSemAssunto() throws Exception {
        mockMvc.perform(post(URL_POST)
                // Sem param assuntoId
                .param("horasEstudadas", "1")
                .param("minutosEstudados", "0"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 4 SUCESSO: Mensagem no terminal informando que deverá selecionar pelo menos um assunto. <<<\n");
    }

    @Test
    @Order(5)
    @DisplayName("CT004 - Teste 5: Não passar horasEstudadas com minutos preenchidos")
    void deveFalharSemHorasComMinutos() throws Exception {
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                // Sem horasEstudadas (null) -> @NotNull deve barrar
                .param("minutosEstudados", "30"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 5 SUCESSO: Mensagem no terminal informando que precisa cadastrar pelo menos 1min de estudo (Horas obrigatórias). <<<\n");
    }

    @Test
    @Order(6)
    @DisplayName("CT004 - Teste 6: Passar horasEstudas sem minutos")
    void deveSucessoHorasSemMinutos() throws Exception {
        UUID planoId = UUID.randomUUID();
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                .param("planejamentoId", planoId.toString())
                .param("idMateriaPlanejamento", UUID.randomUUID().toString())
                .param("horasEstudadas", "2")
                .param("minutosEstudados", "0")) // 2h 00m -> VÁLIDO
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));

        System.out.println("\n>>> TESTE 6 SUCESSO: Mensagem no terminal dizendo que o estudo foi Registrado (Apenas Horas). <<<\n");
    }

    @Test
    @Order(7)
    @DisplayName("CT004 - Teste 7: HorasEstudas e minutos preenchidos")
    void deveSucessoHorasEMinutos() throws Exception {
        UUID planoId = UUID.randomUUID();
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                .param("planejamentoId", planoId.toString())
                .param("idMateriaPlanejamento", UUID.randomUUID().toString())
                .param("horasEstudadas", "1")
                .param("minutosEstudados", "30")) // 1h 30m -> VÁLIDO
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));

        System.out.println("\n>>> TESTE 7 SUCESSO: Mensagem no terminal dizendo que o estudo foi Registrado. <<<\n");
    }

    @Test
    @Order(8)
    @DisplayName("CT004 - Teste 8: HorasEstudadas e minutos sem estarem preenchidos")
    void deveFalharSemNada() throws Exception {
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                // Sem horas e sem minutos
                )
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 8 SUCESSO: Mensagem no terminal dizendo que é necessário, pelo menos, 1min de estudo. <<<\n");
    }
}