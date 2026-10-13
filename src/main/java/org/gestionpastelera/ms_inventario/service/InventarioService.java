package org.gestionpastelera.ms_inventario.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gestionpastelera.ms_inventario.dto.request.AjusteStockRequest;
import org.gestionpastelera.ms_inventario.dto.request.ItemStockRequest;
import org.gestionpastelera.ms_inventario.dto.request.MovimientoStockBulkRequest;
import org.gestionpastelera.ms_inventario.dto.request.RegistrarLoteRequest;
import org.gestionpastelera.ms_inventario.dto.response.InventarioItemResponse;
import org.gestionpastelera.ms_inventario.dto.response.MovimientoResponse;
import org.gestionpastelera.ms_inventario.dto.response.StockProductoResponse;
import org.gestionpastelera.ms_inventario.enums.TipoMovimiento;
import org.gestionpastelera.ms_inventario.mapper.InventarioMapper;
import org.gestionpastelera.ms_inventario.model.InventarioItem;
import org.gestionpastelera.ms_inventario.model.MovimientoInventario;
import org.gestionpastelera.ms_inventario.repository.InventarioItemRepository;
import org.gestionpastelera.ms_inventario.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final InventarioItemRepository itemRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final InventarioMapper mapper;

    @Transactional(readOnly = true)
    public List<InventarioItemResponse> listarTodos() {
        return mapper.toResponseList(itemRepository.findAll());
    }

    @Transactional(readOnly = true)
    public InventarioItemResponse obtenerPorId(Long id) {
        InventarioItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote de inventario no encontrado con ID: " + id));
        return mapper.toResponse(item);
    }

    @Transactional(readOnly = true)
    public StockProductoResponse obtenerPorCodigo(String codigoPastel) {
        List<InventarioItem> lotes = itemRepository.findByCodigoPastelOrderByFechaIngresoAsc(codigoPastel);
        if (lotes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron lotes para el producto con código: " + codigoPastel);
        }

        Integer stockDisponible = itemRepository.obtenerStockDisponible(codigoPastel);
        String nombrePastel = lotes.get(0).getNombrePastel();

        return StockProductoResponse.builder()
                .codigoPastel(codigoPastel)
                .nombrePastel(nombrePastel)
                .stockDisponible(stockDisponible)
                .lotes(mapper.toResponseList(lotes))
                .build();
    }

    @Transactional(readOnly = true)
    public List<InventarioItemResponse> listarVencidos() {
        List<InventarioItem> vencidos = itemRepository.findByFechaVencimientoBeforeAndStockGreaterThan(LocalDate.now(), 0);
        return mapper.toResponseList(vencidos);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> obtenerKardexPorCodigo(String codigoPastel) {
        List<MovimientoInventario> movimientos = movimientoRepository.findByCodigoPastelOrderByFechaMovimientoDesc(codigoPastel);
        return mapper.toMovimientoResponseList(movimientos);
    }

    @Transactional
    public InventarioItemResponse registrarLote(RegistrarLoteRequest request, String usuarioRut) {
        InventarioItem item = mapper.toEntity(request);
        InventarioItem guardado = itemRepository.save(item);

        MovimientoInventario mov = MovimientoInventario.builder()
                .inventarioItemId(guardado.getId())
                .codigoPastel(guardado.getCodigoPastel())
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(guardado.getStock())
                .motivo(request.getCompraId() != null ? "Ingreso por compra #" + request.getCompraId() : "Ingreso de lote")
                .usuarioRut(usuarioRut)
                .fechaMovimiento(LocalDateTime.now())
                .build();
        movimientoRepository.save(mov);

        return mapper.toResponse(guardado);
    }

    @Transactional
    public InventarioItemResponse ajustarStock(Long id, AjusteStockRequest request, String usuarioRut) {
        InventarioItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote de inventario no encontrado con ID: " + id));

        TipoMovimiento tipo = request.getTipoMovimiento();
        if (tipo != TipoMovimiento.AJUSTE && tipo != TipoMovimiento.MERMA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de movimiento para ajuste debe ser AJUSTE o MERMA");
        }

        int cantidad = request.getCantidad();
        if (tipo == TipoMovimiento.MERMA) {
            if (cantidad <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad para merma debe ser un valor positivo");
            }
            if (item.getStock() < cantidad) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede registrar merma mayor al stock actual del lote (" + item.getStock() + ")");
            }
            item.setStock(item.getStock() - cantidad);
        } else {
            if (cantidad == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad para ajuste no puede ser cero");
            }
            int nuevoStock = item.getStock() + cantidad;
            if (nuevoStock < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ajuste dejaría el stock en negativo (" + nuevoStock + ")");
            }
            item.setStock(nuevoStock);
        }

        InventarioItem actualizado = itemRepository.save(item);

        MovimientoInventario mov = MovimientoInventario.builder()
                .inventarioItemId(actualizado.getId())
                .codigoPastel(actualizado.getCodigoPastel())
                .tipoMovimiento(tipo)
                .cantidad(Math.abs(cantidad))
                .motivo(request.getMotivo())
                .usuarioRut(usuarioRut)
                .fechaMovimiento(LocalDateTime.now())
                .build();
        movimientoRepository.save(mov);

        return mapper.toResponse(actualizado);
    }

    @Transactional
    public void eliminarLote(Long id) {
        InventarioItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote de inventario no encontrado con ID: " + id));
        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> obtenerStockBulk(List<String> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> codigosLimpios = codigos.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(c -> !c.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (codigosLimpios.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> resultado = new HashMap<>();
        for (String c : codigosLimpios) {
            resultado.put(c, 0);
        }

        List<Object[]> filas = itemRepository.obtenerStockDisponiblePorCodigos(codigosLimpios);
        for (Object[] fila : filas) {
            String codigo = (String) fila[0];
            Number total = (Number) fila[1];
            resultado.put(codigo, total != null ? total.intValue() : 0);
        }

        return resultado;
    }

    @Transactional
    public void descontarStockBulk(MovimientoStockBulkRequest request, String usuarioRut) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La lista de ítems a descontar no puede estar vacía");
        }

        Map<String, Integer> requeridos = new HashMap<>();
        for (ItemStockRequest item : request.getItems()) {
            requeridos.merge(item.getCodigoPastel().trim(), item.getCantidad(), Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : requeridos.entrySet()) {
            String codigo = entry.getKey();
            int solicitado = entry.getValue();
            Integer disponible = itemRepository.obtenerStockDisponible(codigo);
            if (disponible == null || disponible < solicitado) {
                int disp = disponible != null ? disponible : 0;
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(
                        "Stock insuficiente para el producto '%s'. Disponible: %d, solicitado: %d",
                        codigo, disp, solicitado
                ));
            }
        }

        String motivo = request.getMotivo() != null && !request.getMotivo().isBlank()
                ? request.getMotivo().trim()
                : "Descuento masivo de stock";

        for (Map.Entry<String, Integer> entry : requeridos.entrySet()) {
            String codigo = entry.getKey();
            int porDescontar = entry.getValue();

            List<InventarioItem> lotes = itemRepository.findDisponiblesParaDescuentoConLock(codigo);

            for (InventarioItem lote : lotes) {
                if (porDescontar <= 0) break;

                int disponibleEnLote = lote.getStock();
                int aRestar = Math.min(disponibleEnLote, porDescontar);

                lote.setStock(disponibleEnLote - aRestar);
                itemRepository.save(lote);

                MovimientoInventario mov = MovimientoInventario.builder()
                        .inventarioItemId(lote.getId())
                        .codigoPastel(codigo)
                        .tipoMovimiento(TipoMovimiento.SALIDA)
                        .cantidad(aRestar)
                        .motivo(motivo)
                        .usuarioRut(usuarioRut)
                        .fechaMovimiento(LocalDateTime.now())
                        .build();
                movimientoRepository.save(mov);

                porDescontar -= aRestar;
            }

            if (porDescontar > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error inesperado: no fue posible descontar la totalidad de unidades para " + codigo);
            }
        }
    }

    @Transactional
    public void reintegrarStockBulk(MovimientoStockBulkRequest request, String usuarioRut) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La lista de ítems a reintegrar no puede estar vacía");
        }

        String motivo = request.getMotivo() != null && !request.getMotivo().isBlank()
                ? request.getMotivo().trim()
                : "Reintegro de stock";

        for (ItemStockRequest itemReq : request.getItems()) {
            String codigo = itemReq.getCodigoPastel().trim();
            int cantidad = itemReq.getCantidad();

            List<InventarioItem> lotes = itemRepository.findByCodigoPastelOrderByFechaIngresoDesc(codigo);
            if (lotes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen lotes registrados para el producto: " + codigo);
            }

            InventarioItem lote = lotes.get(0);
            lote.setStock(lote.getStock() + cantidad);
            itemRepository.save(lote);

            MovimientoInventario mov = MovimientoInventario.builder()
                    .inventarioItemId(lote.getId())
                    .codigoPastel(codigo)
                    .tipoMovimiento(TipoMovimiento.REINTEGRO)
                    .cantidad(cantidad)
                    .motivo(motivo)
                    .usuarioRut(usuarioRut)
                    .fechaMovimiento(LocalDateTime.now())
                    .build();
            movimientoRepository.save(mov);
        }
    }
}
