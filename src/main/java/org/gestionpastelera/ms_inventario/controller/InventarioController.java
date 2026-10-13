package org.gestionpastelera.ms_inventario.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestionpastelera.ms_inventario.dto.request.AjusteStockRequest;
import org.gestionpastelera.ms_inventario.dto.request.MovimientoStockBulkRequest;
import org.gestionpastelera.ms_inventario.dto.request.RegistrarLoteRequest;
import org.gestionpastelera.ms_inventario.dto.response.InventarioItemResponse;
import org.gestionpastelera.ms_inventario.dto.response.MovimientoResponse;
import org.gestionpastelera.ms_inventario.dto.response.StockProductoResponse;
import org.gestionpastelera.ms_inventario.service.InventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

        @GetMapping
    public ResponseEntity<List<InventarioItemResponse>> listarTodos() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

        @GetMapping("/{id}")
    public ResponseEntity<InventarioItemResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

        @GetMapping("/codigo/{codigoPastel}")
    public ResponseEntity<StockProductoResponse> obtenerPorCodigo(@PathVariable String codigoPastel) {
        return ResponseEntity.ok(inventarioService.obtenerPorCodigo(codigoPastel));
    }

        @GetMapping("/vencidos")
    public ResponseEntity<List<InventarioItemResponse>> listarVencidos() {
        return ResponseEntity.ok(inventarioService.listarVencidos());
    }

        @GetMapping("/kardex/{codigoPastel}")
    public ResponseEntity<List<MovimientoResponse>> obtenerKardex(@PathVariable String codigoPastel) {
        return ResponseEntity.ok(inventarioService.obtenerKardexPorCodigo(codigoPastel));
    }

        @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR_SUPERVISOR')")
    public ResponseEntity<InventarioItemResponse> registrarLote(
            @Valid @RequestBody RegistrarLoteRequest request,
            Authentication authentication
    ) {
        String rut = authentication != null ? authentication.getName() : "SISTEMA";
        InventarioItemResponse response = inventarioService.registrarLote(request, rut);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

        @PatchMapping("/{id}/ajuste")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR_SUPERVISOR')")
    public ResponseEntity<InventarioItemResponse> ajustarStock(
            @PathVariable Long id,
            @Valid @RequestBody AjusteStockRequest request,
            Authentication authentication
    ) {
        String rut = authentication != null ? authentication.getName() : "SISTEMA";
        return ResponseEntity.ok(inventarioService.ajustarStock(id, request, rut));
    }

        @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarLote(@PathVariable Long id) {
        inventarioService.eliminarLote(id);
        return ResponseEntity.noContent().build();
    }

        @PostMapping("/stock/bulk")
    public ResponseEntity<Map<String, Integer>> obtenerStockBulk(@RequestBody List<String> codigos) {
        return ResponseEntity.ok(inventarioService.obtenerStockBulk(codigos));
    }

        @PostMapping("/descontar-bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR_SUPERVISOR', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<Void> descontarStockBulk(
            @Valid @RequestBody MovimientoStockBulkRequest request,
            Authentication authentication
    ) {
        String rut = authentication != null ? authentication.getName() : "SISTEMA";
        inventarioService.descontarStockBulk(request, rut);
        return ResponseEntity.noContent().build();
    }

        @PostMapping("/reintegrar-bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR_SUPERVISOR', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<Void> reintegrarStockBulk(
            @Valid @RequestBody MovimientoStockBulkRequest request,
            Authentication authentication
    ) {
        String rut = authentication != null ? authentication.getName() : "SISTEMA";
        inventarioService.reintegrarStockBulk(request, rut);
        return ResponseEntity.noContent().build();
    }
}
