package web.planorama.demo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import web.planorama.demo.dto.PlanejamentoDTO;
import web.planorama.demo.entity.PlanejamentoEntity;
import web.planorama.demo.exceptions.MyNotFoundException;
import web.planorama.demo.mapping.MateriaPlanejamentoMapper;
import web.planorama.demo.mapping.PlanejamentoMapper;
import web.planorama.demo.mapping.SessaoEstudoMapper;
import web.planorama.demo.repository.MateriaRepository;
import web.planorama.demo.repository.PlanejamentoRepository;
import web.planorama.demo.repository.RegistrarEstudoRepository;
import web.planorama.demo.repository.SessaoEstudoRepository;
import web.planorama.demo.repository.UsuarioRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanejamentoServiceImplCT010Test {

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
    @DisplayName("CT010-1: Arquivar planejamento inexistente deve lançar MyNotFoundException")
    void arquivarPlanejamentoInexistente() {
        UUID planejamentoId = UUID.randomUUID();
        PlanejamentoDTO dto = new PlanejamentoDTO();
        dto.setId(planejamentoId);

        when(planejamentoRepository.findById(planejamentoId)).thenReturn(Optional.empty());

        MyNotFoundException exception = assertThrows(MyNotFoundException.class,
                () -> planejamentoService.arquivarPlanoDeEstudos(dto));

        assertTrue(exception.getMessage().contains(planejamentoId.toString()));
        verify(planejamentoRepository, times(1)).findById(planejamentoId);
        verify(planejamentoRepository, never()).save(any());
        verify(mapper, never()).toPlanejamentoDTO(any());

        System.out.println("\n>>> CT010-1 SUCESSO: Mensagem informada quando não existe planejamento para arquivar. <<<\n");
    }

    @Test
    @DisplayName("CT010-2: Arquivar planejamento existente deve marcar planoArquivado como true e retornar DTO")
    void arquivarPlanejamentoExistente() {
        UUID planejamentoId = UUID.randomUUID();
        PlanejamentoDTO entrada = new PlanejamentoDTO();
        entrada.setId(planejamentoId);

        PlanejamentoEntity encontrado = new PlanejamentoEntity();
        encontrado.setId(planejamentoId);
        encontrado.setPlanoArquivado(false);

        PlanejamentoEntity salvo = new PlanejamentoEntity();
        salvo.setId(planejamentoId);
        salvo.setPlanoArquivado(true);

        PlanejamentoDTO esperado = new PlanejamentoDTO();
        esperado.setId(planejamentoId);
        esperado.setPlanoArquivado(true);

        when(planejamentoRepository.findById(planejamentoId)).thenReturn(Optional.of(encontrado));
        when(planejamentoRepository.save(any(PlanejamentoEntity.class))).thenReturn(salvo);
        when(mapper.toPlanejamentoDTO(salvo)).thenReturn(esperado);

        PlanejamentoDTO resultado = planejamentoService.arquivarPlanoDeEstudos(entrada);

        ArgumentCaptor<PlanejamentoEntity> captor = ArgumentCaptor.forClass(PlanejamentoEntity.class);
        verify(planejamentoRepository).save(captor.capture());
        assertTrue(captor.getValue().isPlanoArquivado());
        assertNotNull(resultado);
        assertEquals(planejamentoId, resultado.getId());
        assertTrue(resultado.isPlanoArquivado());

        verify(mapper, times(1)).toPlanejamentoDTO(salvo);

        System.out.println("\n>>> CT010-2 SUCESSO: Planejamento arquivado e DTO retornado com planoArquivado=true. <<<\n");
    }

    @Test
    @DisplayName("CT010-3: Desarquivar planejamento inexistente deve lançar MyNotFoundException")
    void desarquivarPlanejamentoInexistente() {
        UUID planejamentoId = UUID.randomUUID();
        PlanejamentoDTO dto = new PlanejamentoDTO();
        dto.setId(planejamentoId);

        when(planejamentoRepository.findById(planejamentoId)).thenReturn(Optional.empty());

        MyNotFoundException exception = assertThrows(MyNotFoundException.class,
                () -> planejamentoService.desarquivarPlanoDeEstudos(dto));

        assertTrue(exception.getMessage().contains(planejamentoId.toString()));
        verify(planejamentoRepository, times(1)).findById(planejamentoId);
        verify(planejamentoRepository, never()).save(any());
        verify(mapper, never()).toPlanejamentoDTO(any());

        System.out.println("\n>>> CT010-3 SUCESSO: Mensagem informada quando não existe planejamento para desarquivar. <<<\n");
    }

    @Test
    @DisplayName("CT010-4: Desarquivar planejamento existente deve marcar planoArquivado como false e retornar DTO")
    void desarquivarPlanejamentoExistente() {
        UUID planejamentoId = UUID.randomUUID();
        PlanejamentoDTO entrada = new PlanejamentoDTO();
        entrada.setId(planejamentoId);

        PlanejamentoEntity encontrado = new PlanejamentoEntity();
        encontrado.setId(planejamentoId);
        encontrado.setPlanoArquivado(true);

        PlanejamentoEntity salvo = new PlanejamentoEntity();
        salvo.setId(planejamentoId);
        salvo.setPlanoArquivado(false);

        PlanejamentoDTO esperado = new PlanejamentoDTO();
        esperado.setId(planejamentoId);
        esperado.setPlanoArquivado(false);

        when(planejamentoRepository.findById(planejamentoId)).thenReturn(Optional.of(encontrado));
        when(planejamentoRepository.save(any(PlanejamentoEntity.class))).thenReturn(salvo);
        when(mapper.toPlanejamentoDTO(salvo)).thenReturn(esperado);

        PlanejamentoDTO resultado = planejamentoService.desarquivarPlanoDeEstudos(entrada);

        ArgumentCaptor<PlanejamentoEntity> captor = ArgumentCaptor.forClass(PlanejamentoEntity.class);
        verify(planejamentoRepository).save(captor.capture());
        assertFalse(captor.getValue().isPlanoArquivado());
        assertNotNull(resultado);
        assertEquals(planejamentoId, resultado.getId());
        assertFalse(resultado.isPlanoArquivado());

        verify(mapper, times(1)).toPlanejamentoDTO(salvo);

        System.out.println("\n>>> CT010-4 SUCESSO: Planejamento desarquivado e DTO retornado com planoArquivado=false. <<<\n");
    }
}
