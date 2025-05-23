package filtros;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CentroEducativoClient {
    private static final String LOGIN_URL = "http://localhost:9090/CentroEducativo/login";
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
                JsonPrimitive prim = je.getAsJsonPrimitive();
                return prim.getAsString();
            } else if (je.isJsonObject()) {
                JsonObject obj = je.getAsJsonObject();
                if (obj.has("key")) {
                    return obj.get("key").getAsString();
                } else if (obj.has("token")) {
                    return obj.get("token").getAsString();
                }
            }
            // Formato inesperado
            System.err.println("Formato de respuesta inesperado: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return null;
    }
}
