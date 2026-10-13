package org.gestionpastelera.ms_inventario.repository;

import jakarta.persistence.LockModeType;
import org.gestionpastelera.ms_inventario.model.InventarioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventarioItemRepository extends JpaRepository<InventarioItem, Long> {

    List<InventarioItem> findByCodigoPastelOrderByFechaIngresoAsc(String codigoPastel);

    List<InventarioItem> findByCodigoPastelOrderByFechaIngresoDesc(String codigoPastel);

    List<InventarioItem> findByFechaVencimientoBeforeAndStockGreaterThan(LocalDate fecha, Integer stock);

    @Query("SELECT i FROM InventarioItem i WHERE i.codigoPastel = :codigoPastel " +
           "AND i.stock > 0 " +
           "AND (i.fechaVencimiento IS NULL OR i.fechaVencimiento >= CURRENT_DATE) " +
           "ORDER BY CASE WHEN i.fechaVencimiento IS NULL THEN 1 ELSE 0 END, i.fechaVencimiento ASC, i.fechaIngreso ASC")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<InventarioItem> findDisponiblesParaDescuentoConLock(@Param("codigoPastel") String codigoPastel);

    @Query("SELECT COALESCE(SUM(i.stock), 0) FROM InventarioItem i " +
           "WHERE i.codigoPastel = :codigoPastel " +
           "AND (i.fechaVencimiento IS NULL OR i.fechaVencimiento >= CURRENT_DATE)")
    Integer obtenerStockDisponible(@Param("codigoPastel") String codigoPastel);

    @Query("SELECT i.codigoPastel, COALESCE(SUM(i.stock), 0) FROM InventarioItem i " +
           "WHERE (i.fechaVencimiento IS NULL OR i.fechaVencimiento >= CURRENT_DATE) " +
           "AND i.codigoPastel IN :codigos " +
           "GROUP BY i.codigoPastel")
    List<Object[]> obtenerStockDisponiblePorCodigos(@Param("codigos") List<String> codigos);
}
