package com.example.myapplication.data.source.remote.retrofit.gespec;


import com.example.myapplication.data.source.local.entities.Raca;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecRacasService {

    @GET("racas/{usuario}")
    Call<List<Raca>> getAll(@Path("usuario") String usuario);
}
