//EducaMadrid: Servicio de alerts de avisos
package com.educamadrid.cloudeduca.services;

import com.educamadrid.cloudeduca.utils.Alert;
import com.educamadrid.cloudeduca.utils.AlertObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * interface AlertService
 */
public interface AlertService {
    @GET("public/news/js/apps.json")
//    @GET("6ee42b4496a073aeb0e7") // con obj cloud
//    @GET("a47706d1beedd6bbaad4") // sin obj cloud
    Call<AlertObject> getAlert(@QueryMap Map<String, String> params);
}

