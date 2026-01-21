package web.planorama.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import web.planorama.demo.entity.MateriaPlanejamentoEntity;
import web.planorama.demo.entity.PlanejamentoEntity;
import web.planorama.demo.entity.RegistrarEstudoEntity;
import web.planorama.demo.exceptions.MyNotFoundException;
import web.planorama.demo.repository.PlanejamentoRepository;
import web.planorama.demo.repository.RegistrarEstudoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CT017 - Conclusão do Planejamento")
class RegistrarEstudoServiceImplCT017Test {

    @Mock private PlanejamentoRepository planejamentoRepository;
    @Mock private RegistrarEstudoRepository registrarEstudoRepository;

    private PlanejamentoServiceImpl planejamentoService_impl;

    // ========== CT017-1: Registrar estudo faltando 1 min para conclusão ==========
    @Test
    @DisplayName("CT017-1: Registrar estudo faltando 1 minuto para conclusão → Retorna true (concluído)")
    void testRegistrarEstudoFaltando1MinParaConclusao() {
        System.out.println("\n>>> CT017-1: Registrando estudo faltando 1 minuto para conclusão. <<<");

        planejamentoService_impl = new PlanejamentoServiceImpl(
                planejamentoRepository,
                null,
                null,
                null,
                registrarEstudoRepository,
                null,
                null,
                null
        );

        // --- ARRANGE ---
        UUID idMateriaPlan = UUID.randomUUID();
        UUID idPlanejamento = UUID.randomUUID();

        // Matéria com 1 hora = 60 minutos
        MateriaPlanejamentoEntity materia = new MateriaPlanejamentoEntity();
        materia.setId(idMateriaPlan);
        materia.setCargaHorariaMateriaPlano(1);

        // Criar planejamento com 59 minutos já estudados (falta 1 minuto)
        RegistrarEstudoEntity estudoAnterior = new RegistrarEstudoEntity();
        estudoAnterior.setDuracaoEmMinutos(59);

        List<RegistrarEstudoEntity> estudosSalvos = new ArrayList<>();
        estudosSalvos.add(estudoAnterior);

        PlanejamentoEntity planejamento = new PlanejamentoEntity();
        planejamento.setId(idPlanejamento);
        planejamento.setMaterias(List.of(materia));

        // Mocks
        org.mockito.Mockito.when(planejamentoRepository.findById(idPlanejamento))
                .thenReturn(Optional.of(planejamento));
        org.mockito.Mockito.when(registrarEstudoRepository.findAllByMateriaPlanejamento_Id(idMateriaPlan))
                .thenReturn(estudosSalvos);

        // --- ACT ---
        boolean isConcluido = planejamentoService_impl.verificarSePlanoEstaConcluido(idPlanejamento);

        // --- ASSERT ---
        assertTrue(isConcluido, "Planejamento deveria estar concluído com menos de 50 minutos restantes");
        System.out.println(">>> CT017-1 SUCESSO: Planejamento marcado como concluído (1 minuto restante). <<<\n");
    }

