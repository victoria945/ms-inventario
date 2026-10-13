package org.gestionpastelera.ms_inventario.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStockBulkRequest {

        private String motivo;

    @NotEmpty(message = "La lista de ítems a procesar no puede estar vacía")
    @Valid
        private List<ItemStockRequest> items;
}
