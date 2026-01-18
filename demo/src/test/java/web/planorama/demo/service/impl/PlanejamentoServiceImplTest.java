package web.planorama.demo.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.planorama.demo.exceptions.MyNotFoundException;
import web.planorama.demo.mapping.MateriaPlanejamentoMapper;
import web.planorama.demo.mapping.PlanejamentoMapper;
import web.planorama.demo.mapping.SessaoEstudoMapper;
import web.planorama.demo.repository.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanejamentoServiceImplTest {

    @Mock
    private PlanejamentoRepository planejamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private SessaoEstudoRepository sessaoEstudoRepository;

    @Mock
    private RegistrarEstudoRepository registrarEstudoRepository;

    @Mock
    private PlanejamentoMapper mapper;

    @Mock
    private MateriaPlanejamentoMapper materiaPlanejamentoMapper;

    @Mock
    private SessaoEstudoMapper sessaoEstudoMapper;

    @InjectMocks
    private PlanejamentoServiceImpl planejamentoService;

    // ==================== TESTES CT006 - Remoção de Planejamento ====================

    @Test
    @DisplayName("CT006-1: Remover planejamento com ID existente (sucesso)")
    void ct006_removerPlanejamento_idExistente_sucesso() {
        // Arrange
        UUID idExistente = UUID.randomUUID();
        when(planejamentoRepository.existsById(idExistente)).thenReturn(true);
        doNothing().when(planejamentoRepository).deleteById(idExistente);

        // Act
        planejamentoService.remove(idExistente);

        // Assert
        verify(planejamentoRepository, times(1)).existsById(idExistente);
        verify(planejamentoRepository, times(1)).deleteById(idExistente);

        System.out.println("\n>>> CT006-1 SUCESSO: Planejamento removido com sucesso! ID: " + idExistente + " <<<\n");
    }

    @Test
    @DisplayName("CT006-2: Remover planejamento com ID inexistente (falha)")
    void ct006_removerPlanejamento_idInexistente_falha() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(planejamentoRepository.existsById(idInexistente)).thenReturn(false);

        // Act & Assert
        MyNotFoundException exception = assertThrows(MyNotFoundException.class, () -> {
            planejamentoService.remove(idInexistente);
        });

        assertEquals("Planejamento não encontrado.", exception.getMessage());
        verify(planejamentoRepository, times(1)).existsById(idInexistente);
        verify(planejamentoRepository, never()).deleteById(any());

        System.out.println("\n>>> CT006-2 FALHA ESPERADA: Planejamento não foi encontrado. ID: " + idInexistente + " <<<\n");
    }

    @Test
    @DisplayName("CT006-3: Remover planejamento com ID null (falha)")
    void ct006_removerPlanejamento_idNull_falha() {
        // Arrange
        UUID idNull = null;

        // Act & Assert
        // O método provavelmente lançará NullPointerException ou similar quando tentar verificar existsById
        assertThrows(Exception.class, () -> {
            planejamentoService.remove(idNull);
        });

        verify(planejamentoRepository, never()).deleteById(any());

        System.out.println("\n>>> CT006-3 FALHA ESPERADA: É necessário passar algum planejamento para remoção (ID null não permitido). <<<\n");
    }
}
