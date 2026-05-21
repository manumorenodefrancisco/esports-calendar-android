package com.adriim1.esports_calendar.model;

public class Anotacion {
    private int id;
    private String titulo;
    private String descripcion;
    private String fecha_hora;
    private String created_at;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha_hora() { return fecha_hora; }
    public void setFecha_hora(String fecha_hora) { this.fecha_hora = fecha_hora; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
