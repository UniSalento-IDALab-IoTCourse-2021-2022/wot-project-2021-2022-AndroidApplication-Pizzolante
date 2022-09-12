package com.example.worksafe;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.HashMap;

public interface RetrofitInterface {

    @GET("/settings")
    Call<SettingsResult> executeSettingsAcquisition (@Body HashMap<String,Object> map);

}
