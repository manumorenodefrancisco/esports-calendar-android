package com.adriim1.esports_calendar;

public class Notificacion {
    private int id;
    private String titulo;
    private String mensaje;
    private String evento_nombre;
    private int evento_id;
    private String created_at;
    private boolean leida;
    
    public Notificacion() {}
    
    public Notificacion(String titulo, String mensaje, String evento_nombre, int evento_id) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.evento_nombre = evento_nombre;
        this.evento_id = evento_id;
        this.leida = false;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getEvento_nombre() { return evento_nombre; }
    public void setEvento_nombre(String evento_nombre) { this.evento_nombre = evento_nombre; }
    
    public int getEvento_id() { return evento_id; }
    public void setEvento_id(int evento_id) { this.evento_id = evento_id; }
    
    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
    
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}
