package org.gestionpastelera.ms_inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.gestionpastelera.ms_inventario.enums.TipoMovimiento;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjusteStockRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio (AJUSTE o MERMA)")
        private TipoMovimiento tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;

    @NotBlank(message = "El motivo del ajuste es obligatorio")
        private String motivo;
}
