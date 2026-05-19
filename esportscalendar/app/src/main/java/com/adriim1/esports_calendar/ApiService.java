package com.adriim1.esports_calendar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;
import java.util.Map;
import com.google.gson.annotations.SerializedName;
import okhttp3.ResponseBody;

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
    
    @POST("api/subscriptions/")
    Call<ApiResponse> suscribirEvento(@Body SuscripcionRequest suscripcionRequest);
    
    @DELETE("api/subscriptions/{evento_id}/")
    Call<ApiResponse> eliminarSuscripcion(@Path("evento_id") int eventoId);
    
    @GET("api/preferences/recommended/")
    Call<EventsResponse> getRecommendedEvents();

    @POST("api/preferences/")
    Call<ResponseBody> generatePreferences();
    
    @POST("api/anotaciones/")
    Call<ApiResponse> createAnotacion(@Body AnotacionRequest anotacionRequest);
    
    @GET("api/anotaciones/")
    Call<AnotacionesResponse> getAnotaciones(@Query("fecha") String fecha);
    
    @DELETE("api/anotaciones/{anotacion_id}/")
    Call<ApiResponse> deleteAnotacion(@Path("anotacion_id") int anotacionId);

    @GET("api/update-perfil/")
    Call<PerfilResponse> obtenerPerfil();

    @PUT("api/update-perfil/")
    Call<ApiResponse> actualizarPerfil(@Body PerfilRequest perfilRequest);

    @POST("api/notifications/register-token/")
    Call<ApiResponse> registerNotificationToken(@Body TokenRequest tokenRequest);
    
    @GET("api/notifications/")
    Call<NotificacionesResponse> getNotificaciones();
    
    @POST("api/notifications/add/")
    Call<ApiResponse> addNotification(@Body Map<String, String> notificationData);
    

    public static class EventsResponse {
        private boolean success;
        private List<Evento> data;
        public boolean isSuccess() { return success; }
        public List<Evento> getData() { return data; }
    }
    
    public static class AnotacionesResponse {
        private boolean success;
        private List<Anotacion> data;
        public boolean isSuccess() { return success; }
        public List<Anotacion> getData() { return data; }
    }

    public static class SubscriptionsResponse {
        private boolean success;
        private List<Suscripcion> data;
        public boolean isSuccess() { return success; }
        public List<Suscripcion> getData() { return data; }
    }

    public static class NotificacionesResponse {
        private boolean success;
        private List<Notificacion> data;
        public boolean isSuccess() { return success; }
        public List<Notificacion> getData() { return data; }
    }

    public static class PerfilResponse {
        private String email, username, birthday, phone, country;
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getBirthday() { return birthday; }
        public String getPhone() { return phone; }
        public String getCountry() { return country; }
    }


    public static class PerfilRequest {
        private String email, username, password, birthday, phone, country;
        public PerfilRequest(String email, String username, String password, String birthday, String phone, String country) {
            this.email = email; this.username = username; this.password = password;
            this.birthday = birthday; this.phone = phone; this.country = country;
        }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getBirthday() { return birthday; }
        public String getPhone() { return phone; }
        public String getCountry() { return country; }
    }

    public static class LoginRequest {
        private String email, password;
        public LoginRequest(String email, String password) { this.email = email; this.password = password; }
    }
    
    public static class RegisterRequest {
        private String email, password1, password2;
        public RegisterRequest(String email, String p1, String p2) { this.email = email; this.password1 = p1; this.password2 = p2; }
    }

    public static class TokenRequest {
        private String notification_token;
        public TokenRequest(String t) { this.notification_token = t; }
        public String getNotification_token() { return notification_token; }
    }

    public static class AnotacionRequest {
        private String titulo, descripcion, fecha_hora;
        public AnotacionRequest(String t, String d, String f) { this.titulo = t; this.descripcion = d; this.fecha_hora = f; }
    }

    public static class SuscripcionRequest {
        @SerializedName("evento_id") private int evento_id;
        @SerializedName("recordatorio_1_hora") private boolean recordatorio_1_hora;
        @SerializedName("recordatorio_5_minutos") private boolean recordatorio_5_minutos;
        public SuscripcionRequest(int id, boolean h, boolean m) { this.evento_id = id; this.recordatorio_1_hora = h; this.recordatorio_5_minutos = m; }
    }
}
