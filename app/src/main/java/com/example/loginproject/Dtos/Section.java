package com.example.loginproject.Dtos;

import com.example.loginproject.models.Presupuesto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Section {
    private String sectionName;
    private List<Presupuesto> sectionItems;
}
