package web.planorama.demo.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import web.planorama.demo.dto.RegistrarEstudoDTO;
import web.planorama.demo.entity.RegistrarEstudoEntity;

@Mapper(componentModel = "spring")
public interface RegistrarEstudoMapper {

    @Mapping(target = "assuntoId", source = "assunto.id")
    @Mapping(target = "planejamentoId", source = "materiaPlanejamento.planejamentoEntity.id")
    @Mapping(target = "idMateriaPlanejamento", source = "materiaPlanejamento.id")
    RegistrarEstudoDTO toRegistrarEstudoDTO(RegistrarEstudoEntity entity);

    @Mapping(target = "assunto.id", source = "assuntoId")
    @Mapping(target = "materiaPlanejamento.id", source = "idMateriaPlanejamento")
    RegistrarEstudoEntity toRegistrarEstudoEntity(RegistrarEstudoDTO dto);
}