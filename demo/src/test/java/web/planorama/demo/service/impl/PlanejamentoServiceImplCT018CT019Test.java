package web.planorama.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import web.planorama.demo.dto.PlanejamentoDTO;
import web.planorama.demo.dto.PlanejamentoProgressDTO;
import web.planorama.demo.entity.MateriaPlanejamentoEntity;
import web.planorama.demo.entity.PlanejamentoEntity;
import web.planorama.demo.entity.UsuarioEntity;
import web.planorama.demo.exceptions.MyNotFoundException;
import web.planorama.demo.mapping.PlanejamentoMapper;
import web.planorama.demo.mapping.MateriaPlanejamentoMapper;
import web.planorama.demo.mapping.SessaoEstudoMapper;
import web.planorama.demo.repository.PlanejamentoRepository;
import web.planorama.demo.repository.RegistrarEstudoRepository;
import web.planorama.demo.repository.UsuarioRepository;
import web.planorama.demo.repository.SessaoEstudoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CT018 e CT019 - Listagem e Seleção de Planejamentos")
class PlanejamentoServiceImplCT018CT019Test {

    @Mock private PlanejamentoRepository planejamentoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RegistrarEstudoRepository registrarEstudoRepository;
    @Mock private SessaoEstudoRepository sessaoEstudoRepository;
    @Mock private PlanejamentoMapper planejamentoMapper;
    @Mock private MateriaPlanejamentoMapper materiaPlanejamentoMapper;
    @Mock private SessaoEstudoMapper sessaoEstudoMapper;

    // ========== CT018: Listagem de Planejamentos do Usuário ==========

