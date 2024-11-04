package com.example.loginproject.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Presupuesto  implements Serializable {
    private String id;
    private String nombre;
    private double monto;
    private boolean activo;

    public Presupuesto(String nombre, double monto, boolean activo) {
        this.nombre = nombre;
        this.monto = monto;
        this.activo = activo;
    }
}
