package org.gestionpastelera.ms_inventario.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockProductoResponse {

        private String codigoPastel;

        private String nombrePastel;

        private Integer stockDisponible;

        private List<InventarioItemResponse> lotes;
}
