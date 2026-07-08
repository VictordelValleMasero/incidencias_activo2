package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.domain.entity.Incidencia;
import com.mercadona.activo2incidencias.domain.enums.EstadoIncidencia;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class IncidenciaSpecifications {

    private IncidenciaSpecifications() {
    }

    public static Specification<Incidencia> conFiltros(LocalDate fecha, Long edificioId, Long zonaId,
                                                         Long departamentoId, EstadoIncidencia estado,
                                                         Boolean whatsappError, String texto) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (fecha != null) {
                LocalDateTime inicio = fecha.atStartOfDay();
                LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), inicio));
                predicate = cb.and(predicate, cb.lessThan(root.get("createdAt"), fin));
            }
            if (edificioId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("edificio").get("id"), edificioId));
            }
            if (zonaId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("zona").get("id"), zonaId));
            }
            if (departamentoId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("departamento").get("id"), departamentoId));
            }
            if (estado != null) {
                predicate = cb.and(predicate, cb.equal(root.get("estado"), estado));
            }
            if (Boolean.TRUE.equals(whatsappError)) {
                predicate = cb.and(predicate, cb.isNotNull(root.get("whatsappError")));
            }
            if (Boolean.FALSE.equals(whatsappError)) {
                predicate = cb.and(predicate, cb.isNull(root.get("whatsappError")));
            }
            if (texto != null && !texto.isBlank()) {
                String like = "%" + texto.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("descripcion")), like),
                        cb.like(cb.lower(root.get("codigo")), like)));
            }

            return predicate;
        };
    }
}
