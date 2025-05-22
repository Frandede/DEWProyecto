package filtros;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CentroEducativoClient {

    public static String login(String dni, String pass) {
        try {
            URL url = new URL("http://localhost:9090/CentroEducativo/login");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String json = "{\"dni\":\"" + dni + "\", \"password\":\"" + pass + "\"}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            if (con.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String response = br.lines().collect(Collectors.joining());

                    //Gson pa sacar el key
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                    return jsonResponse.get("key").getAsString();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
