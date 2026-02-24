package com.adriim1.esports_calendar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import java.util.List;

public interface ApiService {
    
    @POST("api/login/")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);
    
    @POST("api/registro/")
    Call<ApiResponse> register(@Body RegisterRequest registerRequest);
    
    @GET("api/events/")
    Call<EventsResponse> getEvents();
    
    @GET("api/subscriptions/")
    Call<SubscriptionsResponse> getSubscriptions();
    
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
        private List<Match> data;
        
        public boolean isSuccess() { return success; }
        public List<Match> getData() { return data; }
    }
    
    public static class SubscriptionsResponse {
        private boolean success;
        private List<Match> data;
        
        public boolean isSuccess() { return success; }
        public List<Match> getData() { return data; }
    }
}
