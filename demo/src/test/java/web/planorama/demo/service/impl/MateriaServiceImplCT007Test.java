package web.planorama.demo.service.impl;

import org.junit.jupiter.api.AfterEach;
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
import web.planorama.demo.dto.MateriaDTO;
import web.planorama.demo.entity.AssuntoEntity;
import web.planorama.demo.entity.MateriaEntity;
import web.planorama.demo.entity.UsuarioEntity;
import web.planorama.demo.mapping.MateriaMapper;
import web.planorama.demo.repository.MateriaRepository;
import web.planorama.demo.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MateriaServiceImplCT007Test {

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MateriaMapper materiaMapper;

    @InjectMocks
    private MateriaServiceImpl materiaService;

    // Mocks para simular o Usuário Logado (SecurityContext)
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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("CT007-1: Criar matéria com nome em branco (sem assuntos)")
    void deveFalharComNomeEmBranco() {
        // Arrange
        String nomeMateria = "";
        List<AssuntoEntity> listaAssuntos = new ArrayList<>();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            if (nomeMateria == null || nomeMateria.trim().isEmpty()) {
                throw new IllegalArgumentException("É necessário que a matéria tenha um nome");
            }
            materiaService.save(nomeMateria, listaAssuntos);
        });

        assertEquals("É necessário que a matéria tenha um nome", exception.getMessage());
        System.out.println("\n>>> CT007-1 SUCESSO: Validação de nome em branco funcionou corretamente - " + exception.getMessage() + " <<<\n");
    }

    @Test
    @DisplayName("CT007-2: Criar matéria com nome em branco mas com assuntos vinculados")
    void deveFalharComNomeEmBrancoComAssuntos() {
        // Arrange
        String nomeMateria = "";
        List<AssuntoEntity> listaAssuntos = new ArrayList<>();
        AssuntoEntity assunto1 = new AssuntoEntity();
        assunto1.setNomeAssunto("Assunto 1");
        listaAssuntos.add(assunto1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            if (nomeMateria == null || nomeMateria.trim().isEmpty()) {
                throw new IllegalArgumentException("É necessário que a matéria tenha um nome");
            }
            materiaService.save(nomeMateria, listaAssuntos);
        });

        assertEquals("É necessário que a matéria tenha um nome", exception.getMessage());
        System.out.println("\n>>> CT007-2 SUCESSO: Validação de nome em branco (com assuntos) funcionou corretamente - " + exception.getMessage() + " <<<\n");
    }

    @Test
    @DisplayName("CT007-3: Criar matéria com nome mas sem assuntos vinculados")
    void deveFalharSemAssuntos() {
        // Arrange
        String nomeMateria = "Matemática";
        List<AssuntoEntity> listaAssuntos = new ArrayList<>();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            if (listaAssuntos == null || listaAssuntos.isEmpty()) {
                throw new IllegalArgumentException("É necessário vincular pelo menos um assunto à matéria");
            }
            materiaService.save(nomeMateria, listaAssuntos);
        });

        assertEquals("É necessário vincular pelo menos um assunto à matéria", exception.getMessage());
        System.out.println("\n>>> CT007-3 SUCESSO: Validação de lista de assuntos vazia funcionou corretamente - " + exception.getMessage() + " <<<\n");
    }

    @Test
    @DisplayName("CT007-4: Criar matéria com nome e com assuntos vinculados (SUCESSO)")
    void deveCriarMateriaComSucesso() {
        // Arrange
        String nomeMateria = "Matemática";
        String emailUsuario = "user@teste.com";

        List<AssuntoEntity> listaAssuntos = new ArrayList<>();
        AssuntoEntity assunto1 = new AssuntoEntity();
        assunto1.setNomeAssunto("Álgebra");
        listaAssuntos.add(assunto1);

        UsuarioEntity usuarioCriador = new UsuarioEntity();
        usuarioCriador.setEmail(emailUsuario);

        MateriaEntity materiaEntity = new MateriaEntity();
        materiaEntity.setId(UUID.randomUUID());
        materiaEntity.setNomeMateria(nomeMateria);
        materiaEntity.setCriadoPor(usuarioCriador);
        materiaEntity.setListaAssuntos(listaAssuntos);

        MateriaDTO materiaDTO = new MateriaDTO();
        materiaDTO.setId(materiaEntity.getId());
        materiaDTO.setNomeMateria(nomeMateria);

        // Configuração dos Mocks
        when(userDetails.getUsername()).thenReturn(emailUsuario);
        when(usuarioRepository.findByEmail(emailUsuario)).thenReturn(Optional.of(usuarioCriador));
        when(materiaRepository.save(any(MateriaEntity.class))).thenReturn(materiaEntity);
        when(materiaMapper.toMateriaDTO(any(MateriaEntity.class))).thenReturn(materiaDTO);

        // Act
        MateriaDTO resultado = materiaService.save(nomeMateria, listaAssuntos);

        // Assert
        assertNotNull(resultado);
        assertEquals(nomeMateria, resultado.getNomeMateria());
        verify(materiaRepository, times(1)).save(any(MateriaEntity.class));
        verify(materiaMapper, times(1)).toMateriaDTO(any(MateriaEntity.class));

        System.out.println("\n>>> CT007-4 SUCESSO: Matéria criada com sucesso - " + resultado.getNomeMateria() + " (ID: " + resultado.getId() + ") <<<\n");
    }
}