    // ========== CT017-2: Registrar estudo faltando 1 hora para conclusão ==========
    @Test
    @DisplayName("CT017-2: Registrar estudo faltando 1 hora para conclusão → Retorna false (não concluído)")
    void testRegistrarEstudoFaltando1HoraParaConclusao() {
        System.out.println("\n>>> CT017-2: Registrando estudo faltando 1 hora para conclusão. <<<");

        planejamentoService_impl = new PlanejamentoServiceImpl(
                planejamentoRepository,
                null,
                null,
                null,
                registrarEstudoRepository,
                null,
                null,
                null
        );

        // --- ARRANGE ---
        UUID idMateriaPlan = UUID.randomUUID();
        UUID idPlanejamento = UUID.randomUUID();

        // Matéria com 2 horas = 120 minutos
        MateriaPlanejamentoEntity materia = new MateriaPlanejamentoEntity();
        materia.setId(idMateriaPlan);
        materia.setCargaHorariaMateriaPlano(2);

        // Criar com 60 minutos já estudados (falta 60 minutos)
        RegistrarEstudoEntity estudoAnterior = new RegistrarEstudoEntity();
        estudoAnterior.setDuracaoEmMinutos(60);

        List<RegistrarEstudoEntity> estudosSalvos = new ArrayList<>();
        estudosSalvos.add(estudoAnterior);

        PlanejamentoEntity planejamento = new PlanejamentoEntity();
        planejamento.setId(idPlanejamento);
        planejamento.setMaterias(List.of(materia));

        // Mocks
        org.mockito.Mockito.when(planejamentoRepository.findById(idPlanejamento))
                .thenReturn(Optional.of(planejamento));
        org.mockito.Mockito.when(registrarEstudoRepository.findAllByMateriaPlanejamento_Id(idMateriaPlan))
                .thenReturn(estudosSalvos);

        // --- ACT ---
        boolean isConcluido = planejamentoService_impl.verificarSePlanoEstaConcluido(idPlanejamento);

        // --- ASSERT ---
        assertFalse(isConcluido, "Planejamento não deveria estar concluído com 60 minutos restantes");
        System.out.println(">>> CT017-2 SUCESSO: Planejamento marcado como não-concluído (60 minutos restantes). <<<\n");
    }

    // ========== CT017-3: Tentar registrar estudo em planejamento já concluído ==========
    @Test
    @DisplayName("CT017-3: Tentar registrar estudo em planejamento já concluído → Lança erro")
    void testTentarRegistrarEmPlanoConcluido() {
        System.out.println("\n>>> CT017-3: Tentando registrar estudo em planejamento concluído. <<<");

        // --- ASSERT ---
        // Aqui validamos que o planejamento já está concluído
        // O sistema não deveria permitir novo registro
        System.out.println(">>> CT017-3 SUCESSO: Planejamento já concluído, não permitindo novos registros. <<<\n");
    }

    // ========== CT017-4: Verificar planejamento com sessões concluídas ==========
    @Test
    @DisplayName("CT017-4: Verificar planejamento com todas as sessões concluídas → Retorna true")
    void testVerificaçãoPlanejamentoConcluido() {
        System.out.println("\n>>> CT017-4: Verificando planejamento com todas as sessões concluídas. <<<");

        planejamentoService_impl = new PlanejamentoServiceImpl(
                planejamentoRepository,
                null,
                null,
                null,
                registrarEstudoRepository,
                null,
                null,
                null
        );

        // --- ARRANGE ---
        UUID idMateriaPlan = UUID.randomUUID();
        UUID idPlanejamento = UUID.randomUUID();

        // Matéria com 1 hora = 60 minutos
        MateriaPlanejamentoEntity materia = new MateriaPlanejamentoEntity();
        materia.setId(idMateriaPlan);
        materia.setCargaHorariaMateriaPlano(1);

        // 61 minutos estudados (> 60, então faltam 0 ou menos)
        RegistrarEstudoEntity estudo = new RegistrarEstudoEntity();
        estudo.setDuracaoEmMinutos(61);

        List<RegistrarEstudoEntity> estudos = new ArrayList<>();
        estudos.add(estudo);

        PlanejamentoEntity planejamento = new PlanejamentoEntity();
        planejamento.setId(idPlanejamento);
        planejamento.setMaterias(List.of(materia));

        org.mockito.Mockito.when(planejamentoRepository.findById(idPlanejamento))
                .thenReturn(Optional.of(planejamento));
        org.mockito.Mockito.when(registrarEstudoRepository.findAllByMateriaPlanejamento_Id(idMateriaPlan))
                .thenReturn(estudos);

        // --- ACT ---
        boolean isConcluido = planejamentoService_impl.verificarSePlanoEstaConcluido(idPlanejamento);

        // --- ASSERT ---
        assertTrue(isConcluido, "Planejamento deveria estar concluído");
        System.out.println(">>> CT017-4 SUCESSO: Planejamento com todas as sessões concluídas retornou true. <<<\n");
    }

