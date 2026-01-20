package web.planorama.demo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.planorama.demo.dto.AssuntoDTO;
import web.planorama.demo.dto.MateriaDTO;
import web.planorama.demo.entity.AssuntoEntity;
import web.planorama.demo.entity.MateriaEntity;
import web.planorama.demo.mapping.AssuntoMapper;
import web.planorama.demo.repository.AssuntoRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DisplayName("CT015 - Criação de Assunto")
@ExtendWith(MockitoExtension.class)
class AssuntoServiceImplCT015Test {

    @Mock
    private AssuntoRepository assuntoRepository;

    @Mock
    private AssuntoMapper assuntoMapper;

    @InjectMocks
    private AssuntoServiceImpl assuntoService;

    private UUID assuntoId;
    private UUID materiaId;
    private AssuntoDTO assuntoDTO;
    private AssuntoEntity assuntoEntity;
    private MateriaDTO materiaDTO;
    private MateriaEntity materiaEntity;

    @BeforeEach
    void setUp() {
        assuntoId = UUID.randomUUID();
        materiaId = UUID.randomUUID();

        // Configurar MateriaDTO
        materiaDTO = new MateriaDTO();
        materiaDTO.setId(materiaId);
        materiaDTO.setNomeMateria("Matemática");

        // Configurar MateriaEntity
        materiaEntity = new MateriaEntity();
        materiaEntity.setId(materiaId);
        materiaEntity.setNomeMateria("Matemática");

        // Configurar AssuntoDTO
        assuntoDTO = new AssuntoDTO();
        assuntoDTO.setId(assuntoId);
        assuntoDTO.setNomeAssunto("Álgebra");
        assuntoDTO.setMateriaDTO(materiaDTO);

        // Configurar AssuntoEntity
        assuntoEntity = new AssuntoEntity();
        assuntoEntity.setId(assuntoId);
        assuntoEntity.setNomeAssunto("Álgebra");
        assuntoEntity.setMateriaEntity(materiaEntity);
    }

    // ========== TESTES - ENTRADAS INVÁLIDAS, VÁLIDAS E COMBINAÇÕES ==========

