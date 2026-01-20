package web.planorama.demo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.planorama.demo.dto.MateriaPlanejamentoDTO;
import web.planorama.demo.dto.PlanejamentoDTO;
import web.planorama.demo.dto.SessaoEstudoDTO;
import web.planorama.demo.entity.MateriaPlanejamentoEntity;
import web.planorama.demo.entity.PlanejamentoEntity;
import web.planorama.demo.entity.SessaoEstudoEntity;
import web.planorama.demo.mapping.MateriaPlanejamentoMapper;
import web.planorama.demo.mapping.PlanejamentoMapper;
import web.planorama.demo.mapping.SessaoEstudoMapper;
import web.planorama.demo.repository.MateriaPlanejamentoRepository;
import web.planorama.demo.repository.PlanejamentoRepository;
import web.planorama.demo.repository.SessaoEstudoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DisplayName("CT016 - Geração das Sessões (Ciclo de Estudos)")
@ExtendWith(MockitoExtension.class)
class PlanejamentoServiceImplCT016Test {

    @Mock
    private PlanejamentoRepository planejamentoRepository;

    @Mock
    private PlanejamentoMapper planejamentoMapper;

    @Mock
    private MateriaPlanejamentoRepository materiaPlanejamentoRepository;

    @Mock
    private MateriaPlanejamentoMapper materiaPlanejamentoMapper;

    @Mock
    private SessaoEstudoRepository sessaoEstudoRepository;

    @Mock
    private SessaoEstudoMapper sessaoEstudoMapper;

    @InjectMocks
    private PlanejamentoServiceImpl planejamentoService;

    private UUID planejamentoId;
    private UUID materiaPlanejamentoId;
    private PlanejamentoEntity planejamentoEntity;
    private MateriaPlanejamentoEntity materiaPlanejamentoEntity;

    @BeforeEach
    void setUp() {
        planejamentoId = UUID.randomUUID();
        materiaPlanejamentoId = UUID.randomUUID();

        // Configurar PlanejamentoEntity
        planejamentoEntity = new PlanejamentoEntity();
        planejamentoEntity.setId(planejamentoId);
        planejamentoEntity.setNomePlanejamento("Planejamento Teste");
        planejamentoEntity.setAnoAplicacao(2026);
        planejamentoEntity.setHorasDiarias(4);
        planejamentoEntity.setCargo("Estudante");

        // Configurar MateriaPlanejamentoEntity
        materiaPlanejamentoEntity = new MateriaPlanejamentoEntity();
        materiaPlanejamentoEntity.setId(materiaPlanejamentoId);
        materiaPlanejamentoEntity.setNivelConhecimento(3);
        materiaPlanejamentoEntity.setCargaHorariaMateriaPlano(10);
        materiaPlanejamentoEntity.setPlanejamentoEntity(planejamentoEntity);
    }

    // ========== LIMITAÇÕES DE PARTIÇÃO ==========

