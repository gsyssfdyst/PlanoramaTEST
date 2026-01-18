package web.planorama.demo.service.impl;

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

import web.planorama.demo.exceptions.MyNotFoundException;
import web.planorama.demo.mapping.MateriaPlanejamentoMapper;
import web.planorama.demo.mapping.PlanejamentoMapper;
import web.planorama.demo.mapping.SessaoEstudoMapper;
import web.planorama.demo.repository.MateriaRepository;
import web.planorama.demo.repository.PlanejamentoRepository;
import web.planorama.demo.repository.RegistrarEstudoRepository;
import web.planorama.demo.repository.SessaoEstudoRepository;
import web.planorama.demo.repository.UsuarioRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanejamentoServiceImplCT006Test {

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

    private SecurityContext securityContext;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        userDetails = mock(UserDetails.class);
        
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("CT006-1: Remover planejamento existente (SUCESSO)")
    void deveRemoverPlanejamentoComSucesso() {
        // Arrange
        UUID idPlanejamento = UUID.randomUUID();
        
        when(planejamentoRepository.existsById(idPlanejamento)).thenReturn(true);
        doNothing().when(planejamentoRepository).deleteById(idPlanejamento);

        // Act
        assertDoesNotThrow(() -> planejamentoService.remove(idPlanejamento));

        // Assert
        verify(planejamentoRepository, times(1)).existsById(idPlanejamento);
        verify(planejamentoRepository, times(1)).deleteById(idPlanejamento);
        
        System.out.println("\n>>> CT006-1 SUCESSO: Planejamento removido com sucesso - ID: " + idPlanejamento + " <<<\n");
    }

    @Test
    @DisplayName("CT006-2: Remover planejamento inexistente (FALHA)")
    void deveLancarExcecaoQuandoPlanejamentoNaoExistir() {
        // Arrange
        UUID idPlanejamento = UUID.randomUUID();
        
        when(planejamentoRepository.existsById(idPlanejamento)).thenReturn(false);

        // Act & Assert
        MyNotFoundException exception = assertThrows(MyNotFoundException.class, () -> {
            planejamentoService.remove(idPlanejamento);
        });

        assertEquals("Planejamento não encontrado.", exception.getMessage());
        verify(planejamentoRepository, times(1)).existsById(idPlanejamento);
        verify(planejamentoRepository, never()).deleteById(any(UUID.class));
        
        System.out.println("\n>>> CT006-2 SUCESSO: Exceção lançada corretamente - " + exception.getMessage() + " <<<\n");
    }

    @Test
    @DisplayName("CT006-3: Remover planejamento com ID null (FALHA)")
    void deveLancarExcecaoQuandoIdForNull() {
        // Arrange
        UUID idPlanejamento = null;
        
        // Act & Assert
        assertThrows(Exception.class, () -> {
            planejamentoService.remove(idPlanejamento);
        });

        verify(planejamentoRepository, never()).deleteById(any());
        
        System.out.println("\n>>> CT006-3 SUCESSO: Exceção lançada corretamente ao tentar remover com ID null <<<\n");
    }
}
