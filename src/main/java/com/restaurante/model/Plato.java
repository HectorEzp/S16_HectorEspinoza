package com.restaurante.model;

import java.math.BigDecimal;

/**
 * Modelo que representa un plato del restaurante (tabla "plato").
 */
public class Plato {

    private int idPlato;
    private String nombre;
    private String categoria;
    private String descripcion;
    private BigDecimal precio;
    private int tiempoPreparacion; // minutos
    private boolean disponibilidad;

    public Plato() {
    }

    public Plato(int idPlato, String nombre, String categoria, String descripcion,
                 BigDecimal precio, int tiempoPreparacion, boolean disponibilidad) {
        this.idPlato = idPlato;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tiempoPreparacion = tiempoPreparacion;
        this.disponibilidad = disponibilidad;
    }

    public int getIdPlato() { return idPlato; }
    public void setIdPlato(int idPlato) { this.idPlato = idPlato; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getTiempoPreparacion() { return tiempoPreparacion; }
    public void setTiempoPreparacion(int tiempoPreparacion) { this.tiempoPreparacion = tiempoPreparacion; }

    public boolean isDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    @Override
    public String toString() {
        return nombre;
    }
}
