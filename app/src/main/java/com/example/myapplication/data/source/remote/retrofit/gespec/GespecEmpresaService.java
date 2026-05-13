package com.example.myapplication.data.source.remote.retrofit.gespec;


import com.example.myapplication.data.source.local.entities.Empresa;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecEmpresaService {

    @GET("empresas/{usuario}")
    Call<List<Empresa>> getAll(@Path("usuario") String usuario);
}
