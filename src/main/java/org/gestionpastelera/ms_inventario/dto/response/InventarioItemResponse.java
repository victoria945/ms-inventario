package org.gestionpastelera.ms_inventario.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioItemResponse {

        private Long id;

        private String codigoPastel;

        private String nombrePastel;

        private Integer stock;

        private Integer stockInicial;

        private Long compraId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime fechaIngreso;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate fechaVencimiento;
}
