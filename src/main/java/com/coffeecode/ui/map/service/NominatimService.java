package com.coffeecode.ui.map.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NominatimService {

    private static final String NOMINATIM_API = "https://nominatim.openstreetmap.org/reverse";
    private static final String USER_AGENT = "GraphAlgorithmVisualizer/1.0";

    private NominatimService() {
        throw new IllegalStateException("Utility class");
    }

    public static String getLocationName(double lat, double lon) {
        try {
            String urlString = String.format("%s?format=json&lat=%f&lon=%f",
                    NOMINATIM_API, lat, lon);

            URI uri = new URI(urlString);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("Failed to get location name. Response code: {}", responseCode);
                return "Error getting location";
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                String locationName = parseLocationName(response.toString());
                log.debug("Location name retrieved: {}", locationName);
                return locationName;
            }

        } catch (URISyntaxException e) {
            log.error("Invalid URI syntax", e);
            return "Invalid location";
        } catch (IOException e) {
            log.error("Failed to connect to Nominatim service", e);
            return "Connection error";
        } catch (Exception e) {
            log.error("Unexpected error while getting location name", e);
            return "Unknown error";
        }
    }

    private static String parseLocationName(String jsonResponse) {
        try {
            return jsonResponse.split("\"display_name\":\"")[1].split("\"")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Failed to parse location name from response: {}", jsonResponse, e);
            return "Parse error";
        }
    }
}
