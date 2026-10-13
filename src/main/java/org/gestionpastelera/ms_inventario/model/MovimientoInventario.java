package org.gestionpastelera.ms_inventario.model;

import jakarta.persistence.*;
import lombok.*;
import org.gestionpastelera.ms_inventario.enums.TipoMovimiento;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventario_item_id")
    private Long inventarioItemId;

    @Column(name = "codigo_pastel", nullable = false, length = 30)
    private String codigoPastel;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "usuario_rut", nullable = false, length = 12)
    private String usuarioRut;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;
}
