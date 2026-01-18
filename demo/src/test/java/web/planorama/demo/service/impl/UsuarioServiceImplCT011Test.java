package web.planorama.demo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import web.planorama.demo.dto.UsuarioDTO;
import web.planorama.demo.entity.UsuarioEntity;
import web.planorama.demo.mapping.UsuarioMapper;
import web.planorama.demo.repository.PapelRepository;
import web.planorama.demo.repository.UsuarioRepository;
import web.planorama.demo.service.UsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplCT011Test {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PapelRepository papelRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        // Setup básico para cada teste
    }

    @Test
    @DisplayName("CT011-1: Listar estudantes quando nenhum está cadastrado deve retornar lista vazia")
    void findAllEstudantesSemEstudantes() {
        // Arrange
        when(usuarioRepository.findByPapeisNome("ESTUDANTE")).thenReturn(new ArrayList<>());

        // Act
        List<UsuarioDTO> resultado = usuarioService.findAllEstudantes();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, times(1)).findByPapeisNome("ESTUDANTE");

        System.out.println("\n>>> CT011-1 SUCESSO: Lista de estudantes vazia (nenhum estudante cadastrado). <<<\n");
    }

    @Test
    @DisplayName("CT011-2: Listar estudantes com estudantes cadastrados deve retornar lista com nome e email")
    void findAllEstudantesComEstudantes() {
        // Arrange
        UUID estudante1Id = UUID.randomUUID();
        UUID estudante2Id = UUID.randomUUID();

        UsuarioEntity estudante1 = new UsuarioEntity();
        estudante1.setId(estudante1Id);
        estudante1.setNome("João Silva");
        estudante1.setEmail("joao@example.com");

        UsuarioEntity estudante2 = new UsuarioEntity();
        estudante2.setId(estudante2Id);
        estudante2.setNome("Maria Santos");
        estudante2.setEmail("maria@example.com");

        List<UsuarioEntity> estudantes = List.of(estudante1, estudante2);

        UsuarioDTO dto1 = new UsuarioDTO(estudante1Id, "João Silva", "joao@example.com", "senha123", null, null);
        UsuarioDTO dto2 = new UsuarioDTO(estudante2Id, "Maria Santos", "maria@example.com", "senha123", null, null);

        when(usuarioRepository.findByPapeisNome("ESTUDANTE")).thenReturn(estudantes);
        when(usuarioMapper.toUsuarioDTO(estudante1)).thenReturn(dto1);
        when(usuarioMapper.toUsuarioDTO(estudante2)).thenReturn(dto2);

        // Act
        List<UsuarioDTO> resultado = usuarioService.findAllEstudantes();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("João Silva", resultado.get(0).nome());
        assertEquals("joao@example.com", resultado.get(0).email());
        assertEquals("Maria Santos", resultado.get(1).nome());
        assertEquals("maria@example.com", resultado.get(1).email());

        verify(usuarioRepository, times(1)).findByPapeisNome("ESTUDANTE");
        verify(usuarioMapper, times(2)).toUsuarioDTO(any(UsuarioEntity.class));

        System.out.println("\n>>> CT011-2 SUCESSO: Listados 2 estudantes - João Silva (joao@example.com) e Maria Santos (maria@example.com). <<<\n");
    }

    @Test
    @DisplayName("CT011-3: Listar admins deve retornar sempre um único admin")
    void findAllAdmins() {
        // Arrange
        UUID adminId = UUID.randomUUID();

        UsuarioEntity admin = new UsuarioEntity();
        admin.setId(adminId);
        admin.setNome("Admin Único");
        admin.setEmail("admin@example.com");

        UsuarioDTO dtoAdmin = new UsuarioDTO(adminId, "Admin Único", "admin@example.com", "senha123", null, null);

        when(usuarioRepository.findByPapeisNome("ADMIN")).thenReturn(List.of(admin));
        when(usuarioMapper.toUsuarioDTO(admin)).thenReturn(dtoAdmin);

        // Act
        List<UsuarioDTO> resultado = usuarioService.findAllAdmins();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Admin Único", resultado.get(0).nome());
        assertEquals("admin@example.com", resultado.get(0).email());

        verify(usuarioRepository, times(1)).findByPapeisNome("ADMIN");
        verify(usuarioMapper, times(1)).toUsuarioDTO(any(UsuarioEntity.class));

        System.out.println("\n>>> CT011-3 SUCESSO: Único admin listado - Admin Único (admin@example.com). <<<\n");
    }

    @Test
    @DisplayName("CT011-4: Listar todos os usuários deve retornar admin e estudantes")
    void findAllComAdminEEstudantes() {
        // Arrange
        UUID adminId = UUID.randomUUID();
        UUID estudanteId = UUID.randomUUID();

        UsuarioEntity admin = new UsuarioEntity();
        admin.setId(adminId);
        admin.setNome("Admin Único");
        admin.setEmail("admin@example.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setId(estudanteId);
        estudante.setNome("João Silva");
        estudante.setEmail("joao@example.com");

        List<UsuarioEntity> todosUsuarios = List.of(admin, estudante);

        UsuarioDTO dtoAdmin = new UsuarioDTO(adminId, "Admin Único", "admin@example.com", "senha123", null, null);
        UsuarioDTO dtoEstudante = new UsuarioDTO(estudanteId, "João Silva", "joao@example.com", "senha123", null, null);

        when(usuarioRepository.findAll()).thenReturn(todosUsuarios);
        when(usuarioMapper.toUsuarioDTO(admin)).thenReturn(dtoAdmin);
        when(usuarioMapper.toUsuarioDTO(estudante)).thenReturn(dtoEstudante);

        // Act
        List<UsuarioDTO> resultado = usuarioService.findAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(u -> u.nome().equals("Admin Único")));
        assertTrue(resultado.stream().anyMatch(u -> u.nome().equals("João Silva")));

        verify(usuarioRepository, times(1)).findAll();
        verify(usuarioMapper, times(2)).toUsuarioDTO(any(UsuarioEntity.class));

        System.out.println("\n>>> CT011-4 SUCESSO: Listados 2 usuários - Admin Único e João Silva. <<<\n");
    }

    @Test
    @DisplayName("CT011-5: Listar todos os usuários com apenas admin cadastrado (sem estudantes)")
    void findAllComApenasAdmin() {
        // Arrange
        UUID adminId = UUID.randomUUID();

        UsuarioEntity admin = new UsuarioEntity();
        admin.setId(adminId);
        admin.setNome("Admin Único");
        admin.setEmail("admin@example.com");

        UsuarioDTO dtoAdmin = new UsuarioDTO(adminId, "Admin Único", "admin@example.com", "senha123", null, null);

        when(usuarioRepository.findAll()).thenReturn(List.of(admin));
        when(usuarioMapper.toUsuarioDTO(admin)).thenReturn(dtoAdmin);

        // Act
        List<UsuarioDTO> resultado = usuarioService.findAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Admin Único", resultado.get(0).nome());
        assertEquals("admin@example.com", resultado.get(0).email());

        verify(usuarioRepository, times(1)).findAll();
        verify(usuarioMapper, times(1)).toUsuarioDTO(any(UsuarioEntity.class));

        System.out.println("\n>>> CT011-5 SUCESSO: Único usuário listado - Admin Único (admin@example.com). <<<\n");
    }
}
