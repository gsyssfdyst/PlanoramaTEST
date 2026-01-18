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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

@WebMvcTest(RegistrarEstudoController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegistrarEstudoCT004Test {

    @Autowired private MockMvc mockMvc;

    @MockBean private RegistrarEstudoService registrarEstudoService;
    @MockBean private MateriaService materiaService;
    @MockBean private PlanejamentoService planejamentoService;
    @MockBean private MateriaPlanejamentoService materiaPlanejamentoService;
    @MockBean private AssuntoRepository assuntoRepository;
    @MockBean private MateriaPlanejamentoRepository materiaPlanejamentoRepository;
    @MockBean private MateriaMapper materiaMapper;
    @MockBean private AssuntoMapper assuntoMapper;
    @MockBean private MateriaPlanejamentoMapper materiaPlanejamentoMapper;

    // Mocks extras para evitar erro de ApplicationContext
    @MockBean private UsuarioService usuarioService;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private PapelRepository papelRepository;
    @MockBean private CustomAuthenticationSuccessHandler successHandler;

    private final String URL_POST = "/registrar-estudo/registrar";

    // Helper: when service.save is called for valid cases return the same DTO
    private void mockServiceReturn(RegistrarEstudoDTO dto) {
        when(registrarEstudoService.save(any())).thenReturn(dto);
    }

    @Test
    @Order(1)
    @DisplayName("CT004 - Teste 1: 25h horas estudadas (Inválido)")
    void deveFalharCom25Horas() throws Exception {
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                .param("planejamentoId", UUID.randomUUID().toString())
                .param("idMateriaPlanejamento", UUID.randomUUID().toString())
                .param("horasEstudadas", "25")
                .param("minutosEstudados", "0"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 1 SUCESSO: Mensagem no terminal informando que não é possível cadastrar 25h horas diárias. <<<\n");
    }

    @Test
    @Order(2)
    @DisplayName("CT004 - Teste 2: 0h horas estudadas (com 0min)")
    void deveFalharCom0Horas() throws Exception {
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
        mockMvc.perform(post(URL_POST)
                .param("assuntoId", UUID.randomUUID().toString())
                .param("horasEstudadas", "0")
                .param("minutosEstudados", "0"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 3 SUCESSO: Mensagem no terminal informando que deverá ser cadastrado pelo menos 1min de estudo. <<<\n");
    }

    @Test
    @Order(4)
    @DisplayName("CT004 - Teste 4: Não passar id de assunto")
    void deveFalharSemAssunto() throws Exception {
        mockMvc.perform(post(URL_POST)
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
                .param("minutosEstudados", "30"))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 5 SUCESSO: Mensagem no terminal informando que precisa cadastrar pelo menos 1min de estudo. <<<\n");
    }

    @Test
    @Order(6)
    @DisplayName("CT004 - Teste 6: Passar horasEstudas sem minutos")
    void deveSucessoHorasSemMinutos() throws Exception {
        // Service mocked to return a DTO to simulate success
        RegistrarEstudoDTO retorno = new RegistrarEstudoDTO();
        retorno.setAssuntoId(UUID.randomUUID());
        retorno.setHorasEstudadas(2);
        retorno.setMinutosEstudados(0);
        retorno.setIdMateriaPlanejamento(UUID.randomUUID());
        retorno.setPlanejamentoId(UUID.randomUUID());
        mockServiceReturn(retorno);

        mockMvc.perform(post(URL_POST)
                .param("assuntoId", retorno.getAssuntoId().toString())
                .param("planejamentoId", retorno.getPlanejamentoId().toString())
                .param("idMateriaPlanejamento", retorno.getIdMateriaPlanejamento().toString())
                .param("horasEstudadas", "2")
                .param("minutosEstudados", "0"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().is3xxRedirection());

        System.out.println("\n>>> TESTE 6 SUCESSO: Mensagem no terminal dizendo que o estudo foi Registrado (Apenas Horas). <<<\n");
    }

    @Test
    @Order(7)
    @DisplayName("CT004 - Teste 7: HorasEstudas e minutos preenchidos")
    void deveSucessoHorasEMinutos() throws Exception {
        RegistrarEstudoDTO retorno = new RegistrarEstudoDTO();
        retorno.setAssuntoId(UUID.randomUUID());
        retorno.setHorasEstudadas(1);
        retorno.setMinutosEstudados(30);
        retorno.setIdMateriaPlanejamento(UUID.randomUUID());
        retorno.setPlanejamentoId(UUID.randomUUID());
        mockServiceReturn(retorno);

        mockMvc.perform(post(URL_POST)
                .param("assuntoId", retorno.getAssuntoId().toString())
                .param("planejamentoId", retorno.getPlanejamentoId().toString())
                .param("idMateriaPlanejamento", retorno.getIdMateriaPlanejamento().toString())
                .param("horasEstudadas", "1")
                .param("minutosEstudados", "30"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().is3xxRedirection());

        System.out.println("\n>>> TESTE 7 SUCESSO: Mensagem no terminal dizendo que o estudo foi Registrado. <<<\n");
    }

    @Test
    @Order(8)
    @DisplayName("CT004 - Teste 8: HorasEstudadas e minutos sem estarem preenchidos")
    void deveFalharSemNada() throws Exception {
        mockMvc.perform(post(URL_POST))
                .andExpect(flash().attributeExists("error"));

        System.out.println("\n>>> TESTE 8 SUCESSO: Mensagem no terminal dizendo que é necessário, pelo menos, 1min de estudo. <<<\n");
    }
}