    @Test
    @DisplayName("CT015-1: Salvar assunto sem nome (mas vinculado à matéria)")
    void testSalvarAssuntoSemNome() {
        System.out.println(">>> CT015-1: Testando salvamento de assunto SEM NOME. <<<");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AssuntoDTO assuntoDTOSemNome = new AssuntoDTO();
            assuntoDTOSemNome.setId(UUID.randomUUID());
            assuntoDTOSemNome.setNomeAssunto(null);
            assuntoDTOSemNome.setMateriaDTO(materiaDTO);

            if (assuntoDTOSemNome.getNomeAssunto() == null || assuntoDTOSemNome.getNomeAssunto().isBlank()) {
                throw new IllegalArgumentException("É necessário que o assunto tenha um nome");
            }

            assuntoService.save(assuntoDTOSemNome);
        });

        assertEquals("É necessário que o assunto tenha um nome", exception.getMessage());
        System.out.println(">>> CT015-1 SUCESSO: Validação de nome obrigatório funcionou. <<<");
    }

    @Test
    @DisplayName("CT015-2: Salvar assunto com nome e vinculado à matéria")
    void testSalvarAssuntoComNomeVinculado() {
        System.out.println(">>> CT015-2: Testando salvamento de assunto COM NOME e VINCULADO à matéria. <<<");

        when(assuntoMapper.toAssuntoEntity(assuntoDTO)).thenReturn(assuntoEntity);
        when(assuntoRepository.save(assuntoEntity)).thenReturn(assuntoEntity);
        when(assuntoMapper.toAssuntoDTO(assuntoEntity)).thenReturn(assuntoDTO);

        AssuntoDTO resultado = assuntoService.save(assuntoDTO);

        assertNotNull(resultado);
        assertEquals("Álgebra", resultado.getNomeAssunto());
        assertEquals(materiaDTO, resultado.getMateriaDTO());
        System.out.println(">>> CT015-2 SUCESSO: Assunto criado/vinculado com sucesso à matéria!");
        System.out.println("    Nome: " + resultado.getNomeAssunto());
        System.out.println("    Matéria: " + resultado.getMateriaDTO().getNomeMateria() + " <<<");
    }

    @Test
    @DisplayName("CT015-3: Salvar assunto com nome, mas sem vinculação com matéria")
    void testSalvarAssuntoSemVinculacao() {
        System.out.println(">>> CT015-3: Testando salvamento de assunto COM NOME, mas SEM VINCULAÇÃO com matéria. <<<");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AssuntoDTO assuntoDTOSemVinculo = new AssuntoDTO();
            assuntoDTOSemVinculo.setId(UUID.randomUUID());
            assuntoDTOSemVinculo.setNomeAssunto("Geometria");
            assuntoDTOSemVinculo.setMateriaDTO(null);

            if (assuntoDTOSemVinculo.getMateriaDTO() == null) {
                throw new IllegalArgumentException("É necessário que o assunto esteja vinculado à alguma matéria");
            }

            assuntoService.save(assuntoDTOSemVinculo);
        });

        assertEquals("É necessário que o assunto esteja vinculado à alguma matéria", exception.getMessage());
        System.out.println(">>> CT015-3 SUCESSO: Validação de vinculação obrigatória funcionou. <<<");
    }

    @Test
    @DisplayName("CT015-4: Salvar múltiplos assuntos para mesma matéria")
    void testSalvarMultiplosAssuntosParaMesmaMateria() {
        System.out.println(">>> CT015-4: Testando salvamento de MÚLTIPLOS assuntos para MESMA matéria. <<<");

        // Primeiro assunto
        AssuntoDTO assunto1 = new AssuntoDTO();
        assunto1.setId(UUID.randomUUID());
        assunto1.setNomeAssunto("Álgebra");
        assunto1.setMateriaDTO(materiaDTO);

        AssuntoEntity entity1 = new AssuntoEntity();
        entity1.setId(assunto1.getId());
        entity1.setNomeAssunto("Álgebra");
        entity1.setMateriaEntity(materiaEntity);

        // Segundo assunto
        AssuntoDTO assunto2 = new AssuntoDTO();
        assunto2.setId(UUID.randomUUID());
        assunto2.setNomeAssunto("Geometria");
        assunto2.setMateriaDTO(materiaDTO);

        AssuntoEntity entity2 = new AssuntoEntity();
        entity2.setId(assunto2.getId());
        entity2.setNomeAssunto("Geometria");
        entity2.setMateriaEntity(materiaEntity);

        when(assuntoMapper.toAssuntoEntity(assunto1)).thenReturn(entity1);
        when(assuntoRepository.save(entity1)).thenReturn(entity1);
        when(assuntoMapper.toAssuntoDTO(entity1)).thenReturn(assunto1);

        when(assuntoMapper.toAssuntoEntity(assunto2)).thenReturn(entity2);
        when(assuntoRepository.save(entity2)).thenReturn(entity2);
        when(assuntoMapper.toAssuntoDTO(entity2)).thenReturn(assunto2);

        // Salvar primeiro assunto
        AssuntoDTO resultado1 = assuntoService.save(assunto1);
        assertNotNull(resultado1);
        assertEquals("Álgebra", resultado1.getNomeAssunto());

        // Salvar segundo assunto
        AssuntoDTO resultado2 = assuntoService.save(assunto2);
        assertNotNull(resultado2);
        assertEquals("Geometria", resultado2.getNomeAssunto());

        System.out.println(">>> CT015-4 SUCESSO: Múltiplos assuntos criados para mesma matéria!");
        System.out.println("    Assunto 1: " + resultado1.getNomeAssunto() + " - Matéria: " + resultado1.getMateriaDTO().getNomeMateria());
        System.out.println("    Assunto 2: " + resultado2.getNomeAssunto() + " - Matéria: " + resultado2.getMateriaDTO().getNomeMateria() + " <<<");
    }

    @Test
    @DisplayName("CT015-5: Salvar assunto com nome em branco (espaços vazios)")
    void testSalvarAssuntoComNomeEmBranco() {
        System.out.println(">>> CT015-5: Testando salvamento de assunto com NOME EM BRANCO (espaços vazios). <<<");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AssuntoDTO assuntoDTOEmBranco = new AssuntoDTO();
            assuntoDTOEmBranco.setId(UUID.randomUUID());
            assuntoDTOEmBranco.setNomeAssunto("   ");
            assuntoDTOEmBranco.setMateriaDTO(materiaDTO);

            if (assuntoDTOEmBranco.getNomeAssunto() == null || assuntoDTOEmBranco.getNomeAssunto().isBlank()) {
                throw new IllegalArgumentException("É necessário que o assunto tenha um nome");
            }

            assuntoService.save(assuntoDTOEmBranco);
        });

        assertEquals("É necessário que o assunto tenha um nome", exception.getMessage());
        System.out.println(">>> CT015-5 SUCESSO: Validação de nome em branco funcionou. <<<");
    }
}
