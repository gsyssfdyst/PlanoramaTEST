package web.planorama.demo.mapping;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import web.planorama.demo.dto.RegistrarEstudoDTO;
import web.planorama.demo.entity.AssuntoEntity;
import web.planorama.demo.entity.MateriaPlanejamentoEntity;
import web.planorama.demo.entity.PlanejamentoEntity;
import web.planorama.demo.entity.RegistrarEstudoEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-20T11:58:32-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Homebrew)"
)
@Component
public class RegistrarEstudoMapperImpl implements RegistrarEstudoMapper {

    @Override
    public RegistrarEstudoDTO toRegistrarEstudoDTO(RegistrarEstudoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RegistrarEstudoDTO registrarEstudoDTO = new RegistrarEstudoDTO();

        registrarEstudoDTO.setAssuntoId( entityAssuntoId( entity ) );
        registrarEstudoDTO.setPlanejamentoId( entityMateriaPlanejamentoPlanejamentoEntityId( entity ) );
        registrarEstudoDTO.setIdMateriaPlanejamento( entityMateriaPlanejamentoId( entity ) );
        registrarEstudoDTO.setId( entity.getId() );

        return registrarEstudoDTO;
    }

    @Override
    public RegistrarEstudoEntity toRegistrarEstudoEntity(RegistrarEstudoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        RegistrarEstudoEntity registrarEstudoEntity = new RegistrarEstudoEntity();

        registrarEstudoEntity.setAssunto( registrarEstudoDTOToAssuntoEntity( dto ) );
        registrarEstudoEntity.setMateriaPlanejamento( registrarEstudoDTOToMateriaPlanejamentoEntity( dto ) );
        registrarEstudoEntity.setId( dto.getId() );

        return registrarEstudoEntity;
    }

    private UUID entityAssuntoId(RegistrarEstudoEntity registrarEstudoEntity) {
        AssuntoEntity assunto = registrarEstudoEntity.getAssunto();
        if ( assunto == null ) {
            return null;
        }
        return assunto.getId();
    }

    private UUID entityMateriaPlanejamentoPlanejamentoEntityId(RegistrarEstudoEntity registrarEstudoEntity) {
        MateriaPlanejamentoEntity materiaPlanejamento = registrarEstudoEntity.getMateriaPlanejamento();
        if ( materiaPlanejamento == null ) {
            return null;
        }
        PlanejamentoEntity planejamentoEntity = materiaPlanejamento.getPlanejamentoEntity();
        if ( planejamentoEntity == null ) {
            return null;
        }
        return planejamentoEntity.getId();
    }

    private UUID entityMateriaPlanejamentoId(RegistrarEstudoEntity registrarEstudoEntity) {
        MateriaPlanejamentoEntity materiaPlanejamento = registrarEstudoEntity.getMateriaPlanejamento();
        if ( materiaPlanejamento == null ) {
            return null;
        }
        return materiaPlanejamento.getId();
    }

    protected AssuntoEntity registrarEstudoDTOToAssuntoEntity(RegistrarEstudoDTO registrarEstudoDTO) {
        if ( registrarEstudoDTO == null ) {
            return null;
        }

        AssuntoEntity assuntoEntity = new AssuntoEntity();

        assuntoEntity.setId( registrarEstudoDTO.getAssuntoId() );

        return assuntoEntity;
    }

    protected MateriaPlanejamentoEntity registrarEstudoDTOToMateriaPlanejamentoEntity(RegistrarEstudoDTO registrarEstudoDTO) {
        if ( registrarEstudoDTO == null ) {
            return null;
        }

        MateriaPlanejamentoEntity materiaPlanejamentoEntity = new MateriaPlanejamentoEntity();

        materiaPlanejamentoEntity.setId( registrarEstudoDTO.getIdMateriaPlanejamento() );

        return materiaPlanejamentoEntity;
    }
}
