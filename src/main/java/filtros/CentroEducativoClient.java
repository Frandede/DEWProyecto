package filtros;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class CentroEducativoClient {
    private static final String BASE_URL = "http://localhost:9090/CentroEducativo";
    private static final Gson gson = new Gson();
    private static String sessionCookie;

    public static String login(String dni, String pass) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(BASE_URL + "/login");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            JsonObject payload = new JsonObject();
            payload.addProperty("dni", dni);
            payload.addProperty("password", pass);

            try (OutputStream os = con.getOutputStream()) {
                os.write(gson.toJson(payload).getBytes(StandardCharsets.UTF_8));
            }

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                System.err.println("Error en login: HTTP " + status);
                if (con.getErrorStream() != null) {
                    try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                        String errorLine;
                        System.err.println("Cuerpo del error del login:");
                        while ((errorLine = errorReader.readLine()) != null) {
                            System.err.println(errorLine);
                        }
                    }
                }
                return null;
            }

            sessionCookie = con.getHeaderField("Set-Cookie");
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String response = br.readLine();
                System.out.println("Respuesta del login (cuerpo): " + response);
                return response;
            }
        } catch (IOException e) {
            System.err.println("Error en login (IOException): " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) con.disconnect();
        }
    }

    public static List<Asignatura> getAsignaturasDeAlumno(String dni, String key) {
        HttpURLConnection con = null;
        try {
            String urlStr = String.format("%s/alumnos/%s/asignaturas?key=%s",
                BASE_URL,
                URLEncoder.encode(dni, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(key, StandardCharsets.UTF_8.name()));
            System.out.println("URL getAsignaturasDeAlumno: " + urlStr);
            return makeGetRequest(con, urlStr, new TypeToken<List<Asignatura>>(){}.getType());
        } catch (Exception e) {
            System.err.println("Error obteniendo asignaturas de alumno: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<AsignaturaProfesor> getAsignaturasDeProfesor(String dni, String key) {
        HttpURLConnection con = null;
        try {
            String urlStr = String.format("%s/profesores/%s/asignaturas?key=%s",
                BASE_URL,
                URLEncoder.encode(dni, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(key, StandardCharsets.UTF_8.name()));
            System.out.println("URL getAsignaturasDeProfesor: " + urlStr);
            return makeGetRequest(con, urlStr, new TypeToken<List<AsignaturaProfesor>>(){}.getType());
        } catch (Exception e) {
            System.err.println("Error obteniendo asignaturas de profesor: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<AlumnoAsignatura> getAlumnosPorAsignatura(String acronimo, String dniProfesor, String key) {
        System.out.println("Obteniendo alumnos para asignatura: " + acronimo + " (Profesor: " + dniProfesor + ", Usando Key: " + key +")");
        HttpURLConnection con = null;
        try {
            String urlStr = String.format("%s/asignaturas/%s/alumnos?key=%s",
                BASE_URL,
                URLEncoder.encode(acronimo, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(key, StandardCharsets.UTF_8.name()));
            
            System.out.println("URL de la solicitud a la API getAlumnosPorAsignatura: " + urlStr);

            List<Map<String, String>> rawAlumnos = makeGetRequest(con, urlStr, 
                new TypeToken<List<Map<String, String>>>(){}.getType());

            if (rawAlumnos == null || rawAlumnos.isEmpty()) {
                System.out.println("No se encontraron alumnos para la asignatura " + acronimo);
                return new ArrayList<>();
            }

            List<AlumnoAsignatura> alumnos = new ArrayList<>();
            for (Map<String, String> raw : rawAlumnos) {
                String dniAlumno = raw.get("alumno");
                Map<String, String> infoAlumno = getInfoAlumnoCompleto(dniAlumno, key);
                
                AlumnoAsignatura alumno = new AlumnoAsignatura();
                
                if (infoAlumno != null) {
                    String nombreCompleto = infoAlumno.getOrDefault("nombre", "") + " " + 
                                          infoAlumno.getOrDefault("apellidos", "");
                    alumno.setAdditions1Drop1(nombreCompleto.trim());
                } else {
                    alumno.setAdditions1Drop1("Alumno " + dniAlumno);
                }
                
                alumno.setAdditions1Drop2(dniAlumno);
                alumno.setAdditions1Drop3(raw.get("nota"));
                
                alumnos.add(alumno);
            }
            
            System.out.println("Alumnos encontrados y mapeados: " + alumnos.size());
            if (!alumnos.isEmpty()) {
                System.out.println("Detalle primer alumno mapeado: " + 
                    "Nombre=" + alumnos.get(0).getAdditions1Drop1() + 
                    ", DNI=" + alumnos.get(0).getAdditions1Drop2() + 
                    ", Nota=" + alumnos.get(0).getAdditions1Drop3());
            }
            return alumnos;

        } catch (Exception e) {
            System.err.println("Error en getAlumnosPorAsignatura: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) con.disconnect();
        }
    }

    public static boolean actualizarNota(String asignatura, String dniAlumno, String nota, String dniProfesor, String key) {
        HttpURLConnection con = null;
        try {
            String urlStr = String.format("%s/asignaturas/%s/alumnos/%s/nota",
                BASE_URL,
                URLEncoder.encode(asignatura, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(dniAlumno, StandardCharsets.UTF_8.name()));
            
            System.out.println("URL actualizarNota: " + urlStr);

            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + key);
            if (sessionCookie != null) {
                con.setRequestProperty("Cookie", sessionCookie);
            }
            con.setDoOutput(true);

            JsonObject payload = new JsonObject();
            payload.addProperty("nota", nota);
            payload.addProperty("profesor", dniProfesor);
            payload.addProperty("key", key);

            try (OutputStream os = con.getOutputStream()) {
                os.write(gson.toJson(payload).getBytes(StandardCharsets.UTF_8));
            }

            int status = con.getResponseCode();
            System.out.println("Respuesta de actualizarNota: " + status);
            if (status != HttpURLConnection.HTTP_OK) {
                System.err.println("Error actualizando nota, HTTP " + status);
                if (con.getErrorStream() != null) {
                    try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                        String errorLine;
                        System.err.println("Cuerpo del error de actualizarNota:");
                        while ((errorLine = errorReader.readLine()) != null) {
                            System.err.println(errorLine);
                        }
                    }
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando nota (Exception): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) con.disconnect();
        }
    }

    private static Map<String, String> getInfoAlumnoCompleto(String dni, String key) {
        HttpURLConnection con = null;
        try {
            String urlStr = String.format("%s/alumnos/%s?key=%s",
                BASE_URL,
                URLEncoder.encode(dni, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(key, StandardCharsets.UTF_8.name()));
            
            System.out.println("Obteniendo detalles del alumno: " + dni);
            return makeGetRequest(con, urlStr, new TypeToken<Map<String, String>>(){}.getType());
            
        } catch (Exception e) {
            System.err.println("Error obteniendo info de alumno " + dni + ": " + e.getMessage());
            return null;
        } finally {
            if (con != null) con.disconnect();
        }
    }

    public static Map<String, String> getInfoAlumno(String dni, String key) {
        return getInfoAlumnoCompleto(dni, key);
    }

    public static Map<String, String> getInfoProfesor(String dni, String key) {
        HttpURLConnection con = null;
        try {
            String urlStr = String.format("%s/profesores/%s?key=%s",
                BASE_URL,
                URLEncoder.encode(dni, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(key, StandardCharsets.UTF_8.name()));
            System.out.println("URL getInfoProfesor: " + urlStr);
            return makeGetRequest(con, urlStr, new TypeToken<Map<String, String>>(){}.getType());
        } catch (Exception e) {
            System.err.println("Error obteniendo info de profesor: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<Asignatura> getAllAsignaturas(String key) {
        HttpURLConnection con = null;
        try {
            String urlStr = String.format("%s/asignaturas?key=%s",
                BASE_URL,
                URLEncoder.encode(key, StandardCharsets.UTF_8.name()));
            System.out.println("URL getAllAsignaturas: " + urlStr);
            return makeGetRequest(con, urlStr, new TypeToken<List<Asignatura>>(){}.getType());
        } catch (Exception e) {
            System.err.println("Error obteniendo todas las asignaturas: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static <T> T makeGetRequest(HttpURLConnection con, String urlStr, Type type) throws IOException {
        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            if (sessionCookie != null) {
                con.setRequestProperty("Cookie", sessionCookie);
            }

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                System.err.println("HTTP Error en makeGetRequest: " + status + " en URL: " + urlStr);
                if (con.getErrorStream() != null) {
                    try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                        String errorLine;
                        System.err.println("Cuerpo del error (makeGetRequest):");
                        while ((errorLine = errorReader.readLine()) != null) {
                            System.err.println(errorLine);
                        }
                    }
                }
                return null;
            }
            
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                return gson.fromJson(br, type);
            }
        } finally {
            if (con != null) con.disconnect();
        }
    }
}