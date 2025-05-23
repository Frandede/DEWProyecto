package filtros;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CentroEducativoClient {
    private static final String LOGIN_URL       = "http://localhost:9090/CentroEducativo/login";
    private static final String ASIGNATURAS_URL = "http://localhost:9090/CentroEducativo/alumnos/%s/asignaturas";
    private static final Gson gson = new Gson();

    /**
     * Llama al endpoint de login de CentroEducativo.
     * @param dni  Documento de identidad
     * @param pass Contraseña
     * @return La "key" devuelta por el servicio, o null si error
     */
    public static String login(String dni, String pass) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(LOGIN_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            // Construir y enviar JSON de petición
            JsonObject payload = new JsonObject();
            payload.addProperty("dni", dni);
            payload.addProperty("password", pass);
            byte[] body = gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = con.getOutputStream()) {
                os.write(body);
            }

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                System.err.println("HTTP " + status + " al logear en REST");
                return null;
            }

            // Leer la respuesta completa
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            String response = sb.toString().trim();

            // Parseo seguro: permite JSON primitivo o JSON objeto
            JsonElement je = gson.fromJson(response, JsonElement.class);
            if (je.isJsonPrimitive()) {
                return je.getAsJsonPrimitive().getAsString();
            } else if (je.isJsonObject()) {
                JsonObject obj = je.getAsJsonObject();
                if (obj.has("key"))   return obj.get("key").getAsString();
                if (obj.has("token")) return obj.get("token").getAsString();
            }
            System.err.println("Formato de respuesta inesperado: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.disconnect();
        }
        return null;
    }

    /**
     * Llama al endpoint de asignaturas de un alumno.
     * @param dni DNI del alumno
     * @param key Key de autenticación (Bearer)
     * @return Lista de Asignatura, o null si hay error
     */
    public static List<Asignatura> getAsignaturas(String dni, String key) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(String.format(ASIGNATURAS_URL, dni));
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + key);
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                System.err.println("HTTP " + status + " al pedir asignaturas");
                return null;
            }

            // Leer respuesta JSON
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }

            // Parsear JSON array a List<Asignatura>
            JsonArray array = gson.fromJson(sb.toString(), JsonArray.class);
            return gson.fromJson(array, new TypeToken<List<Asignatura>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) con.disconnect();
        }
    }
}
