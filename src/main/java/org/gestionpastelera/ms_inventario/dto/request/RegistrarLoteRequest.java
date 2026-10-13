package org.gestionpastelera.ms_inventario.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarLoteRequest {

    @NotBlank(message = "El código del pastel es obligatorio")
        private String codigoPastel;

    @NotBlank(message = "El nombre del pastel es obligatorio")
        private String nombrePastel;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;

        private Long compraId;

    @FutureOrPresent(message = "La fecha de vencimiento no puede ser pasada")
        private LocalDate fechaVencimiento;
}
