package web.planorama.demo.mapping;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.planorama.demo.dto.MateriaPlanejamentoDTO;
import web.planorama.demo.dto.PlanejamentoDTO;
import web.planorama.demo.dto.SessaoEstudoDTO;
import web.planorama.demo.entity.MateriaPlanejamentoEntity;
import web.planorama.demo.entity.PlanejamentoEntity;
import web.planorama.demo.entity.SessaoEstudoEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-07T21:57:20+0000",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class PlanejamentoMapperImpl implements PlanejamentoMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Override
    public PlanejamentoEntity toPlanejamentoEntity(PlanejamentoEntity planejamentoDTO) {
        if ( planejamentoDTO == null ) {
            return null;
        }

        PlanejamentoEntity planejamentoEntity = new PlanejamentoEntity();

        planejamentoEntity.setAnoAplicacao( planejamentoDTO.getAnoAplicacao() );
        planejamentoEntity.setCargo( planejamentoDTO.getCargo() );
        planejamentoEntity.setCriador( planejamentoDTO.getCriador() );
        List<String> list = planejamentoDTO.getDisponibilidade();
        if ( list != null ) {
            planejamentoEntity.setDisponibilidade( new ArrayList<String>( list ) );
        }
        planejamentoEntity.setHorasDiarias( planejamentoDTO.getHorasDiarias() );
        planejamentoEntity.setId( planejamentoDTO.getId() );
        List<MateriaPlanejamentoEntity> list1 = planejamentoDTO.getMaterias();
        if ( list1 != null ) {
            planejamentoEntity.setMaterias( new ArrayList<MateriaPlanejamentoEntity>( list1 ) );
        }
        planejamentoEntity.setNomePlanejamento( planejamentoDTO.getNomePlanejamento() );
        planejamentoEntity.setPlanoArquivado( planejamentoDTO.isPlanoArquivado() );
        planejamentoEntity.setPreDefinidoAdm( planejamentoDTO.isPreDefinidoAdm() );
        List<SessaoEstudoEntity> list2 = planejamentoDTO.getSessoesEstudo();
        if ( list2 != null ) {
            planejamentoEntity.setSessoesEstudo( new ArrayList<SessaoEstudoEntity>( list2 ) );
        }

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
