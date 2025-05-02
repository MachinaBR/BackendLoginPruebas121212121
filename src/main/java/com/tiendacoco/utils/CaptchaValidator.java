package com.tiendacoco.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.util.HashMap;
import java.util.Map;

public class CaptchaValidator {

    private static final String SECRET_KEY = "6LeDNisrAAAAAKDspbngg9np7iJ3AJ4sRApkrMwE";

    public static boolean validarCaptcha(String token) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://www.google.com/recaptcha/api/siteverify"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "secret=" + SECRET_KEY + "&response=" + token))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.body());
            return json.get("success").asBoolean();
        } catch (Exception e) {
            System.out.println("❌ Error validando captcha: " + e.getMessage());
            return false;
        }
    }
}
