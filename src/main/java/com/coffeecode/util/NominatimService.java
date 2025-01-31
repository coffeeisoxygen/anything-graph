package com.coffeecode.util;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class NominatimService implements MapSearch {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/";

    // Find the name of a location given its longitude and latitude
    @Override
    public String findName(double longitude, double latitude) {
        try {
            String url = NOMINATIM_URL + "reverse?format=json&lon=" + longitude + "&lat=" + latitude;
            JSONObject response = getResponse(url);
            if (response == null || !response.has("address")) {
                log.warn("Invalid response or missing address for coordinates: {}, {}", longitude, latitude);
                return "Unknown Location";
            }
            return response.getJSONObject("address").optString("display_name", "Unknown Location");
        } catch (Exception e) {
            log.error("Error finding name for coordinates: {}, {}", longitude, latitude, e);
            return "Unknown Location";
        }
    }

    // Find the longitude and latitude of a location given its name
    @Override
    public double[] findLongLat(String name) {
        try {
            String url = NOMINATIM_URL + "search?format=json&q=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
            JSONArray results = getResponseArray(url);
            if (results == null || results.isEmpty()) {
                log.warn("No results found for location name: {}", name);
                return new double[0];
            }
            JSONObject location = results.getJSONObject(0);
            double lon = location.getDouble("lon");
            double lat = location.getDouble("lat");
            return new double[]{lon, lat};
        } catch (Exception e) {
            log.error("Error finding coordinates for name: {}", name, e);
            return new double[0];
        }
    }

    // Helper method to get JSON response from a URL
    private JSONObject getResponse(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(5000); // 5 seconds timeout
        conn.setReadTimeout(5000);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                log.error("Failed request. HTTP status: {}", status);
                return null;
            }
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return new JSONObject(content.toString());
        } finally {
            conn.disconnect();
        }
    }

    private JSONArray getResponseArray(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(5000); // 5 seconds timeout
        conn.setReadTimeout(5000);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                log.error("Failed request. HTTP status: {}", status);
                return null;
            }
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return new JSONArray(content.toString());
        } finally {
            conn.disconnect();
        }
    }
}
