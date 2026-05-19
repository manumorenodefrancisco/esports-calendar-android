package com.adriim1.esports_calendar;

public class Suscripcion {
    private Evento evento;
    private boolean recordatorio_1_hora;
    private boolean recordatorio_5_minutos;
    private String created_at;

    public Suscripcion() {}

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public boolean isRecordatorio_1_hora() {
        return recordatorio_1_hora;
    }

    public void setRecordatorio_1_hora(boolean recordatorio_1_hora) {
        this.recordatorio_1_hora = recordatorio_1_hora;
    }

    public boolean isRecordatorio_5_minutos() {
        return recordatorio_5_minutos;
    }

    public void setRecordatorio_5_minutos(boolean recordatorio_5_minutos) {
        this.recordatorio_5_minutos = recordatorio_5_minutos;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
