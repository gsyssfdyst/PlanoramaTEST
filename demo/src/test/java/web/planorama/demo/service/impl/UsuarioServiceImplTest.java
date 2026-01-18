package web.planorama.demo.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.planorama.demo.dto.UsuarioDTO;
import web.planorama.demo.entity.PapelEntity;
import web.planorama.demo.entity.UsuarioEntity;
import web.planorama.demo.enums.PapeisUsuario;
import web.planorama.demo.exceptions.RecursoDuplicadoException;
import web.planorama.demo.mapping.UsuarioMapper;
import web.planorama.demo.repository.PapelRepository;
import web.planorama.demo.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

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

    @Test
    @DisplayName("Deve salvar usuário com sucesso sendo o PRIMEIRO registro (ADMIN + ESTUDANTE)")
    void deveSalvarPrimeiroUsuarioComoAdmin() {
        UsuarioDTO dtoEntrada = new UsuarioDTO(null, "Lizandra", "liz@email.com", "123", null, null);
        
        UsuarioEntity usuarioSalvoEntity = new UsuarioEntity();
        usuarioSalvoEntity.setId(java.util.UUID.randomUUID());
        usuarioSalvoEntity.setEmail("liz@email.com");
        usuarioSalvoEntity.setSenha("senhaCriptografada");

        UsuarioDTO dtoRetorno = new UsuarioDTO(usuarioSalvoEntity.getId(), "Lizandra", "liz@email.com", "senhaCriptografada", null, null);

        when(usuarioRepository.findByEmail(dtoEntrada.email())).thenReturn(Optional.empty());

        PapelEntity papelEstudante = new PapelEntity(); papelEstudante.setId(PapeisUsuario.ESTUDANTE.getId());
        PapelEntity papelAdmin = new PapelEntity(); papelAdmin.setId(PapeisUsuario.ADMIN.getId());
        
        when(papelRepository.findById(PapeisUsuario.ESTUDANTE.getId())).thenReturn(Optional.of(papelEstudante));
        when(papelRepository.findById(PapeisUsuario.ADMIN.getId())).thenReturn(Optional.of(papelAdmin));

        when(usuarioRepository.count()).thenReturn(0L);

        when(passwordEncoder.encode(dtoEntrada.senha())).thenReturn("senhaCriptografada");
        when(usuarioMapper.toUsuarioEntity(eq(dtoEntrada), anyList())).thenReturn(usuarioSalvoEntity);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioSalvoEntity);
        when(usuarioMapper.toUsuarioDTO(usuarioSalvoEntity)).thenReturn(dtoRetorno);

        UsuarioDTO resultado = usuarioService.save(dtoEntrada);

        assertNotNull(resultado);
        assertEquals(dtoRetorno.id(), resultado.id());
        
        verify(usuarioRepository, times(1)).count();
        verify(papelRepository, times(1)).findById(PapeisUsuario.ADMIN.getId());
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));

        System.out.println("\n>>> TESTE 1 SUCESSO: O primeiro usuário foi criado e virou ADMIN automaticamente! <<<" + resultado);
    }

    @Test
    @DisplayName("Deve salvar usuário com sucesso NÃO sendo o primeiro (Apenas ESTUDANTE)")
    void deveSalvarUsuarioComum() {
        UsuarioDTO dtoEntrada = new UsuarioDTO(null, "Usuario Comum", "user@email.com", "123", null, null);
        UsuarioEntity usuarioEntity = new UsuarioEntity(); 
        UsuarioDTO dtoRetorno = new UsuarioDTO(java.util.UUID.randomUUID(), "Usuario Comum", "user@email.com", "xxx", null, null);

        when(usuarioRepository.findByEmail(dtoEntrada.email())).thenReturn(Optional.empty());
        
        PapelEntity papelEstudante = new PapelEntity();
        when(papelRepository.findById(PapeisUsuario.ESTUDANTE.getId())).thenReturn(Optional.of(papelEstudante));

        when(usuarioRepository.count()).thenReturn(5L);

        when(passwordEncoder.encode(anyString())).thenReturn("xxx");
        when(usuarioMapper.toUsuarioEntity(any(), anyList())).thenReturn(usuarioEntity);
        when(usuarioRepository.save(any())).thenReturn(usuarioEntity);
        when(usuarioMapper.toUsuarioDTO(any())).thenReturn(dtoRetorno);

        usuarioService.save(dtoEntrada);

        verify(papelRepository, never()).findById(PapeisUsuario.ADMIN.getId());
        verify(usuarioRepository, times(1)).save(any());

        System.out.println("\n>>> TESTE 2 SUCESSO: Usuário comum criado (apenas permissão de estudante)! <<<");
    }

    @Test
    @DisplayName("Deve lançar RecursoDuplicadoException ao tentar cadastrar e-mail existente")
    void deveLancarExcecaoEmailDuplicado() {
        UsuarioDTO dtoEntrada = new UsuarioDTO(null, "Liz", "duplicado@email.com", "123", null, null);
        UsuarioEntity usuarioExistente = new UsuarioEntity();
        usuarioExistente.setId(java.util.UUID.randomUUID());

        when(usuarioRepository.findByEmail(dtoEntrada.email())).thenReturn(Optional.of(usuarioExistente));

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class, () -> {
            usuarioService.save(dtoEntrada);
        });

        assertEquals("E-mail duplicado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());

        System.out.println("\n>>> TESTE 3 SUCESSO: O sistema bloqueou corretamente um e-mail duplicado! <<<");
    }

    // ------------------- TESTES CT005 - Atualização dos dados do usuário -------------------

    private SecurityContext securityContext;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setupSecurityContext() {
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        userDetails = mock(UserDetails.class);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDownSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("CT005-Service: alterarEmail com senha correta atualiza e salva usuário")
    void ct005_alterarEmail_sucesso() {
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");
        estudante.setSenha("senhaHashed");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaAtual", "senhaHashed")).thenReturn(true);
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.alterarEmail("novo@teste.com", "senhaAtual");

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioRepository, times(1)).save(captor.capture());
        assertEquals("novo@teste.com", captor.getValue().getEmail());

        System.out.println("\n>>> CT005 Serviço: alterarEmail SUCESSO (email atualizado e salvo). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarEmail com senha incorreta lança RuntimeException")
    void ct005_alterarEmail_senhaIncorreta() {
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");
        estudante.setSenha("senhaHashed");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaErrada", "senhaHashed")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.alterarEmail("novo@teste.com", "senhaErrada"));
        assertEquals("A senha atual está incorreta", ex.getMessage());

        System.out.println("\n>>> CT005 Serviço: alterarEmail FALHA - senha incorreta (exceção lançada). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarSenha com senha correta altera e salva a senha")
    void ct005_alterarSenha_sucesso() {
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");
        estudante.setSenha("senhaHashed");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaAtual", "senhaHashed")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaHashed");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.alterarSenha("senhaAtual", "novaSenha123");

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioRepository, times(1)).save(captor.capture());
        assertEquals("novaHashed", captor.getValue().getSenha());

        System.out.println("\n>>> CT005 Serviço: alterarSenha SUCESSO (senha atualizada e salva). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarSenha com senha atual incorreta lança RuntimeException")
    void ct005_alterarSenha_senhaIncorreta() {
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");
        estudante.setSenha("senhaHashed");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaErrada", "senhaHashed")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.alterarSenha("senhaErrada", "novaSenha"));
        assertEquals("A senha atual está incorreta", ex.getMessage());

        System.out.println("\n>>> CT005 Serviço: alterarSenha FALHA - senha incorreta (exceção lançada). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarNomeUsuario com senha correta altera e salva o nome")
    void ct005_alterarNomeUsuario_sucesso() {
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");
        estudante.setSenha("senhaHashed");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaAtual", "senhaHashed")).thenReturn(true);
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.alterarNomeUsuario("NovoNome", "senhaAtual");

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioRepository, times(1)).save(captor.capture());
        assertEquals("NovoNome", captor.getValue().getNome());

        System.out.println("\n>>> CT005 Serviço: alterarNomeUsuario SUCESSO (nome atualizado e salvo). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarNomeUsuario com senha incorreta lança RuntimeException")
    void ct005_alterarNomeUsuario_senhaIncorreta() {
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");
        estudante.setSenha("senhaHashed");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaErrada", "senhaHashed")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.alterarNomeUsuario("NovoNome", "senhaErrada"));
        assertEquals("A senha atual está incorreta", ex.getMessage());

        System.out.println("\n>>> CT005 Serviço: alterarNomeUsuario FALHA - senha incorreta (exceção lançada). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarFotoUsuario com email existente altera e salva a foto")
    void ct005_alterarFotoUsuario_sucesso() {
        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.alterarFotoUsuario("usuario@teste.com", "/uploadsUser/foto.png");

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioRepository, times(1)).save(captor.capture());
        assertEquals("/uploadsUser/foto.png", captor.getValue().getFotoUsuario());

        System.out.println("\n>>> CT005 Serviço: alterarFotoUsuario SUCESSO (foto atualizada e salva). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarFotoUsuario com usuário não encontrado lança RuntimeException")
    void ct005_alterarFotoUsuario_naoEncontrado() {
        when(usuarioRepository.findByEmail("naoexistente@teste.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.alterarFotoUsuario("naoexistente@teste.com", "/uploadsUser/foto.png"));
        assertEquals("Usuário não encontrado no banco de dados", ex.getMessage());

        System.out.println("\n>>> CT005 Serviço: alterarFotoUsuario FALHA - usuário não encontrado (exceção lançada). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarDescricao com senha correta altera e salva a descrição")
    void ct005_alterarDescricao_sucesso() {
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");
        estudante.setSenha("senhaHashed");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaAtual", "senhaHashed")).thenReturn(true);
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.alterarDescricao("Nova descrição", "senhaAtual");

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(usuarioRepository, times(1)).save(captor.capture());
        assertEquals("Nova descrição", captor.getValue().getDescricaoUsuario());

        System.out.println("\n>>> CT005 Serviço: alterarDescricao SUCESSO (descrição atualizada e salva). <<<\n");
    }

    @Test
    @DisplayName("CT005-Service: alterarDescricao com senha incorreta lança RuntimeException")
    void ct005_alterarDescricao_senhaIncorreta() {
        when(userDetails.getUsername()).thenReturn("usuario@teste.com");

        UsuarioEntity estudante = new UsuarioEntity();
        estudante.setEmail("usuario@teste.com");
        estudante.setSenha("senhaHashed");

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaErrada", "senhaHashed")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.alterarDescricao("Nova descrição", "senhaErrada"));
        assertEquals("A senha atual está incorreta", ex.getMessage());

        System.out.println("\n>>> CT005 Serviço: alterarDescricao FALHA - senha incorreta (exceção lançada). <<<\n");
    }
}
