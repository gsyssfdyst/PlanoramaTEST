package web.planorama.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import web.planorama.demo.entity.PapelEntity;
import web.planorama.demo.entity.UsuarioEntity;
import web.planorama.demo.exceptions.MyNotFoundException;
import web.planorama.demo.mapping.UsuarioMapper;
import web.planorama.demo.repository.PapelRepository;
import web.planorama.demo.repository.UsuarioRepository;
import web.planorama.demo.service.UsuarioService;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CT012 - Remoção do Usuário")
class UsuarioServiceImplCT012Test {

    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private UsuarioMapper usuarioMapper;

    @MockBean
    private PapelRepository papelRepository;

    private UsuarioEntity usuarioEntity;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioServiceImpl(usuarioRepository, usuarioMapper, null, papelRepository);

        usuarioId = UUID.randomUUID();
        usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId(usuarioId);
        usuarioEntity.setNome("João Silva");
        usuarioEntity.setEmail("joao@example.com");
    }

    @Test
    @DisplayName("CT012-1: Remover usuário existente com sucesso")
    void testRemoverUsuarioExistenteComSucesso() {
        // Arrange
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> usuarioService.remove(usuarioId));

        // Assert
        verify(usuarioRepository, times(1)).existsById(usuarioId);
        verify(usuarioRepository, times(1)).deleteById(usuarioId);
        System.out.println(">>> CT012-1 SUCESSO: Usuário removido com sucesso! ID: " + usuarioId + " <<<");
    }

    @Test
    @DisplayName("CT012-2: Tentativa de remover usuário inexistente lança exceção")
    void testRemoverUsuarioInexistenteComErro() {
        // Arrange
        UUID usuarioInexistenteId = UUID.randomUUID();
        when(usuarioRepository.existsById(usuarioInexistenteId)).thenReturn(false);

        // Act & Assert
        assertThrows(MyNotFoundException.class, () -> usuarioService.remove(usuarioInexistenteId),
                "Deverá lançar MyNotFoundException ao tentar remover usuário inexistente");

        verify(usuarioRepository, times(1)).existsById(usuarioInexistenteId);
        verify(usuarioRepository, never()).deleteById(any());
        System.out.println(">>> CT012-2 SUCESSO: Exceção lançada ao tentar remover usuário inexistente. <<<");
    }

    @Test
    @DisplayName("CT012-3: Mensagem de erro é correta ao remover usuário não encontrado")
    void testMensagemErroUsuarioNaoEncontrado() {
        // Arrange
        UUID usuarioInexistenteId = UUID.randomUUID();
        when(usuarioRepository.existsById(usuarioInexistenteId)).thenReturn(false);

        // Act & Assert
        MyNotFoundException exception = assertThrows(MyNotFoundException.class,
                () -> usuarioService.remove(usuarioInexistenteId));

        assertEquals("Usuário não encontrado para remoção", exception.getMessage());
        System.out.println(">>> CT012-3 SUCESSO: Mensagem de erro correta: '" + exception.getMessage() + "' <<<");
    }

    @Test
    @DisplayName("CT012-4: Remover múltiplos usuários sequencialmente com sucesso")
    void testRemoverMultiplosUsuariosComSucesso() {
        // Arrange
        UUID usuario1 = UUID.randomUUID();
        UUID usuario2 = UUID.randomUUID();
        UUID usuario3 = UUID.randomUUID();

        when(usuarioRepository.existsById(usuario1)).thenReturn(true);
        when(usuarioRepository.existsById(usuario2)).thenReturn(true);
        when(usuarioRepository.existsById(usuario3)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> usuarioService.remove(usuario1));
        assertDoesNotThrow(() -> usuarioService.remove(usuario2));
        assertDoesNotThrow(() -> usuarioService.remove(usuario3));

        // Assert
        verify(usuarioRepository, times(3)).existsById(any());
        verify(usuarioRepository, times(1)).deleteById(usuario1);
        verify(usuarioRepository, times(1)).deleteById(usuario2);
        verify(usuarioRepository, times(1)).deleteById(usuario3);
        System.out.println(">>> CT012-4 SUCESSO: Múltiplos usuários removidos com sucesso! <<<");
    }
}
