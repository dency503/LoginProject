package com.example.loginproject.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresupuestoDto {
    private String nombre;
    private double monto;
    private boolean activo;
}
