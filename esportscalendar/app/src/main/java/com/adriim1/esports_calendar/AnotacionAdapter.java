package com.adriim1.esports_calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnotacionAdapter extends RecyclerView.Adapter<AnotacionAdapter.AnotacionViewHolder> {

    private List<Anotacion> anotacionList;

    public AnotacionAdapter(List<Anotacion> anotacionList) {
        this.anotacionList = anotacionList;
    }

    @NonNull
    @Override
    public AnotacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anotacion, parent, false);
        return new AnotacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnotacionViewHolder holder, int position) {
        Anotacion anotacion = anotacionList.get(position);

        holder.tituloTV.setText(anotacion.getTitulo() != null ? anotacion.getTitulo() : "Sin título");

        String descripcion = anotacion.getDescripcion();
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            holder.descripcionTV.setText(descripcion);
            holder.descripcionTV.setVisibility(View.VISIBLE);
        } else {
            holder.descripcionTV.setVisibility(View.GONE);
        }

        String hora = "Sin hora";
        if (anotacion.getFecha_hora() != null && anotacion.getFecha_hora().length() >= 16) {
            hora = anotacion.getFecha_hora().substring(11, 16);
        }
        holder.horaTV.setText(hora);

        holder.containerBorrar.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        holder.containerBorrar.setClipToOutline(true);

        Context contexto = holder.itemView.getContext();

        holder.containerBorrar.setOnClickListener(v -> borrarAnotacion(contexto, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return anotacionList.size();
    }

    public List<Anotacion> getAnotacionList() {
        return anotacionList;
    }

    class AnotacionViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTV;
        TextView descripcionTV;
        TextView horaTV;
        ImageView borrarBtn;
        FrameLayout containerBorrar;

        public AnotacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTV = itemView.findViewById(R.id.text_view_anotacion_titulo);
            descripcionTV = itemView.findViewById(R.id.text_view_anotacion_descripcion);
            horaTV = itemView.findViewById(R.id.text_view_anotacion_hora);
            borrarBtn = itemView.findViewById(R.id.btn_borrar_anotacion);
            containerBorrar = itemView.findViewById(R.id.container_btn_borrar); // Enlazamos el contenedor
        }
    }

    private void borrarAnotacion(Context contexto, int position) {
        if (position == RecyclerView.NO_POSITION || position >= anotacionList.size()) {
            return;
        }

        Anotacion a = anotacionList.get(position);

        SharedPreferences prefs = contexto.getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        ApiService api = token != null ? RetrofitClient.getApiService(token) : RetrofitClient.getApiService();
        api.deleteAnotacion(a.getId()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    int currentPos = position;
                    if (currentPos < anotacionList.size()) {
                        anotacionList.remove(currentPos);
                        notifyItemRemoved(currentPos);
                        notifyItemRangeChanged(currentPos, anotacionList.size());
                        Toast.makeText(contexto, "Anotación borrada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(contexto, "Error borrando anotación", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(contexto, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}