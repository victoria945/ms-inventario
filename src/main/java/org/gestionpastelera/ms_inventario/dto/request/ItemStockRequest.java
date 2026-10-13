package org.gestionpastelera.ms_inventario.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStockRequest {

    @NotBlank(message = "El código del pastel es obligatorio")
        private String codigoPastel;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;
}
