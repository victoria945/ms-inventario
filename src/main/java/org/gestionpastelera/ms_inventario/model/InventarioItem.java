package org.gestionpastelera.ms_inventario.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_pastel", nullable = false, length = 30)
    private String codigoPastel;

    @Column(name = "nombre_pastel", nullable = false, length = 120)
    private String nombrePastel;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "stock_inicial", nullable = false)
    private Integer stockInicial;

    @Column(name = "compra_id")
    private Long compraId;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
}
