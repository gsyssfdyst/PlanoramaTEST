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
import web.planorama.demo.dto.RegistrarEstudoDTO;
import web.planorama.demo.entity.*;
import web.planorama.demo.mapping.RegistrarEstudoMapper;
import web.planorama.demo.repository.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarEstudoServiceImplTest {

    @Mock private RegistrarEstudoRepository registrarEstudoRepository;
    @Mock private AssuntoRepository assuntoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MateriaPlanejamentoRepository materiaPlanejamentoRepository;
    @Mock private SessaoEstudoRepository sessaoEstudoRepository;
    @Mock private RegistrarEstudoMapper registrarEstudoMapper;

    @InjectMocks
    private RegistrarEstudoServiceImpl registrarEstudoService;

    // Mocks para simular o Usuário Logado (SecurityContext)
    private SecurityContext securityContext;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        userDetails = mock(UserDetails.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("CT004 - Service: Deve salvar registro com sucesso")
    void deveSalvarRegistroComSucesso() {
        // --- ARRANGE ---
        UUID idMateriaPlan = UUID.randomUUID();
        UUID idAssunto = UUID.randomUUID();
        String emailUsuario = "liz@teste.com";

        RegistrarEstudoDTO dto = new RegistrarEstudoDTO();
        dto.setIdMateriaPlanejamento(idMateriaPlan);
        dto.setAssuntoId(idAssunto);
        dto.setHorasEstudadas(2);
        dto.setMinutosEstudados(30);

        MateriaPlanejamentoEntity materiaPlan = new MateriaPlanejamentoEntity();
        materiaPlan.setRegistrosDeEstudo(new ArrayList<>());
        RegistrarEstudoEntity registroSalvo = new RegistrarEstudoEntity();
        registroSalvo.setId(UUID.randomUUID());

        // Configuração dos Mocks
        when(materiaPlanejamentoRepository.findById(idMateriaPlan)).thenReturn(Optional.of(materiaPlan));
        when(assuntoRepository.findById(idAssunto)).thenReturn(Optional.of(new AssuntoEntity()));
        when(userDetails.getUsername()).thenReturn(emailUsuario);
        when(usuarioRepository.findByEmail(emailUsuario)).thenReturn(Optional.of(new UsuarioEntity()));
        when(registrarEstudoRepository.save(any())).thenReturn(registroSalvo);
        when(registrarEstudoMapper.toRegistrarEstudoDTO(registroSalvo)).thenReturn(dto);
        when(sessaoEstudoRepository.findByMateriaPlanejamento(materiaPlan)).thenReturn(new ArrayList<>());

        // --- ACT ---
        RegistrarEstudoDTO resultado = registrarEstudoService.save(dto);

        // --- ASSERT ---
        assertNotNull(resultado);
        verify(registrarEstudoRepository, times(1)).save(any());
        
        System.out.println("\n>>> SERVICE SUCESSO: Cadastro do Registro de Estudos foi um sucesso. Objeto criado: " + resultado + " <<<\n");
    }
}