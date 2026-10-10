package com.academic.gestor.application.services;

import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.exceptions.SolapamientoException;
import com.academic.gestor.domain.model.entities.BloqueHorario;
import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.ports.outbound.BloqueHorarioRepository;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendaServiceImpl Tests")
class AgendaServiceImplTest {

    @Mock
    private BloqueHorarioRepository bloqueRepository;

    @Mock
    private MateriaRepository materiaRepository;

    private AgendaServiceImpl agendaService;
    private UUID materiaId;
    private Materia materia;

    @BeforeEach
    void setUp() {
        agendaService = new AgendaServiceImpl(bloqueRepository, materiaRepository);
        materiaId = UUID.randomUUID();
        materia = new Materia(
                materiaId,
                "Física",
                CodigoMateria.of("FIS101"),
                Color.of("#FF0000"),
                Prioridad.ALTA,
                LocalDateTime.now(),
                true
        );
    }

    @Nested
    @DisplayName("Crear bloque")
    class CrearBloque {

        @Test
        @DisplayName("Debe crear bloque cuando no hay conflictos")
        void debeCrearBloqueSinConflictos() {
            when(materiaRepository.existsById(materiaId)).thenReturn(true);
            when(bloqueRepository.findConflictos(any(), any(), any(), any())).thenReturn(List.of());
            when(bloqueRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            BloqueHorario bloque = agendaService.crearBloque(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), "Aula 101");

            assertNotNull(bloque.getId());
            assertEquals(materiaId, bloque.getMateriaId());
            assertEquals(DiaSemana.of("LUNES"), bloque.getDiaSemana());
            verify(bloqueRepository).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si materia no existe")
        void debeLanzarExcepcionSiMateriaNoExiste() {
            when(materiaRepository.existsById(materiaId)).thenReturn(false);

            assertThrows(MateriaNotFoundException.class, () ->
                    agendaService.crearBloque(materiaId, DiaSemana.of("LUNES"),
                            LocalTime.of(12, 0), LocalTime.of(14, 0), null));
        }

        @Test
        @DisplayName("Debe lanzar SolapamientoException si hay conflicto")
        void debeLanzarSolapamientoExceptionSiHayConflicto() {
            when(materiaRepository.existsById(materiaId)).thenReturn(true);
            when(bloqueRepository.findConflictos(any(), any(), any(), any()))
                    .thenReturn(List.of(mock(BloqueHorario.class)));

            assertThrows(SolapamientoException.class, () ->
                    agendaService.crearBloque(materiaId, DiaSemana.of("LUNES"),
                            LocalTime.of(12, 0), LocalTime.of(14, 0), null));
        }
    }

    @Nested
    @DisplayName("Mover bloque (Drag & Drop)")
    class MoverBloque {

        @Test
        @DisplayName("Debe mover bloque a nueva posición válida")
        void debeMoverBloqueValido() {
            UUID bloqueId = UUID.randomUUID();
            BloqueHorario bloqueExistente = BloqueHorario.create(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), "Aula 101");
            // Force same ID for test
            try {
                java.lang.reflect.Field field = BloqueHorario.class.getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(bloqueExistente, bloqueId);
            } catch (Exception ignored) {}

