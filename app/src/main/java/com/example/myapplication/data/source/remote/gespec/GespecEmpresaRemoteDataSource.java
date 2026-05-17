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

public class GespecEmpresaRemoteDataSource {
    private final Context context;

    @Inject
    public GespecEmpresaRemoteDataSource(@ApplicationContext Context context) {
        this.context = context;
    }

    public String sync(Configuration config, String path) throws IOException {
        HttpURLConnection connection = openConnection(buildUrl(config, path));
        applyHeaders(connection);
        return readResponse(connection);
    }

    private String buildUrl(Configuration config, String path) {
        return context.getString(R.string.url_negociacao_gespec,
                config.host,
                config.port
        );
    }

    private HttpURLConnection openConnection(String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }

    private void applyHeaders(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
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