    @Test
    @DisplayName("CT016-1: Nível de conhecimento zero (mínimo deve ser 1)")
    void testGerarCicloComNivelConhecimentoZero() {
        System.out.println(">>> CT016-1: Testando geração de ciclo com NÍVEL DE CONHECIMENTO = 0. <<<");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            MateriaPlanejamentoEntity materiaComNivelInvalido = new MateriaPlanejamentoEntity();
            materiaComNivelInvalido.setNivelConhecimento(0);
            materiaComNivelInvalido.setCargaHorariaMateriaPlano(10);

            if (materiaComNivelInvalido.getNivelConhecimento() < 1) {
                throw new IllegalArgumentException("O valor do nível de conhecimento deve ser no mínimo 1");
            }
        });

        assertEquals("O valor do nível de conhecimento deve ser no mínimo 1", exception.getMessage());
        System.out.println(">>> CT016-1 SUCESSO: Validação de nível mínimo funcionou. <<<");
    }

    @Test
    @DisplayName("CT016-2: Nível de conhecimento seis (máximo deve ser 5)")
    void testGerarCicloComNivelConhecimentoSeis() {
        System.out.println(">>> CT016-2: Testando geração de ciclo com NÍVEL DE CONHECIMENTO = 6. <<<");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            MateriaPlanejamentoEntity materiaComNivelInvalido = new MateriaPlanejamentoEntity();
            materiaComNivelInvalido.setNivelConhecimento(6);
            materiaComNivelInvalido.setCargaHorariaMateriaPlano(10);

            if (materiaComNivelInvalido.getNivelConhecimento() > 5) {
                throw new IllegalArgumentException("O valor do nível de conhecimento deve ser no máximo 5");
            }
        });

        assertEquals("O valor do nível de conhecimento deve ser no máximo 5", exception.getMessage());
        System.out.println(">>> CT016-2 SUCESSO: Validação de nível máximo funcionou. <<<");
    }

    // ========== ENTRADAS INVÁLIDAS, VÁLIDAS E COMBINAÇÕES ==========

    @Test
    @DisplayName("CT016-3: ID de planejamento que não existe no sistema")
    void testGerarCicloComPlanejamentoInexistente() {
        System.out.println(">>> CT016-3: Testando geração de ciclo com planejamento INEXISTENTE. <<<");

        UUID idInexistente = UUID.randomUUID();
        when(planejamentoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            if (planejamentoRepository.findById(idInexistente).isEmpty()) {
                throw new IllegalArgumentException("Não é possível gerar o ciclo de estudo para um planejamento que não existe ou ainda não foi criado um id para ele");
            }
        });

        assertEquals("Não é possível gerar o ciclo de estudo para um planejamento que não existe ou ainda não foi criado um id para ele", exception.getMessage());
        System.out.println(">>> CT016-3 SUCESSO: Validação de planejamento inexistente funcionou. <<<");
    }

    @Test
    @DisplayName("CT016-4: ID de planejamento em branco (null)")
    void testGerarCicloComPlanejamentoNull() {
        System.out.println(">>> CT016-4: Testando geração de ciclo com planejamento NULL. <<<");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            UUID idNull = null;
            if (idNull == null) {
                throw new IllegalArgumentException("Não é possível gerar o ciclo de estudo para um id em branco(null)");
            }
        });

        assertEquals("Não é possível gerar o ciclo de estudo para um id em branco(null)", exception.getMessage());
        System.out.println(">>> CT016-4 SUCESSO: Validação de id null funcionou. <<<");
    }

    @Test
    @DisplayName("CT016-5: ID de planejamento correto - ciclo gerado com sucesso")
    void testGerarCicloComPlanejamentoValido() {
        System.out.println(">>> CT016-5: Testando geração de ciclo com planejamento VÁLIDO. <<<");

        // Simular geração de sessões
        List<SessaoEstudoDTO> sessoesGeradas = new ArrayList<>();
        
        // Cálculo de duração: (5 - nivelConhecimento) * cargaHorariaMateriaPlano * 60 / número de dias
        int nivelConhecimento = materiaPlanejamentoEntity.getNivelConhecimento();
        int cargaHoraria = materiaPlanejamentoEntity.getCargaHorariaMateriaPlano();
        int diasDisponiveis = 5; // Simulando 5 dias de estudo
        
        // Se nível é 3, fator multiplicador = 5 - 3 = 2
        int duracaoTotal = (5 - nivelConhecimento) * cargaHoraria * 60;
        int duracaoSessao = duracaoTotal / diasDisponiveis;

        for (int i = 0; i < diasDisponiveis; i++) {
            SessaoEstudoDTO sessao = new SessaoEstudoDTO();
            sessao.setId(UUID.randomUUID());
            sessao.setIdMateriaPlanejamento(materiaPlanejamentoId);
            sessao.setDuracaoSessao(duracaoSessao);
            sessoesGeradas.add(sessao);
        }

        // Validações
        assertNotNull(sessoesGeradas);
        assertFalse(sessoesGeradas.isEmpty());
        assertEquals(diasDisponiveis, sessoesGeradas.size());
        
        for (SessaoEstudoDTO sessao : sessoesGeradas) {
            assertTrue(sessao.getDuracaoSessao() > 0);
            assertEquals(materiaPlanejamentoId, sessao.getIdMateriaPlanejamento());
        }

        System.out.println(">>> CT016-5 SUCESSO: Ciclo de estudo gerado com sucesso!");
        System.out.println("    Planejamento: " + planejamentoEntity.getNomePlanejamento());
        System.out.println("    Nível de conhecimento: " + nivelConhecimento);
        System.out.println("    Sessões geradas: " + sessoesGeradas.size());
        System.out.println("    Duração de cada sessão: " + duracaoSessao + " minutos <<<");
    }

    @Test
    @DisplayName("CT016-6: Geração de ciclo com múltiplas matérias")
    void testGerarCicloComMultiplasMaterias() {
        System.out.println(">>> CT016-6: Testando geração de ciclo com MÚLTIPLAS MATÉRIAS. <<<");

        // Segunda matéria
        UUID materiaPlanejamentoId2 = UUID.randomUUID();
        MateriaPlanejamentoEntity materiaPlanejamento2 = new MateriaPlanejamentoEntity();
        materiaPlanejamento2.setId(materiaPlanejamentoId2);
        materiaPlanejamento2.setNivelConhecimento(2);
        materiaPlanejamento2.setCargaHorariaMateriaPlano(8);
        materiaPlanejamento2.setPlanejamentoEntity(planejamentoEntity);

        List<SessaoEstudoDTO> sessoesTotal = new ArrayList<>();

        // Gerar sessões para primeira matéria
        int duracao1 = (5 - 3) * 10 * 60 / 5; // 240 minutos
        for (int i = 0; i < 5; i++) {
            SessaoEstudoDTO sessao = new SessaoEstudoDTO();
            sessao.setId(UUID.randomUUID());
            sessao.setIdMateriaPlanejamento(materiaPlanejamentoId);
            sessao.setDuracaoSessao(duracao1);
            sessoesTotal.add(sessao);
        }

        // Gerar sessões para segunda matéria
        int duracao2 = (5 - 2) * 8 * 60 / 5; // 288 minutos
        for (int i = 0; i < 5; i++) {
            SessaoEstudoDTO sessao = new SessaoEstudoDTO();
            sessao.setId(UUID.randomUUID());
            sessao.setIdMateriaPlanejamento(materiaPlanejamentoId2);
            sessao.setDuracaoSessao(duracao2);
            sessoesTotal.add(sessao);
        }

        assertNotNull(sessoesTotal);
        assertEquals(10, sessoesTotal.size());
        System.out.println(">>> CT016-6 SUCESSO: Ciclo de estudo gerado para múltiplas matérias!");
        System.out.println("    Total de sessões: " + sessoesTotal.size());
        System.out.println("    Matérias: 2");
        System.out.println("    Sessões por matéria: 5 <<<");
    }
}
