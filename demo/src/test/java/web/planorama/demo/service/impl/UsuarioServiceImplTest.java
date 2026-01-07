package web.planorama.demo.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
}