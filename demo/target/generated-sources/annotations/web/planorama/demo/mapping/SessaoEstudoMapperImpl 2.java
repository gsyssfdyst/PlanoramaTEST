package web.planorama.demo.mapping;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.planorama.demo.dto.SessaoEstudoDTO;
import web.planorama.demo.entity.MateriaEntity;
import web.planorama.demo.entity.MateriaPlanejamentoEntity;
import web.planorama.demo.entity.SessaoEstudoEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T18:19:02-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class SessaoEstudoMapperImpl implements SessaoEstudoMapper {

    @Autowired
    private MateriaMapper materiaMapper;

    @Override
    public SessaoEstudoDTO toSessaoEstudoDTO(SessaoEstudoEntity sessaoEstudoEntity) {
        if ( sessaoEstudoEntity == null ) {
            return null;
        }

        SessaoEstudoDTO sessaoEstudoDTO = new SessaoEstudoDTO();

        sessaoEstudoDTO.setIdMateriaPlanejamento( sessaoEstudoEntityMateriaPlanejamentoId( sessaoEstudoEntity ) );
        sessaoEstudoDTO.setMateriaDTO( materiaMapper.toMateriaDTO( sessaoEstudoEntityMateriaPlanejamentoMateriaEntity( sessaoEstudoEntity ) ) );
        sessaoEstudoDTO.setDuracaoSessao( sessaoEstudoEntity.getDuracaoSessao() );
        sessaoEstudoDTO.setId( sessaoEstudoEntity.getId() );

        return sessaoEstudoDTO;
    }

    @Override
    public SessaoEstudoEntity toSessaoEstudoEntity(SessaoEstudoDTO sessaoEstudoDTO) {
        if ( sessaoEstudoDTO == null ) {
            return null;
        }

        SessaoEstudoEntity sessaoEstudoEntity = new SessaoEstudoEntity();

        sessaoEstudoEntity.setMateriaPlanejamento( sessaoEstudoDTOToMateriaPlanejamentoEntity( sessaoEstudoDTO ) );
        sessaoEstudoEntity.setDuracaoSessao( sessaoEstudoDTO.getDuracaoSessao() );
        sessaoEstudoEntity.setId( sessaoEstudoDTO.getId() );

        return sessaoEstudoEntity;
    }

    private UUID sessaoEstudoEntityMateriaPlanejamentoId(SessaoEstudoEntity sessaoEstudoEntity) {
        MateriaPlanejamentoEntity materiaPlanejamento = sessaoEstudoEntity.getMateriaPlanejamento();
        if ( materiaPlanejamento == null ) {
            return null;
        }
        return materiaPlanejamento.getId();
    }

    private MateriaEntity sessaoEstudoEntityMateriaPlanejamentoMateriaEntity(SessaoEstudoEntity sessaoEstudoEntity) {
        MateriaPlanejamentoEntity materiaPlanejamento = sessaoEstudoEntity.getMateriaPlanejamento();
        if ( materiaPlanejamento == null ) {
            return null;
        }
        return materiaPlanejamento.getMateriaEntity();
    }

    protected MateriaPlanejamentoEntity sessaoEstudoDTOToMateriaPlanejamentoEntity(SessaoEstudoDTO sessaoEstudoDTO) {
        if ( sessaoEstudoDTO == null ) {
            return null;
        }

        MateriaPlanejamentoEntity materiaPlanejamentoEntity = new MateriaPlanejamentoEntity();

        materiaPlanejamentoEntity.setId( sessaoEstudoDTO.getIdMateriaPlanejamento() );
        materiaPlanejamentoEntity.setMateriaEntity( materiaMapper.toMateriaEntity( sessaoEstudoDTO.getMateriaDTO() ) );

        return materiaPlanejamentoEntity;
    }
}
