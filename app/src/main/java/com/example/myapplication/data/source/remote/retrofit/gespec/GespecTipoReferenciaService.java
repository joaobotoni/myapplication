package com.example.myapplication.data.source.remote.retrofit.gespec;


import com.example.myapplication.data.source.local.entities.TipoReferencia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecTipoReferenciaService {

    @GET("tiposReferencia/{usuario}")
    Call<List<TipoReferencia>> getAll(@Path("usuario") String usuario);
}
