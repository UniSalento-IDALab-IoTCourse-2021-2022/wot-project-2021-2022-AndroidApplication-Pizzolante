package com.example.worksafe;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface RetrofitInterface {

    @GET("/settings")
    Call<List<SettingsResult>>getSettings ();

    @GET("/beacons")
    Call<List<BeaconsResult>> getBeacons ();

    /*@POST("/settings")
    Call<ArrayList<Risks>> executeRiskInsertion (@Body HashMap<String,Object> map);*/

}