            when(bloqueRepository.findById(bloqueId)).thenReturn(Optional.of(bloqueExistente));
            when(bloqueRepository.findConflictos(any(), any(), any(), any())).thenReturn(List.of());
            when(bloqueRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            BloqueHorario movido = agendaService.moverBloque(bloqueId, DiaSemana.of("MARTES"),
                    LocalTime.of(10, 0), LocalTime.of(12, 0));

            assertEquals(DiaSemana.of("MARTES"), movido.getDiaSemana());
            assertEquals(LocalTime.of(10, 0), movido.getHoraInicio());
            assertEquals(LocalTime.of(12, 0), movido.getHoraFin());
            verify(bloqueRepository).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si bloque no existe")
        void debeLanzarExcepcionSiBloqueNoExiste() {
            when(bloqueRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    agendaService.moverBloque(UUID.randomUUID(), DiaSemana.of("MARTES"),
                            LocalTime.of(10, 0), LocalTime.of(12, 0)));
        }

        @Test
        @DisplayName("Debe lanzar SolapamientoException si nueva posición tiene conflicto")
        void debeLanzarExcepcionSiNuevaPosicionConConflicto() {
            UUID bloqueId = UUID.randomUUID();
            UUID conflictoMateriaId = UUID.randomUUID();
            BloqueHorario bloqueExistente = BloqueHorario.create(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            BloqueHorario conflicto = BloqueHorario.create(conflictoMateriaId, DiaSemana.of("MARTES"),
                    LocalTime.of(10, 0), LocalTime.of(12, 0), null);
            Materia materiaConflicto = new Materia(
                    conflictoMateriaId, "Química", CodigoMateria.of("QUI101"),
                    Color.of("#00FF00"), Prioridad.MEDIA, LocalDateTime.now(), true);

            when(bloqueRepository.findById(bloqueId)).thenReturn(Optional.of(bloqueExistente));
            when(bloqueRepository.findConflictos(any(), any(), any(), any()))
                    .thenReturn(List.of(conflicto));
            when(materiaRepository.findById(conflictoMateriaId)).thenReturn(Optional.of(materiaConflicto));

            assertThrows(SolapamientoException.class, () ->
                    agendaService.moverBloque(bloqueId, DiaSemana.of("MARTES"),
                            LocalTime.of(10, 0), LocalTime.of(12, 0)));
        }
    }

    @Nested
    @DisplayName("Validar disponibilidad")
    class ValidarDisponibilidad {

        @Test
        @DisplayName("Debe retornar válido sin conflictos")
        void debeRetornarValidoSinConflictos() {
            when(bloqueRepository.findConflictos(any(), any(), any(), any())).thenReturn(List.of());
            when(bloqueRepository.findActivosByDiaSemana(any())).thenReturn(List.of());

            var response = agendaService.validarDisponibilidad(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0));

            assertTrue(response.isValido());
            assertTrue(response.getConflictos().isEmpty());
        }

        @Test
        @DisplayName("Debe retornar inválido con conflictos y sugerencias")
        void debeRetornarInvalidoConConflictosYSugerencias() {
            BloqueHorario conflicto = BloqueHorario.create(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(13, 0), LocalTime.of(15, 0), null);
            when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
            when(bloqueRepository.findConflictos(any(), any(), any(), any())).thenReturn(List.of(conflicto));
            when(bloqueRepository.findActivosByDiaSemana(any())).thenReturn(List.of());

            var response = agendaService.validarDisponibilidad(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0));

            assertFalse(response.isValido());
            assertEquals(1, response.getConflictos().size());
            assertEquals("Física", response.getConflictos().get(0).getMateriaNombre());
            assertEquals("#FF0000", response.getConflictos().get(0).getColorHex());
        }
    }

    @Nested
    @DisplayName("Listar bloques")
    class ListarBloques {

        @Test
        @DisplayName("Debe listar bloques activos por materia")
        void debeListarBloquesActivosPorMateria() {
            BloqueHorario b1 = BloqueHorario.create(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            BloqueHorario b2 = BloqueHorario.create(materiaId, DiaSemana.of("MIERCOLES"),
                    LocalTime.of(8, 0), LocalTime.of(10, 0), null);
            when(bloqueRepository.findActivosByMateriaId(materiaId)).thenReturn(List.of(b1, b2));

            List<BloqueHorario> resultado = agendaService.listarPorMateria(materiaId);

            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Debe listar bloques activos por día")
        void debeListarBloquesActivosPorDia() {
            BloqueHorario b1 = BloqueHorario.create(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            when(bloqueRepository.findActivosByDiaSemana(DiaSemana.of("LUNES"))).thenReturn(List.of(b1));

            List<BloqueHorario> resultado = agendaService.listarPorDia(DiaSemana.of("LUNES"));

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("Eliminar bloque")
    class EliminarBloque {

        @Test
        @DisplayName("Debe desactivar bloque (soft delete)")
        void debeDesactivarBloque() {
            UUID bloqueId = UUID.randomUUID();
            BloqueHorario bloque = BloqueHorario.create(materiaId, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            when(bloqueRepository.findById(bloqueId)).thenReturn(Optional.of(bloque));
            when(bloqueRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            agendaService.eliminarBloque(bloqueId);

            assertFalse(bloque.isActivo());
            verify(bloqueRepository).save(bloque);
        }
    }
}