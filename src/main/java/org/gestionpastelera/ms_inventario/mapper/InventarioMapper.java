package org.gestionpastelera.ms_inventario.mapper;

import org.gestionpastelera.ms_inventario.dto.request.RegistrarLoteRequest;
import org.gestionpastelera.ms_inventario.dto.response.InventarioItemResponse;
import org.gestionpastelera.ms_inventario.dto.response.MovimientoResponse;
import org.gestionpastelera.ms_inventario.model.InventarioItem;
import org.gestionpastelera.ms_inventario.model.MovimientoInventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventarioMapper {

    InventarioItemResponse toResponse(InventarioItem item);

    List<InventarioItemResponse> toResponseList(List<InventarioItem> items);

    MovimientoResponse toMovimientoResponse(MovimientoInventario movimiento);

    List<MovimientoResponse> toMovimientoResponseList(List<MovimientoInventario> movimientos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stock", source = "cantidad")
    @Mapping(target = "stockInicial", source = "cantidad")
    @Mapping(target = "fechaIngreso", expression = "java(java.time.LocalDateTime.now())")
    InventarioItem toEntity(RegistrarLoteRequest request);
}
