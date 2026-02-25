package com.adriim1.esports_calendar;

public class Suscripcion {
    private Evento evento;
    private boolean recordatorio_1_dia;
    private boolean recordatorio_1_hora;
    private String created_at;

    public Suscripcion() {}

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public boolean isRecordatorio_1_dia() {
        return recordatorio_1_dia;
    }

    public void setRecordatorio_1_dia(boolean recordatorio_1_dia) {
        this.recordatorio_1_dia = recordatorio_1_dia;
    }

    public boolean isRecordatorio_1_hora() {
        return recordatorio_1_hora;
    }

    public void setRecordatorio_1_hora(boolean recordatorio_1_hora) {
        this.recordatorio_1_hora = recordatorio_1_hora;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
