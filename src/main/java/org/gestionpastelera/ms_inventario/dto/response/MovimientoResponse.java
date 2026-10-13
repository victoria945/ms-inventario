package org.gestionpastelera.ms_inventario.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.gestionpastelera.ms_inventario.enums.TipoMovimiento;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoResponse {

        private Long id;

        private Long inventarioItemId;

        private String codigoPastel;

        private TipoMovimiento tipoMovimiento;

        private Integer cantidad;

        private String motivo;

        private String usuarioRut;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime fechaMovimiento;
}
