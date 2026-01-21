package web.planorama.demo.mapping;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import web.planorama.demo.dto.AssuntoDTO;
import web.planorama.demo.entity.AssuntoEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T18:19:09-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class AssuntoMapperImpl implements AssuntoMapper {

    @Override
    public AssuntoEntity toAssuntoEntity(AssuntoDTO assuntoDTO) {
        if ( assuntoDTO == null ) {
            return null;
        }

        AssuntoEntity assuntoEntity = new AssuntoEntity();

        assuntoEntity.setId( assuntoDTO.getId() );
        assuntoEntity.setNomeAssunto( assuntoDTO.getNomeAssunto() );

        return assuntoEntity;
    }

    @Override
    public AssuntoDTO toAssuntoDTO(AssuntoEntity assuntoEntity) {
        if ( assuntoEntity == null ) {
            return null;
        }

        AssuntoDTO assuntoDTO = new AssuntoDTO();

        assuntoDTO.setId( assuntoEntity.getId() );
        assuntoDTO.setNomeAssunto( assuntoEntity.getNomeAssunto() );

        return assuntoDTO;
    }
}
