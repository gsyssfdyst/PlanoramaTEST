package web.planorama.demo.mapping;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import web.planorama.demo.dto.RegistrarEstudoDTO;
import web.planorama.demo.entity.AssuntoEntity;
import web.planorama.demo.entity.RegistrarEstudoEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-07T21:57:20+0000",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class RegistrarEstudoMapperImpl implements RegistrarEstudoMapper {

    @Override
    public RegistrarEstudoEntity toRegistrarEstudoEntity(RegistrarEstudoDTO registrarEstudoDTO) {
        if ( registrarEstudoDTO == null ) {
            return null;
        }

        RegistrarEstudoEntity registrarEstudoEntity = new RegistrarEstudoEntity();

        registrarEstudoEntity.setAssunto( mapAssuntoIdToAssuntoEntity( registrarEstudoDTO.getAssuntoId() ) );

        registrarEstudoEntity.setDuracaoEmMinutos( registrarEstudoDTO.getHorasEstudadas() * 60 + registrarEstudoDTO.getMinutosEstudados() );

        return registrarEstudoEntity;
    }

    @Override
    public RegistrarEstudoDTO toRegistrarEstudoDTO(RegistrarEstudoEntity registrarEstudoEntity) {
        if ( registrarEstudoEntity == null ) {
            return null;
        }

        RegistrarEstudoDTO registrarEstudoDTO = new RegistrarEstudoDTO();

        registrarEstudoDTO.setAssuntoId( registrarEstudoEntityAssuntoId( registrarEstudoEntity ) );
        registrarEstudoDTO.setId( registrarEstudoEntity.getId() );

        calcularHorasEMinutos( registrarEstudoDTO, registrarEstudoEntity );

        return registrarEstudoDTO;
    }

    private UUID registrarEstudoEntityAssuntoId(RegistrarEstudoEntity registrarEstudoEntity) {
        AssuntoEntity assunto = registrarEstudoEntity.getAssunto();
        if ( assunto == null ) {
            return null;
        }
        return assunto.getId();
    }
}
