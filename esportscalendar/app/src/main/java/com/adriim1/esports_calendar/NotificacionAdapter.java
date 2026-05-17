package com.adriim1.esports_calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {
    
    private List<Notificacion> notificacionList;
    
    public NotificacionAdapter(List<Notificacion> notificacionList) {
        this.notificacionList = notificacionList;
    }
    
    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new NotificacionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificacion notificacion = notificacionList.get(position);
        holder.bind(notificacion);
    }
    
    @Override
    public int getItemCount() {
        return notificacionList.size();
    }
    
    static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTV;
        TextView mensajeTV;
        TextView eventoTV;
        TextView horaTV;
        
        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTV = itemView.findViewById(R.id.text_view_notificacion_titulo);
            mensajeTV = itemView.findViewById(R.id.text_view_notificacion_mensaje);
            eventoTV = itemView.findViewById(R.id.text_view_notificacion_evento);
            horaTV = itemView.findViewById(R.id.text_view_notificacion_hora);
        }
        
        public void bind(Notificacion notificacion) {
            tituloTV.setText(notificacion.getTitulo());
            mensajeTV.setText(notificacion.getMensaje());
            eventoTV.setText(notificacion.getEvento_nombre());
            
            if (notificacion.getCreated_at() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(notificacion.getCreated_at());
                    horaTV.setText(sdf.format(date));
                } catch (Exception e) {
                    horaTV.setText("--:--");
                }
            }

            if (notificacion.isLeida()) {
                tituloTV.setAlpha(0.5f);
                mensajeTV.setAlpha(0.5f);
                eventoTV.setAlpha(0.5f);
                horaTV.setAlpha(0.5f);
            } else {
                tituloTV.setAlpha(1.0f);
                mensajeTV.setAlpha(1.0f);
                eventoTV.setAlpha(1.0f);
                horaTV.setAlpha(1.0f);
            }
        }
    }
}
