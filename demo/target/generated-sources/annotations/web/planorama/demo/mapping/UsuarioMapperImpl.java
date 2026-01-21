package web.planorama.demo.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import web.planorama.demo.dto.UsuarioDTO;
import web.planorama.demo.entity.PapelEntity;
import web.planorama.demo.entity.UsuarioEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T18:19:09-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public UsuarioDTO toUsuarioDTO(UsuarioEntity usuarioEntity) {
        if ( usuarioEntity == null ) {
            return null;
        }

        UUID id = null;
        String nome = null;
        String email = null;
        String senha = null;
        String fotoUsuario = null;
        String descricaoUsuario = null;

        id = usuarioEntity.getId();
        nome = usuarioEntity.getNome();
        email = usuarioEntity.getEmail();
        senha = usuarioEntity.getSenha();
        fotoUsuario = usuarioEntity.getFotoUsuario();
        descricaoUsuario = usuarioEntity.getDescricaoUsuario();

        UsuarioDTO usuarioDTO = new UsuarioDTO( id, nome, email, senha, fotoUsuario, descricaoUsuario );

        return usuarioDTO;
    }

    @Override
    public UsuarioEntity toUsuarioEntity(UsuarioDTO UsuarioDTO, List<PapelEntity> papeis) {
        if ( UsuarioDTO == null && papeis == null ) {
            return null;
        }

        UsuarioEntity usuarioEntity = new UsuarioEntity();

        if ( UsuarioDTO != null ) {
            usuarioEntity.setDescricaoUsuario( UsuarioDTO.descricaoUsuario() );
            usuarioEntity.setEmail( UsuarioDTO.email() );
            usuarioEntity.setFotoUsuario( UsuarioDTO.fotoUsuario() );
            usuarioEntity.setId( UsuarioDTO.id() );
            usuarioEntity.setNome( UsuarioDTO.nome() );
            usuarioEntity.setSenha( UsuarioDTO.senha() );
        }
        List<PapelEntity> list = papeis;
        if ( list != null ) {
            usuarioEntity.setPapeis( new ArrayList<PapelEntity>( list ) );
        }

        return usuarioEntity;
    }
}