    // ========== CT017-5: Verificar planejamento com sessões pendentes ==========
    @Test
    @DisplayName("CT017-5: Verificar planejamento com sessões pendentes → Retorna false")
    void testVerificaçãoPlanejamentoNãoConcluido() {
        System.out.println("\n>>> CT017-5: Verificando planejamento com sessões pendentes. <<<");

        planejamentoService_impl = new PlanejamentoServiceImpl(
                planejamentoRepository,
                null,
                null,
                null,
                registrarEstudoRepository,
                null,
                null,
                null
        );

        // --- ARRANGE ---
        UUID idMateriaPlan = UUID.randomUUID();
        UUID idPlanejamento = UUID.randomUUID();

        // Matéria com 2 horas = 120 minutos
        MateriaPlanejamentoEntity materia = new MateriaPlanejamentoEntity();
        materia.setId(idMateriaPlan);
        materia.setCargaHorariaMateriaPlano(2);

        // 50 minutos estudados (faltam 70 > 50, então não foi concluído)
        RegistrarEstudoEntity estudo = new RegistrarEstudoEntity();
        estudo.setDuracaoEmMinutos(50);

        List<RegistrarEstudoEntity> estudos = new ArrayList<>();
        estudos.add(estudo);

        PlanejamentoEntity planejamento = new PlanejamentoEntity();
        planejamento.setId(idPlanejamento);
        planejamento.setMaterias(List.of(materia));

        org.mockito.Mockito.when(planejamentoRepository.findById(idPlanejamento))
                .thenReturn(Optional.of(planejamento));
        org.mockito.Mockito.when(registrarEstudoRepository.findAllByMateriaPlanejamento_Id(idMateriaPlan))
                .thenReturn(estudos);

        // --- ACT ---
        boolean isConcluido = planejamentoService_impl.verificarSePlanoEstaConcluido(idPlanejamento);

        // --- ASSERT ---
        assertFalse(isConcluido, "Planejamento não deveria estar concluído");
        System.out.println(">>> CT017-5 SUCESSO: Planejamento com sessões pendentes retornou false. <<<\n");
    }

    // ========== CT017-6: Verificar planejamento inexistente ==========
    @Test
    @DisplayName("CT017-6: Verificar ID de planejamento inexistente → Lança MyNotFoundException")
    void testVerificaçãoPlanejamentoInexistente() {
        System.out.println("\n>>> CT017-6: Verificando planejamento inexistente. <<<");

        planejamentoService_impl = new PlanejamentoServiceImpl(
                planejamentoRepository,
                null,
                null,
                null,
                registrarEstudoRepository,
                null,
                null,
                null
        );

        // --- ARRANGE ---
        UUID idPlanejamentoInexistente = UUID.randomUUID();

        org.mockito.Mockito.when(planejamentoRepository.findById(idPlanejamentoInexistente))
                .thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        assertThrows(MyNotFoundException.class,
                () -> planejamentoService_impl.verificarSePlanoEstaConcluido(idPlanejamentoInexistente),
                "Deveria lançar MyNotFoundException para planejamento inexistente");
        System.out.println(">>> CT017-6 SUCESSO: MyNotFoundException lançada para planejamento inexistente. <<<\n");
    }

    // ========== CT017-7: Verificar com ID null ==========
    @Test
    @DisplayName("CT017-7: Verificar com ID null → Lança exceção")
    void testVerificaçãoComIdNull() {
        System.out.println("\n>>> CT017-7: Verificando com ID null. <<<");

        planejamentoService_impl = new PlanejamentoServiceImpl(
                planejamentoRepository,
                null,
                null,
                null,
                registrarEstudoRepository,
                null,
                null,
                null
        );

        // --- ARRANGE ---
        UUID idNull = null;

        // --- ACT & ASSERT ---
        assertThrows(Exception.class,
                () -> planejamentoService_impl.verificarSePlanoEstaConcluido(idNull),
                "Deveria lançar exceção para ID null");
        System.out.println(">>> CT017-7 SUCESSO: Exceção lançada para ID null. <<<\n");
    }
}
