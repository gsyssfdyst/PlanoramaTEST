package web.planorama.demo.mapping;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.planorama.demo.dto.MateriaPlanejamentoDTO;
import web.planorama.demo.dto.PlanejamentoDTO;
import web.planorama.demo.dto.SessaoEstudoDTO;
import web.planorama.demo.dto.UsuarioDTO;
import web.planorama.demo.entity.MateriaPlanejamentoEntity;
import web.planorama.demo.entity.PlanejamentoEntity;
import web.planorama.demo.entity.SessaoEstudoEntity;
import web.planorama.demo.entity.UsuarioEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T18:19:09-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class PlanejamentoMapperImpl implements PlanejamentoMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Override
    public PlanejamentoEntity toPlanejamentoEntity(PlanejamentoDTO planejamentoDTO) {
        if ( planejamentoDTO == null ) {
            return null;
        }

        PlanejamentoEntity planejamentoEntity = new PlanejamentoEntity();

        planejamentoEntity.setAnoAplicacao( planejamentoDTO.getAnoAplicacao() );
        planejamentoEntity.setCargo( planejamentoDTO.getCargo() );
        planejamentoEntity.setCriador( usuarioDTOToUsuarioEntity( planejamentoDTO.getCriador() ) );
        List<String> list = planejamentoDTO.getDisponibilidade();
        if ( list != null ) {
            planejamentoEntity.setDisponibilidade( new ArrayList<String>( list ) );
        }
        planejamentoEntity.setHorasDiarias( planejamentoDTO.getHorasDiarias() );
        planejamentoEntity.setId( planejamentoDTO.getId() );
        planejamentoEntity.setMaterias( materiaPlanejamentoDTOListToMateriaPlanejamentoEntityList( planejamentoDTO.getMaterias() ) );
        planejamentoEntity.setNomePlanejamento( planejamentoDTO.getNomePlanejamento() );
        planejamentoEntity.setPlanoArquivado( planejamentoDTO.isPlanoArquivado() );
        planejamentoEntity.setPreDefinidoAdm( planejamentoDTO.isPreDefinidoAdm() );
        planejamentoEntity.setSessoesEstudo( sessaoEstudoDTOListToSessaoEstudoEntityList( planejamentoDTO.getSessoesEstudo() ) );

        return planejamentoEntity;
    }

    @Override
    public PlanejamentoDTO toPlanejamentoDTO(PlanejamentoEntity planejamentoEntity) {
        if ( planejamentoEntity == null ) {
            return null;
        }

        PlanejamentoDTO planejamentoDTO = new PlanejamentoDTO();

        planejamentoDTO.setAnoAplicacao( planejamentoEntity.getAnoAplicacao() );
        planejamentoDTO.setCargo( planejamentoEntity.getCargo() );
        planejamentoDTO.setCriador( usuarioMapper.toUsuarioDTO( planejamentoEntity.getCriador() ) );
        List<String> list = planejamentoEntity.getDisponibilidade();
        if ( list != null ) {
            planejamentoDTO.setDisponibilidade( new ArrayList<String>( list ) );
        }
        planejamentoDTO.setHorasDiarias( planejamentoEntity.getHorasDiarias() );
        planejamentoDTO.setId( planejamentoEntity.getId() );
        planejamentoDTO.setMaterias( materiaPlanejamentoEntityListToMateriaPlanejamentoDTOList( planejamentoEntity.getMaterias() ) );
        planejamentoDTO.setNomePlanejamento( planejamentoEntity.getNomePlanejamento() );
        planejamentoDTO.setPlanoArquivado( planejamentoEntity.isPlanoArquivado() );
        planejamentoDTO.setPreDefinidoAdm( planejamentoEntity.isPreDefinidoAdm() );
        planejamentoDTO.setSessoesEstudo( sessaoEstudoEntityListToSessaoEstudoDTOList( planejamentoEntity.getSessoesEstudo() ) );

        return planejamentoDTO;
    }

    protected UsuarioEntity usuarioDTOToUsuarioEntity(UsuarioDTO usuarioDTO) {
        if ( usuarioDTO == null ) {
            return null;
        }

        UsuarioEntity usuarioEntity = new UsuarioEntity();

        usuarioEntity.setDescricaoUsuario( usuarioDTO.descricaoUsuario() );
        usuarioEntity.setEmail( usuarioDTO.email() );
        usuarioEntity.setFotoUsuario( usuarioDTO.fotoUsuario() );
        usuarioEntity.setId( usuarioDTO.id() );
        usuarioEntity.setNome( usuarioDTO.nome() );
        usuarioEntity.setSenha( usuarioDTO.senha() );

        return usuarioEntity;
    }

    protected MateriaPlanejamentoEntity materiaPlanejamentoDTOToMateriaPlanejamentoEntity(MateriaPlanejamentoDTO materiaPlanejamentoDTO) {
        if ( materiaPlanejamentoDTO == null ) {
            return null;
        }

        MateriaPlanejamentoEntity materiaPlanejamentoEntity = new MateriaPlanejamentoEntity();

        if ( materiaPlanejamentoDTO.getCargaHorariaMateriaPlano() != null ) {
            materiaPlanejamentoEntity.setCargaHorariaMateriaPlano( materiaPlanejamentoDTO.getCargaHorariaMateriaPlano() );
        }
        materiaPlanejamentoEntity.setId( materiaPlanejamentoDTO.getId() );
        if ( materiaPlanejamentoDTO.getNivelConhecimento() != null ) {
            materiaPlanejamentoEntity.setNivelConhecimento( materiaPlanejamentoDTO.getNivelConhecimento() );
        }

        return materiaPlanejamentoEntity;
    }

    protected List<MateriaPlanejamentoEntity> materiaPlanejamentoDTOListToMateriaPlanejamentoEntityList(List<MateriaPlanejamentoDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<MateriaPlanejamentoEntity> list1 = new ArrayList<MateriaPlanejamentoEntity>( list.size() );
        for ( MateriaPlanejamentoDTO materiaPlanejamentoDTO : list ) {
            list1.add( materiaPlanejamentoDTOToMateriaPlanejamentoEntity( materiaPlanejamentoDTO ) );
        }

        return list1;
    }

    protected SessaoEstudoEntity sessaoEstudoDTOToSessaoEstudoEntity(SessaoEstudoDTO sessaoEstudoDTO) {
        if ( sessaoEstudoDTO == null ) {
            return null;
        }

        SessaoEstudoEntity sessaoEstudoEntity = new SessaoEstudoEntity();

        sessaoEstudoEntity.setDuracaoSessao( sessaoEstudoDTO.getDuracaoSessao() );
        sessaoEstudoEntity.setId( sessaoEstudoDTO.getId() );

        return sessaoEstudoEntity;
    }

    protected List<SessaoEstudoEntity> sessaoEstudoDTOListToSessaoEstudoEntityList(List<SessaoEstudoDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<SessaoEstudoEntity> list1 = new ArrayList<SessaoEstudoEntity>( list.size() );
        for ( SessaoEstudoDTO sessaoEstudoDTO : list ) {
            list1.add( sessaoEstudoDTOToSessaoEstudoEntity( sessaoEstudoDTO ) );
        }

        return list1;
    }

    protected MateriaPlanejamentoDTO materiaPlanejamentoEntityToMateriaPlanejamentoDTO(MateriaPlanejamentoEntity materiaPlanejamentoEntity) {
        if ( materiaPlanejamentoEntity == null ) {
            return null;
        }

        MateriaPlanejamentoDTO materiaPlanejamentoDTO = new MateriaPlanejamentoDTO();

        materiaPlanejamentoDTO.setCargaHorariaMateriaPlano( materiaPlanejamentoEntity.getCargaHorariaMateriaPlano() );
        materiaPlanejamentoDTO.setId( materiaPlanejamentoEntity.getId() );
        materiaPlanejamentoDTO.setNivelConhecimento( materiaPlanejamentoEntity.getNivelConhecimento() );

        return materiaPlanejamentoDTO;
    }

    protected List<MateriaPlanejamentoDTO> materiaPlanejamentoEntityListToMateriaPlanejamentoDTOList(List<MateriaPlanejamentoEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<MateriaPlanejamentoDTO> list1 = new ArrayList<MateriaPlanejamentoDTO>( list.size() );
        for ( MateriaPlanejamentoEntity materiaPlanejamentoEntity : list ) {
            list1.add( materiaPlanejamentoEntityToMateriaPlanejamentoDTO( materiaPlanejamentoEntity ) );
        }

        return list1;
    }

    protected SessaoEstudoDTO sessaoEstudoEntityToSessaoEstudoDTO(SessaoEstudoEntity sessaoEstudoEntity) {
        if ( sessaoEstudoEntity == null ) {
            return null;
        }

        SessaoEstudoDTO sessaoEstudoDTO = new SessaoEstudoDTO();

        sessaoEstudoDTO.setDuracaoSessao( sessaoEstudoEntity.getDuracaoSessao() );
        sessaoEstudoDTO.setId( sessaoEstudoEntity.getId() );

        return sessaoEstudoDTO;
    }

    protected List<SessaoEstudoDTO> sessaoEstudoEntityListToSessaoEstudoDTOList(List<SessaoEstudoEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<SessaoEstudoDTO> list1 = new ArrayList<SessaoEstudoDTO>( list.size() );
        for ( SessaoEstudoEntity sessaoEstudoEntity : list ) {
            list1.add( sessaoEstudoEntityToSessaoEstudoDTO( sessaoEstudoEntity ) );
        }

        return list1;
    }
}