    @Test
    @DisplayName("CT018-1: Listar planejamentos para usuário com planejamentos → Retorna lista com planejamentos")
    void testListarPlanejamentosComPlanejamentosExistentes() {
        System.out.println("\n>>> CT018-1: Listando planejamentos para usuário com planejamentos. <<<");

        // --- ARRANGE ---
        UUID idUsuario = UUID.randomUUID();
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(idUsuario);
        usuario.setEmail("user@test.com");

        // Criar 2 planejamentos não arquivados
        PlanejamentoEntity plano1 = new PlanejamentoEntity();
        plano1.setId(UUID.randomUUID());
        plano1.setNomePlanejamento("Plano 1");
        plano1.setCriador(usuario);
        plano1.setPlanoArquivado(false);
        plano1.setMaterias(new ArrayList<>());

        PlanejamentoEntity plano2 = new PlanejamentoEntity();
        plano2.setId(UUID.randomUUID());
        plano2.setNomePlanejamento("Plano 2");
        plano2.setCriador(usuario);
        plano2.setPlanoArquivado(false);
        plano2.setMaterias(new ArrayList<>());

        List<PlanejamentoEntity> planosUsuario = new ArrayList<>();
        planosUsuario.add(plano1);
        planosUsuario.add(plano2);

        // Mock para repository
        when(planejamentoRepository.findAllByCriador(usuario))
                .thenReturn(planosUsuario);

        // Mock para mapper
        PlanejamentoDTO dto1 = new PlanejamentoDTO();
        dto1.setId(plano1.getId());
        dto1.setNomePlanejamento("Plano 1");

        PlanejamentoDTO dto2 = new PlanejamentoDTO();
        dto2.setId(plano2.getId());
        dto2.setNomePlanejamento("Plano 2");

        when(planejamentoMapper.toPlanejamentoDTO(plano1)).thenReturn(dto1);
        when(planejamentoMapper.toPlanejamentoDTO(plano2)).thenReturn(dto2);

        // Criar serviço sem mocking de métodos privados
        PlanejamentoServiceImpl planejamentoService = new PlanejamentoServiceImpl(
                planejamentoRepository,
                usuarioRepository,
                null,
                sessaoEstudoRepository,
                registrarEstudoRepository,
                planejamentoMapper,
                materiaPlanejamentoMapper,
                sessaoEstudoMapper
        );

        // --- ACT ---
        List<PlanejamentoProgressDTO> resultado = planejamentoService.findAllComProgressoByUsuario(usuario);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "Deveria retornar 2 planejamentos");
        assertEquals("Plano 1", resultado.get(0).getPlanejamento().getNomePlanejamento());
        assertEquals("Plano 2", resultado.get(1).getPlanejamento().getNomePlanejamento());
        System.out.println(">>> CT018-1 SUCESSO: 2 planejamentos listados com sucesso. <<<\n");
    }

    @Test
    @DisplayName("CT018-2: Listar planejamentos para usuário SEM planejamentos → Retorna lista vazia")
    void testListarPlanejamentosSemPlanejamentosExistentes() {
        System.out.println("\n>>> CT018-2: Listando planejamentos para usuário sem planejamentos. <<<");

        // --- ARRANGE ---
        UUID idUsuario = UUID.randomUUID();
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(idUsuario);
        usuario.setEmail("user@test.com");

        List<PlanejamentoEntity> planosVazio = new ArrayList<>();

        when(planejamentoRepository.findAllByCriador(usuario))
                .thenReturn(planosVazio);

        PlanejamentoServiceImpl planejamentoService = new PlanejamentoServiceImpl(
                planejamentoRepository,
                usuarioRepository,
                null,
                sessaoEstudoRepository,
                registrarEstudoRepository,
                planejamentoMapper,
                materiaPlanejamentoMapper,
                sessaoEstudoMapper
        );

        // --- ACT ---
        List<PlanejamentoProgressDTO> resultado = planejamentoService.findAllComProgressoByUsuario(usuario);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(0, resultado.size(), "Deveria retornar lista vazia");
        System.out.println(">>> CT018-2 SUCESSO: Lista vazia retornada para usuário sem planejamentos. <<<\n");
    }

    // ========== CT019: Seleção de Planejamento Pré-Definido ==========

    @Test
    @DisplayName("CT019-1: Selecionar planejamento pré-definido por admin → Cria cópia com sucesso")
    void testSelecionarPlanoPredefinidoDoAdmin() {
        System.out.println("\n>>> CT019-1: Selecionando planejamento pré-definido do administrador. <<<");

        // --- ARRANGE ---
        UUID idAdmin = UUID.randomUUID();
        UUID idEstudante = UUID.randomUUID();
        UUID idPlanoPredefinido = UUID.randomUUID();
        UUID idPlanoCopia = UUID.randomUUID();

        UsuarioEntity admin = new UsuarioEntity();
        admin.setId(idAdmin);
        admin.setEmail("admin@test.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setId(idEstudante);
        estudante.setEmail("estudante@test.com");

        // Planejamento pré-definido do admin
        PlanejamentoEntity planoPredefinido = new PlanejamentoEntity();
        planoPredefinido.setId(idPlanoPredefinido);
        planoPredefinido.setNomePlanejamento("Plano Pré-Definido");
        planoPredefinido.setCriador(admin);
        planoPredefinido.setPreDefinidoAdm(true);
        planoPredefinido.setPlanoArquivado(false);
        planoPredefinido.setDisponibilidade(List.of("segunda", "terca"));
        planoPredefinido.setHorasDiarias(5);
        planoPredefinido.setMaterias(new ArrayList<>());

        PlanejamentoDTO dtoPredefinido = new PlanejamentoDTO();
        dtoPredefinido.setId(idPlanoCopia);
        dtoPredefinido.setNomePlanejamento("Plano Pré-Definido");
        dtoPredefinido.setPreDefinidoAdm(false);

        when(planejamentoRepository.findById(idPlanoPredefinido))
                .thenReturn(Optional.of(planoPredefinido));

        when(usuarioRepository.findByEmail(estudante.getEmail()))
                .thenReturn(Optional.of(estudante));

        when(planejamentoRepository.save(any(PlanejamentoEntity.class)))
                .thenAnswer(invocation -> {
                    PlanejamentoEntity savedPlano = invocation.getArgument(0);
                    savedPlano.setId(idPlanoCopia);
                    return savedPlano;
                });

        // Mock para gerarCicloDeEstudos - buscar o planejamento salvo
        when(planejamentoRepository.findById(idPlanoCopia))
                .thenReturn(Optional.of(new PlanejamentoEntity()));

        when(planejamentoMapper.toPlanejamentoDTO(any(PlanejamentoEntity.class)))
                .thenReturn(dtoPredefinido);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);

            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn(estudante.getEmail());

            PlanejamentoServiceImpl planejamentoService = new PlanejamentoServiceImpl(
                    planejamentoRepository,
                    usuarioRepository,
                    null,
                    sessaoEstudoRepository,
                    registrarEstudoRepository,
                    planejamentoMapper,
                    materiaPlanejamentoMapper,
                    sessaoEstudoMapper
            );

            // --- ACT ---
            PlanejamentoDTO resultado = planejamentoService.selecionarPlanoPredefinido(idPlanoPredefinido);

            // --- ASSERT ---
            assertNotNull(resultado);
            assertEquals("Plano Pré-Definido", resultado.getNomePlanejamento());
            assertFalse(resultado.isPreDefinidoAdm(), "Cópia não deveria ser pré-definida");
            System.out.println(">>> CT019-1 SUCESSO: Planejamento pré-definido selecionado com sucesso. <<<\n");
        }
    }

    @Test
    @DisplayName("CT019-2: Selecionar planejamento inexistente → Lança MyNotFoundException")
    void testSelecionarPlanoInexistente() {
        System.out.println("\n>>> CT019-2: Selecionando planejamento inexistente. <<<");

        // --- ARRANGE ---
        UUID idPlanoInexistente = UUID.randomUUID();
        UUID idEstudante = UUID.randomUUID();

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setId(idEstudante);
        estudante.setEmail("estudante@test.com");

        when(planejamentoRepository.findById(idPlanoInexistente))
                .thenReturn(Optional.empty());

        when(usuarioRepository.findByEmail(estudante.getEmail()))
                .thenReturn(Optional.of(estudante));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);

            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn(estudante.getEmail());

            PlanejamentoServiceImpl planejamentoService = new PlanejamentoServiceImpl(
                    planejamentoRepository,
                    usuarioRepository,
                    null,
                    sessaoEstudoRepository,
                    registrarEstudoRepository,
                    planejamentoMapper,
                    materiaPlanejamentoMapper,
                    sessaoEstudoMapper
            );

            // --- ACT & ASSERT ---
            assertThrows(MyNotFoundException.class,
                    () -> planejamentoService.selecionarPlanoPredefinido(idPlanoInexistente),
                    "Deveria lançar MyNotFoundException para planejamento inexistente");
            System.out.println(">>> CT019-2 SUCESSO: MyNotFoundException lançada para planejamento inexistente. <<<\n");
        }
    }

    @Test
    @DisplayName("CT019-3: Selecionar planejamento criado por estudante → Permite seleção (não verifica criador)")
    void testSelecionarPlanoDeOutroEstudante() {
        System.out.println("\n>>> CT019-3: Selecionando planejamento criado por outro estudante. <<<");

        // --- ARRANGE ---
        UUID idEstudante1 = UUID.randomUUID();
        UUID idEstudante2 = UUID.randomUUID();
        UUID idPlanoEstudante = UUID.randomUUID();
        UUID idPlanoCopia = UUID.randomUUID();

        UsuarioEntity estudante1 = new UsuarioEntity();
        estudante1.setId(idEstudante1);
        estudante1.setEmail("estudante1@test.com");

        UsuarioEntity estudante2 = new UsuarioEntity();
        estudante2.setId(idEstudante2);
        estudante2.setEmail("estudante2@test.com");

        // Planejamento criado por estudante1
        PlanejamentoEntity planoEstudante = new PlanejamentoEntity();
        planoEstudante.setId(idPlanoEstudante);
        planoEstudante.setNomePlanejamento("Plano Estudante 1");
        planoEstudante.setCriador(estudante1);
        planoEstudante.setPreDefinidoAdm(false);
        planoEstudante.setPlanoArquivado(false);
        planoEstudante.setDisponibilidade(List.of("segunda"));
        planoEstudante.setHorasDiarias(3);
        planoEstudante.setMaterias(new ArrayList<>());

        PlanejamentoDTO dtoPlanoEstudante = new PlanejamentoDTO();
        dtoPlanoEstudante.setId(idPlanoCopia);
        dtoPlanoEstudante.setNomePlanejamento("Plano Estudante 1");
        dtoPlanoEstudante.setPreDefinidoAdm(false);

        when(planejamentoRepository.findById(idPlanoEstudante))
                .thenReturn(Optional.of(planoEstudante));

        when(usuarioRepository.findByEmail(estudante2.getEmail()))
                .thenReturn(Optional.of(estudante2));

        when(planejamentoRepository.save(any(PlanejamentoEntity.class)))
                .thenAnswer(invocation -> {
                    PlanejamentoEntity savedPlano = invocation.getArgument(0);
                    savedPlano.setId(idPlanoCopia);
                    return savedPlano;
                });

        // Mock para gerarCicloDeEstudos
        when(planejamentoRepository.findById(idPlanoCopia))
                .thenReturn(Optional.of(new PlanejamentoEntity()));

        when(planejamentoMapper.toPlanejamentoDTO(any(PlanejamentoEntity.class)))
                .thenReturn(dtoPlanoEstudante);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);

            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn(estudante2.getEmail());

            PlanejamentoServiceImpl planejamentoService = new PlanejamentoServiceImpl(
                    planejamentoRepository,
                    usuarioRepository,
                    null,
                    sessaoEstudoRepository,
                    registrarEstudoRepository,
                    planejamentoMapper,
                    materiaPlanejamentoMapper,
                    sessaoEstudoMapper
            );

            // --- ACT ---
            PlanejamentoDTO resultado = planejamentoService.selecionarPlanoPredefinido(idPlanoEstudante);

            // --- ASSERT ---
            assertNotNull(resultado);
            assertEquals("Plano Estudante 1", resultado.getNomePlanejamento());
            System.out.println(">>> CT019-3 SUCESSO: Planejamento de outro estudante selecionado (sem restrição de criador). <<<\n");
        }
    }
}
