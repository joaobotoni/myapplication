package com.example.myapplication.data.source.remote.gespec;

import android.content.Context;


import com.example.myapplication.R;
import com.example.myapplication.data.models.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import dagger.hilt.android.qualifiers.ApplicationContext;
import jakarta.inject.Inject;

public class GespecSyncAcessoRemoteDataSource {
    private final Context context;
    @Inject
    public GespecSyncAcessoRemoteDataSource(@ApplicationContext Context context) {
        this.context = context;
    }

    public String sync(Configuration config) throws IOException {
        HttpURLConnection connection = openConnection(buildUrl(config));
        applyHeaders(connection, config);
        return readResponse(connection);
    }

    private String buildUrl(Configuration config) {
        return context.getString(R.string.url_acesso_gespec, config.host, config.port, config.username);
    }

    private HttpURLConnection openConnection(String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    private void applyHeaders(HttpURLConnection connection, Configuration config) throws ProtocolException {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("DATASOURCE_DEFAULT", config.site);
        connection.setRequestProperty("USUARIO_LOGADO", config.username);
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
            return body.toString();
        } finally {
            connection.disconnect();
        }
    }
}