package com.adriim1.esports_calendar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.util.List;

public interface ApiService {
    
    @POST("api/login/")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);
    
    @POST("api/registro/")
    Call<ApiResponse> register(@Body RegisterRequest registerRequest);
    
    @GET("api/events/")
    Call<EventsResponse> getEvents();
    
    @GET("api/events/")
    Call<EventsResponse> getEventsByDate(@Query("date") String date);
    
    @GET("api/events/")
    Call<EventsResponse> searchEvents(@Query("id") String id, @Query("external_id") String externalId, 
                                     @Query("search") String search, @Query("videogame") String videogame, 
                                     @Query("status") String status);
    
    @GET("api/subscriptions/")
    Call<SubscriptionsResponse> getSubscriptions();
    
    @GET("api/preferences/recommended/")
    Call<EventsResponse> getRecommendedEvents();
    
    @POST("api/anotaciones/")
    Call<ApiResponse> createAnotacion(@Body AnotacionRequest anotacionRequest);
    
    public static class LoginRequest {
        private String email;
        private String password;
        
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
        
        // Getters para Gson
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }
    
    public static class RegisterRequest {
        private String email;
        private String password1;
        private String password2;
        
        public RegisterRequest(String email, String password1, String password2) {
            this.email = email;
            this.password1 = password1;
            this.password2 = password2;
        }
        
        // Getters para Gson
        public String getEmail() { return email; }
        public String getPassword1() { return password1; }
        public String getPassword2() { return password2; }
    }
    
    public static class EventsResponse {
        private boolean success;
        private List<Evento> data;
        private int count;
        
        public boolean isSuccess() { return success; }
        public List<Evento> getData() { return data; }
        public int getCount() { return count; }
    }
    
    public static class SubscriptionsResponse {
        private boolean success;
        private List<Suscripcion> data;
        
        public boolean isSuccess() { return success; }
        public List<Suscripcion> getData() { return data; }
    }
    
    public static class AnotacionRequest {
        private String titulo;
        private String descripcion;
        private String fecha_hora;
        
        public AnotacionRequest(String titulo, String descripcion, String fecha_hora) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.fecha_hora = fecha_hora;
        }
        
        public String getTitulo() { return titulo; }
        public String getDescripcion() { return descripcion; }
        public String getFecha_hora() { return fecha_hora; }
    }
}
