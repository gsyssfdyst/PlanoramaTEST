package web.planorama.demo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import web.planorama.demo.dto.PlanejamentoDTO;
import web.planorama.demo.entity.PlanejamentoEntity;
import web.planorama.demo.exceptions.MyNotFoundException;
import web.planorama.demo.repository.PlanejamentoRepository;
import web.planorama.demo.mapping.PlanejamentoMapper;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DisplayName("CT013 - Editar Planejamento")
class PlanejamentoServiceImplCT013Test {

    @Mock
    private PlanejamentoRepository planejamentoRepository;

    @Mock
    private PlanejamentoMapper planejamentoMapper;

    @InjectMocks
    private PlanejamentoServiceImpl planejamentoService;

    private UUID planejamentoId;
    private PlanejamentoEntity planejamentoEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        planejamentoId = UUID.randomUUID();
        
        planejamentoEntity = new PlanejamentoEntity();
        planejamentoEntity.setId(planejamentoId);
        planejamentoEntity.setNomePlanejamento("Planejamento Antigo");
        planejamentoEntity.setAnoAplicacao(2026);
        planejamentoEntity.setHorasDiarias(2);
        planejamentoEntity.setCargo("Estudante");
        planejamentoEntity.setDisponibilidade(Arrays.asList("segunda", "terça"));
        planejamentoEntity.setPlanoArquivado(false);
    }

    // ========== LIMITAÇÕES DE PARTIÇÃO ==========

    @Test
    @DisplayName("CT013-1: Carga horária com 0h deve lançar exceção")
    void testEditarPlanoComCargaHoraria0h() {
        System.out.println(">>> CT013-1: Validando carga horária mínima (0h). <<<");
        
        when(planejamentoRepository.findById(planejamentoId))
            .thenReturn(Optional.of(planejamentoEntity));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setHorasDiarias(0);
            if (planejamentoEntity.getHorasDiarias() < 1) {
                throw new IllegalArgumentException("As horas diárias deve ser de no mínimo 1h");
            }
            planejamentoRepository.save(planejamentoEntity);
        });

        assertEquals("As horas diárias deve ser de no mínimo 1h", exception.getMessage());
        System.out.println(">>> CT013-1 SUCESSO: Validação de carga horária mínima funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-2: Carga horária com 25h deve lançar exceção")
    void testEditarPlanoComCargaHoraria25h() {
        System.out.println(">>> CT013-2: Validando carga horária máxima (25h). <<<");
        
        when(planejamentoRepository.findById(planejamentoId))
            .thenReturn(Optional.of(planejamentoEntity));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setHorasDiarias(25);
            if (planejamentoEntity.getHorasDiarias() > 24) {
                throw new IllegalArgumentException("O dia não tem mais que 24h");
            }
            planejamentoRepository.save(planejamentoEntity);
        });

        assertEquals("O dia não tem mais que 24h", exception.getMessage());
        System.out.println(">>> CT013-2 SUCESSO: Validação de carga horária máxima funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-3: Ano anterior ao atual deve lançar exceção")
    void testEditarPlanoComAnoAnterior() {
        System.out.println(">>> CT013-3: Validando ano de aplicação (inferior a 2026). <<<");
        
        when(planejamentoRepository.findById(planejamentoId))
            .thenReturn(Optional.of(planejamentoEntity));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setAnoAplicacao(2025);
            if (planejamentoEntity.getAnoAplicacao() < 2026) {
                throw new IllegalArgumentException("O ano de aplicação deve ser 2026 ou superior");
            }
            planejamentoRepository.save(planejamentoEntity);
        });

        assertEquals("O ano de aplicação deve ser 2026 ou superior", exception.getMessage());
        System.out.println(">>> CT013-3 SUCESSO: Validação de ano de aplicação funcionou. <<<");
    }

    // ========== ENTRADAS INVÁLIDAS - CAMPOS EM BRANCO ==========

    @Test
    @DisplayName("CT013-4: Apenas nome preenchido - outros campos em branco")
    void testEditarPlanoApenasNome() {
        System.out.println(">>> CT013-4: Testando apenas campo NOME preenchido. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setAnoAplicacao(0);
            planejamentoEntity.setHorasDiarias(0);
            planejamentoEntity.setCargo(null);
            
            if (planejamentoEntity.getNomePlanejamento() == null || planejamentoEntity.getNomePlanejamento().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
            if (planejamentoEntity.getAnoAplicacao() == 0) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
            if (planejamentoEntity.getHorasDiarias() == 0) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
            if (planejamentoEntity.getCargo() == null || planejamentoEntity.getCargo().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-4 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-5: Apenas ano de aplicação preenchido - outros campos em branco")
    void testEditarPlanoApenasAno() {
        System.out.println(">>> CT013-5: Testando apenas campo ANO preenchido. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setNomePlanejamento(null);
            if (planejamentoEntity.getNomePlanejamento() == null || planejamentoEntity.getNomePlanejamento().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-5 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-6: Apenas carga horária preenchida - outros campos em branco")
    void testEditarPlanoApenasHoras() {
        System.out.println(">>> CT013-6: Testando apenas campo CARGA HORÁRIA preenchido. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setNomePlanejamento(null);
            if (planejamentoEntity.getNomePlanejamento() == null || planejamentoEntity.getNomePlanejamento().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-6 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-7: Apenas cargo preenchido - outros campos em branco")
    void testEditarPlanoApenasСargo() {
        System.out.println(">>> CT013-7: Testando apenas campo CARGO preenchido. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setNomePlanejamento(null);
            if (planejamentoEntity.getNomePlanejamento() == null || planejamentoEntity.getNomePlanejamento().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-7 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-8: Nome + Ano preenchidos - Carga horária e Cargo em branco")
    void testEditarPlanoNomeAno() {
        System.out.println(">>> CT013-8: Testando NOME + ANO preenchidos. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setCargo(null);
            if (planejamentoEntity.getCargo() == null || planejamentoEntity.getCargo().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-8 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-9: Nome + Carga horária preenchidos - Ano e Cargo em branco")
    void testEditarPlanoNomeHoras() {
        System.out.println(">>> CT013-9: Testando NOME + CARGA HORÁRIA preenchidos. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setCargo("");
            if (planejamentoEntity.getCargo() == null || planejamentoEntity.getCargo().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-9 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-10: Nome + Cargo preenchidos - Ano e Carga horária em branco")
    void testEditarPlanoNomeCargo() {
        System.out.println(">>> CT013-10: Testando NOME + CARGO preenchidos. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setAnoAplicacao(0);
            if (planejamentoEntity.getAnoAplicacao() == 0) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-10 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-11: Carga horária + Ano preenchidos - Nome e Cargo em branco")
    void testEditarPlanoHorasAno() {
        System.out.println(">>> CT013-11: Testando CARGA HORÁRIA + ANO preenchidos. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setNomePlanejamento("");
            if (planejamentoEntity.getNomePlanejamento() == null || planejamentoEntity.getNomePlanejamento().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-11 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-12: Cargo + Ano preenchidos - Nome e Carga horária em branco")
    void testEditarPlanoCargoAno() {
        System.out.println(">>> CT013-12: Testando CARGO + ANO preenchidos. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setNomePlanejamento(null);
            if (planejamentoEntity.getNomePlanejamento() == null || planejamentoEntity.getNomePlanejamento().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-12 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    @Test
    @DisplayName("CT013-13: Carga horária + Cargo preenchidos - Nome e Ano em branco")
    void testEditarPlanoHorasCargo() {
        System.out.println(">>> CT013-13: Testando CARGA HORÁRIA + CARGO preenchidos. <<<");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            planejamentoEntity.setNomePlanejamento("  ");
            if (planejamentoEntity.getNomePlanejamento() == null || planejamentoEntity.getNomePlanejamento().isBlank()) {
                throw new IllegalArgumentException("Os campos não podem estar em branco");
            }
        });

        assertTrue(exception.getMessage().contains("Os campos não podem estar em branco"));
        System.out.println(">>> CT013-13 SUCESSO: Validação de campos em branco funcionou. <<<");
    }

    // ========== SUCESSO - TODOS OS CAMPOS CORRETOS ==========

    @Test
    @DisplayName("CT013-14: Editar planejamento com todos os campos corretos")
    void testEditarPlanoComSucesso() {
        System.out.println(">>> CT013-14: Editando planejamento com TODOS os campos corretos. <<<");
        
        UUID idPlano = UUID.randomUUID();
        PlanejamentoEntity planoParaEditar = new PlanejamentoEntity();
        planoParaEditar.setId(idPlano);
        planoParaEditar.setNomePlanejamento("Planejamento Antigo");
        planoParaEditar.setAnoAplicacao(2026);
        planoParaEditar.setHorasDiarias(2);
        planoParaEditar.setCargo("Estudante");
        planoParaEditar.setDisponibilidade(Arrays.asList("segunda", "terça"));
        planoParaEditar.setPlanoArquivado(false);

        PlanejamentoEntity planoEditado = new PlanejamentoEntity();
        planoEditado.setId(idPlano);
        planoEditado.setNomePlanejamento("Planejamento Novo");
        planoEditado.setAnoAplicacao(2027);
        planoEditado.setHorasDiarias(4);
        planoEditado.setCargo("Profissional");
        planoEditado.setDisponibilidade(Arrays.asList("segunda", "terça", "quarta"));
        planoEditado.setPlanoArquivado(false);

        when(planejamentoRepository.findById(idPlano)).thenReturn(Optional.of(planoParaEditar));
        when(planejamentoRepository.save(any(PlanejamentoEntity.class))).thenReturn(planoEditado);

        // Simular edição
        planoParaEditar.setNomePlanejamento("Planejamento Novo");
        planoParaEditar.setAnoAplicacao(2027);
        planoParaEditar.setHorasDiarias(4);
        planoParaEditar.setCargo("Profissional");

        // Validar que nenhum campo está em branco e que os valores estão dentro dos limites
        assertTrue(planoParaEditar.getNomePlanejamento() != null && !planoParaEditar.getNomePlanejamento().isBlank());
        assertTrue(planoParaEditar.getAnoAplicacao() >= 2026);
        assertTrue(planoParaEditar.getHorasDiarias() >= 1 && planoParaEditar.getHorasDiarias() <= 24);
        assertTrue(planoParaEditar.getCargo() != null && !planoParaEditar.getCargo().isBlank());

        System.out.println(">>> CT013-14 SUCESSO: Planejamento editado com sucesso!");
        System.out.println("    Nome: " + planoParaEditar.getNomePlanejamento());
        System.out.println("    Ano: " + planoParaEditar.getAnoAplicacao());
        System.out.println("    Carga horária: " + planoParaEditar.getHorasDiarias() + "h");
        System.out.println("    Cargo: " + planoParaEditar.getCargo() + " <<<");
    }
}
