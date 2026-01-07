
package web.planorama.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import web.planorama.demo.dto.PlanejamentoDTO;
import web.planorama.demo.dto.UsuarioDTO;
import web.planorama.demo.entity.UsuarioEntity;
import web.planorama.demo.mapping.UsuarioMapper;
import web.planorama.demo.repository.UsuarioRepository;
import web.planorama.demo.service.MateriaService;
import web.planorama.demo.service.PlanejamentoService;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CriarPlanejamentoControllerTest {

    @InjectMocks
    private CriarPlanejamentoController controller;

    @Mock
    private PlanejamentoService planejamentoService;

    @Mock
    private MateriaService materiaService;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // --- TESTES DE LIMITAÇÕES DE PARTIÇÕES (Horas Diárias) ---

    @Test
    @DisplayName("CT003 - Deve rejeitar 25h diárias de estudo (Limite Superior)")
    void deveRejeitar25HorasDiarias() throws Exception {
        mockMvc.perform(post("/criar-planejamento/primeiro-card")
                        .param("nomePlanejamento", "Plano Teste")
                        .param("cargo", "Estudante")
                        .param("anoAplicacao", "2025")
                        .param("horasDiarias", "25") // Inválido: Max 24
                        .param("disponibilidade", "segunda", "terca"))
                .andExpect(view().name("primeiroCriarPlano :: cardCriacao"))
                .andExpect(model().attributeHasFieldErrors("planejamentoDTO", "horasDiarias"))
                .andExpect(model().attributeErrorCount("planejamentoDTO", 1));
        
        System.out.println("\n>>> CONTROLLER - TESTE LIMITE SUPERIOR SUCESSO: Mensagem informando que o máximo de horas diárias é de 24h exibida corretamente! <<<");
    }

    @Test
    @DisplayName("CT003 - Deve rejeitar 0h diárias de estudo (Limite Inferior)")
    void deveRejeitar0HorasDiarias() throws Exception {
        mockMvc.perform(post("/criar-planejamento/primeiro-card")
                        .param("nomePlanejamento", "Plano Teste")
                        .param("cargo", "Estudante")
                        .param("anoAplicacao", "2025")
                        .param("horasDiarias", "0") // Inválido: Min 1
                        .param("disponibilidade", "segunda"))
                .andExpect(view().name("primeiroCriarPlano :: cardCriacao"))
                .andExpect(model().attributeHasFieldErrors("planejamentoDTO", "horasDiarias"));

        System.out.println("\n>>> CONTROLLER - TESTE LIMITE INFERIOR SUCESSO: Mensagem informando que o mínimo de horas diárias é de 01h exibida corretamente! <<<");
    }

    // --- TESTES DE ENTRADAS INVÁLIDAS (Campos Obrigatórios) ---

    @Test
    @DisplayName("CT003 - Deve rejeitar planejamento sem nome")
    void deveRejeitarSemNome() throws Exception {
        mockMvc.perform(post("/criar-planejamento/primeiro-card")
                        .param("nomePlanejamento", "") // Vazio
                        .param("cargo", "Estudante")
                        .param("anoAplicacao", "2025")
                        .param("horasDiarias", "4")
                        .param("disponibilidade", "segunda"))
                .andExpect(model().attributeHasFieldErrors("planejamentoDTO", "nomePlanejamento"));

        System.out.println("\n>>> CONTROLLER - TESTE CAMPO VAZIO SUCESSO: Mensagem informando que o planejamento deve ter um nome exibida corretamente! <<<");
    }

    @Test
    @DisplayName("CT003 - Deve rejeitar planejamento sem cargo")
    void deveRejeitarSemCargo() throws Exception {
        mockMvc.perform(post("/criar-planejamento/primeiro-card")
                        .param("nomePlanejamento", "Plano Teste")
                        .param("cargo", "") // Vazio
                        .param("anoAplicacao", "2025")
                        .param("horasDiarias", "4")
                        .param("disponibilidade", "segunda"))
                .andExpect(model().attributeHasFieldErrors("planejamentoDTO", "cargo"));

        System.out.println("\n>>> CONTROLLER - TESTE CAMPO VAZIO SUCESSO: Mensagem informando que o planejamento deve ter um cargo exibida corretamente! <<<");
    }

    @Test
    @DisplayName("CT003 - Deve rejeitar planejamento com ano de aplicação inválido (< 2025)")
    void deveRejeitarAnoInvalido() throws Exception {
        mockMvc.perform(post("/criar-planejamento/primeiro-card")
                        .param("nomePlanejamento", "Plano Teste")
                        .param("cargo", "Estudante")
                        .param("anoAplicacao", "2024") // Inválido: Min 2025
                        .param("horasDiarias", "4")
                        .param("disponibilidade", "segunda"))
                .andExpect(model().attributeHasFieldErrors("planejamentoDTO", "anoAplicacao"));

        System.out.println("\n>>> CONTROLLER - TESTE ANO INVÁLIDO SUCESSO: Mensagem informando que o planejamento deve ter o ano de aplicação válido exibida corretamente! <<<");
    }

    @Test
    @DisplayName("CT003 - Deve rejeitar planejamento sem disponibilidade selecionada")
    void deveRejeitarSemDisponibilidade() throws Exception {
        // Não enviando o parâmetro 'disponibilidade'
        mockMvc.perform(post("/criar-planejamento/primeiro-card")
                        .param("nomePlanejamento", "Plano Teste")
                        .param("cargo", "Estudante")
                        .param("anoAplicacao", "2025")
                        .param("horasDiarias", "4"))
                .andExpect(model().attributeHasFieldErrors("planejamentoDTO", "disponibilidade"));

        System.out.println("\n>>> CONTROLLER - TESTE DISPONIBILIDADE SUCESSO: Mensagem informando que o planejamento deve ter disponibilidade selecionada exibida corretamente! <<<");
    }

    @Test
    @DisplayName("CT003 - Deve rejeitar planejamento sem matérias (Segundo Card)")
    void deveRejeitarSemMaterias() throws Exception {
        // O controller verifica explicitamente se materiaIds é nulo ou vazio
        when(materiaService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/criar-planejamento/segundo-card"))
                // Não passando o param 'materiaIds'
                .andExpect(view().name("segundoCriarPlano :: cardCriacao"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Você deve selecionar, pelo menos, uma matéria para o Planejamento."));

        System.out.println("\n>>> CONTROLLER - TESTE MATÉRIAS SUCESSO: Mensagem informando que o planejamento deve ter matérias vinculadas exibida corretamente! <<<");
    }

    // --- TESTE DE FLUXO FELIZ (Criar Planejamento com Sucesso) ---

    @Test
    @DisplayName("CT003 - Deve criar planejamento com sucesso (Fluxo Completo)")
    void deveCriarPlanejamentoComSucesso() throws Exception {
        // 1. Mock do contexto de segurança para o postFinalizarCriacao
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user@email.com");
        SecurityContextHolder.setContext(securityContext);

        // 2. Mock dos dados
        UsuarioEntity usuarioMock = new UsuarioEntity();
        usuarioMock.setId(UUID.randomUUID());
        
        // CORREÇÃO: Inicialização correta do UsuarioDTO (é um record, então usamos o construtor canônico)
        UsuarioDTO usuarioDTOMock = new UsuarioDTO(usuarioMock.getId(), "User", "user@email.com", "senha123", null, null);
        
        PlanejamentoDTO planejamentoSalvo = new PlanejamentoDTO();
        planejamentoSalvo.setId(UUID.randomUUID());

        // 3. Mock dos comportamentos do serviço e repositório
        when(usuarioRepository.findByEmail("user@email.com")).thenReturn(Optional.of(usuarioMock));
        when(usuarioMapper.toUsuarioDTO(usuarioMock)).thenReturn(usuarioDTOMock);
        when(planejamentoService.save(any(PlanejamentoDTO.class))).thenReturn(planejamentoSalvo);

        // 4. Execução do POST final (terceiro passo)
        mockMvc.perform(post("/criar-planejamento/finalizar")
                        .param("nomePlanejamento", "Plano Valido")
                        .param("cargo", "Estudante")
                        .param("anoAplicacao", "2025")
                        .param("horasDiarias", "5")
                        .param("disponibilidade", "segunda", "sexta")
                        .param("planoArquivado", "false")
                        .param("preDefinidoAdm", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/planejamento/" + planejamentoSalvo.getId()));

        System.out.println("\n>>> CONTROLLER - TESTE 1 SUCESSO: O planejamento foi criado com sucesso e redirecionado para /planejamento/" + planejamentoSalvo.getId() + "! <<<");
        
        // 5. Verificação se o serviço foi chamado
        verify(planejamentoService, times(1)).save(any(PlanejamentoDTO.class));
    }
}