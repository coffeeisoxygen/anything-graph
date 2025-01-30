package com.coffeecode.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NominatimService {

    private NominatimService() {
        throw new IllegalStateException("Utility class");
    }

    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/reverse";

    public static String getLocationName(double lat, double lon) {
        try {
            String urlString = String.format("%s?format=json&lat=%f&lon=%f",
                    NOMINATIM_API, lat, lon);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "GraphAlgorithmVisualizer/1.0");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Simple parsing - in production you should use proper JSON parser
                return response.toString()
                        .split("\"display_name\":\"")[1]
                        .split("\"")[0];
            }
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            return "Undefined";
        }
    }
}
