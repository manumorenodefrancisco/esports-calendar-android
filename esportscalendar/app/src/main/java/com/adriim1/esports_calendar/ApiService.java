package com.adriim1.esports_calendar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;
import java.util.Map;
import com.google.gson.annotations.SerializedName;

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
    
    @GET("api/anotaciones/")
    Call<AnotacionesResponse> getAnotaciones(@Query("fecha") String fecha);
    
    @DELETE("api/anotaciones/{anotacion_id}/")
    Call<ApiResponse> deleteAnotacion(@Path("anotacion_id") int anotacionId);

    @DELETE("api/anotaciones/")
    Call<ApiResponse> deleteAnotacionSimple(@Query("id") int anotacionId);
    
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
    
    public static class TokenRequest {
        private String notification_token;
        
        public TokenRequest(String token) {
            this.notification_token = token;
        }
        
        public String getNotification_token() { return notification_token; }
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
    
    public static class AnotacionesResponse {
        private boolean success;
        private List<Anotacion> data;
        
        public boolean isSuccess() { return success; }
        public List<Anotacion> getData() { return data; }
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
    
    public static class SuscripcionRequest {
        @SerializedName("evento_id")
        private int evento_id;
        
        @SerializedName("recordatorio_1_dia")
        private boolean recordatorio_1_dia;
        
        @SerializedName("recordatorio_1_hora")
        private boolean recordatorio_1_hora;
        
        public SuscripcionRequest(int evento_id, boolean recordatorio_1_dia, boolean recordatorio_1_hora) {
            this.evento_id = evento_id;
            this.recordatorio_1_dia = recordatorio_1_dia;
            this.recordatorio_1_hora = recordatorio_1_hora;
        }
        
        public int getEvento_id() { return evento_id; }
        public boolean isRecordatorio_1_dia() { return recordatorio_1_dia; }
        public boolean isRecordatorio_1_hora() { return recordatorio_1_hora; }
    }
    
    // API endpoints
    @POST("/api/notifications/register-token/")
    Call<ApiResponse> registerNotificationToken(@Body TokenRequest tokenRequest);
    
    @POST("/api/notifications/send/{user_id}/")
    Call<ApiResponse> sendNotificationToUser(@Path("user_id") int userId, @Body Map<String, String> notification);
    
    @POST("/api/subscriptions/")
    Call<ApiResponse> suscribirEvento(@Body SuscripcionRequest suscripcionRequest);
    
    @GET("/api/notifications/")
    Call<NotificacionesResponse> getNotificaciones();
    
    @POST("/api/notifications/add")
    Call<ApiResponse> addNotification(@Body Map<String, String> notificationData);
    
    public static class PerfilRequest {
        private String email;
        private String username;
        private String birthday;
        private String phone;
        private String country;
        
        public PerfilRequest(String email, String username, String birthday, String phone, String country) {
            this.email = email;
            this.username = username;
            this.birthday = birthday;
            this.phone = phone;
            this.country = country;
        }
        
        // Getters
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getBirthday() { return birthday; }
        public String getPhone() { return phone; }
        public String getCountry() { return country; }
    }
    
    @POST("/api/perfil/actualizar")
    Call<ApiResponse> actualizarPerfil(@Body PerfilRequest perfilRequest);
    
    public static class NotificacionesResponse {
        private boolean success;
        private List<Notificacion> data;
        
        public boolean isSuccess() { return success; }
        public List<Notificacion> getData() { return data; }
    }
}
