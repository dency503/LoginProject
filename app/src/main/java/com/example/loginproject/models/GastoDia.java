package com.example.loginproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GastoDia {
    private String nombreArticulo;
    private double precio;
    private int cantidad;
    private double subTotal;
    private String idPresupuesto;
}
