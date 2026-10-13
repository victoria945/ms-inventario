package org.gestionpastelera.ms_inventario.repository;

import org.gestionpastelera.ms_inventario.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByCodigoPastelOrderByFechaMovimientoDesc(String codigoPastel);
}
