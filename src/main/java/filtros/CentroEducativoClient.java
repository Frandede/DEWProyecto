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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CentroEducativoClient {
    private static final String LOGIN_URL       = "http://localhost:9090/CentroEducativo/login";
    private static final String ASIGNATURAS_URL = 
        "http://localhost:9090/CentroEducativo/alumnos/%s/asignaturas?key=%s";
    private static final Gson gson = new Gson();

    // Aquí guardaremos la cookie de sesión tras el login
    private static String sessionCookie;

    /**
     * Llama al endpoint de login de CentroEducativo,
     * captura la cookie de sesión (JSESSIONID) y devuelve el key.
     */
    public static String login(String dni, String pass) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(LOGIN_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            // Envío JSON con credenciales
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

            // Capturar cookie de sesión
            Map<String, List<String>> headers = con.getHeaderFields();
            List<String> cookies = headers.get("Set-Cookie");
            if (cookies != null) {
                // Tomamos la primera cookie (por ejemplo: JSESSIONID=XYZ; Path=/CentroEducativo; HttpOnly)
                sessionCookie = cookies.get(0).split(";", 2)[0];
                System.out.println("Guardada cookie de sesión: " + sessionCookie);
            }

            // Leemos el body para extraer el key
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            String resp = sb.toString().trim();
            JsonElement je = gson.fromJson(resp, JsonElement.class);
            if (je.isJsonPrimitive()) {
                return je.getAsJsonPrimitive().getAsString();
            } else if (je.isJsonObject()) {
                JsonObject obj = je.getAsJsonObject();
                if (obj.has("key"))   return obj.get("key").getAsString();
                if (obj.has("token")) return obj.get("token").getAsString();
            }
            System.err.println("Formato de respuesta inesperado: " + resp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.disconnect();
        }
        return null;
    }

    /**
     * Llama al endpoint de asignaturas de un alumno,
     * enviando la key por query y reenviando la cookie de sesión.
     */
    public static List<Asignatura> getAsignaturasDeAlumno(String dni, String key) {
        HttpURLConnection con = null;
        try {
            String urlStr = String.format(ASIGNATURAS_URL,
                                          URLEncoder.encode(dni, StandardCharsets.UTF_8),
                                          URLEncoder.encode(key, StandardCharsets.UTF_8));
            System.out.println("GET Asignaturas URL: " + urlStr);

            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            // Reenviamos la cookie de sesión que arrancamos en login()
            if (sessionCookie != null) {
                con.setRequestProperty("Cookie", sessionCookie);
                System.out.println("Enviando cookie: " + sessionCookie);
            }

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                // Leer cuerpo de error
                try (BufferedReader err = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sbErr = new StringBuilder();
                    String line;
                    while ((line = err.readLine()) != null) {
                        sbErr.append(line).append('\n');
                    }
                    System.err.println("Error body: " + sbErr);
                }
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

            JsonArray array = gson.fromJson(sb.toString(), JsonArray.class);
            return gson.fromJson(array, new TypeToken<List<Asignatura>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) con.disconnect();
        }
    }
    
    
    public static List<Asignatura> getAsignaturasDeProfesor(String dni, String key) {
        HttpURLConnection con = null;
        try {
            String urlStr = String.format("http://localhost:9090/CentroEducativo/profesores/%s/asignaturas?key=%s",
                                          URLEncoder.encode(dni, StandardCharsets.UTF_8),
                                          URLEncoder.encode(key, StandardCharsets.UTF_8));
            System.out.println("GET Asignaturas Profesor URL: " + urlStr);

            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            if (sessionCookie != null) {
                con.setRequestProperty("Cookie", sessionCookie);
                System.out.println("Enviando cookie: " + sessionCookie);
            }

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                try (BufferedReader err = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sbErr = new StringBuilder();
                    String line;
                    while ((line = err.readLine()) != null) {
                        sbErr.append(line).append('\n');
                    }
                    System.err.println("Error body: " + sbErr);
                }
                System.err.println("HTTP " + status + " al pedir asignaturas del profesor");
                return null;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }

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
