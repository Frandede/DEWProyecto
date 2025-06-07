package filtros;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/profesores/alumnos-por-asignatura")
public class AlumnosPorAsignaturaServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("dni") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String asignatura = req.getParameter("asignatura");
        String dniProfesor = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");

        System.out.println("Solicitando alumnos para: " + asignatura);

        List<AlumnoAsignatura> alumnos = CentroEducativoClient.getAlumnosPorAsignatura(asignatura, dniProfesor, key);

        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            if (alumnos == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"Error al obtener alumnos\"}");
            } else {
                out.print(new Gson().toJson(alumnos));
                System.out.println("Enviados " + alumnos.size() + " alumnos");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("dni") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Leer y parsear el JSON
        JsonObject jsonRequest = gson.fromJson(req.getReader(), JsonObject.class);
        String action = jsonRequest.get("action").getAsString();
        
        if ("update-grade".equals(action)) {
            String asignatura = jsonRequest.get("asignatura").getAsString();
            String dniAlumno = jsonRequest.get("dniAlumno").getAsString();
            Float nota = jsonRequest.get("nota").getAsFloat();
            String dniProfesor = jsonRequest.get("dniProfesor").getAsString();
            String key = (String) session.getAttribute("key");

            // Actualizar la nota
            boolean success = CentroEducativoClient.actualizarNota(
                asignatura, dniAlumno, nota, dniProfesor, key
            );

            // Enviar respuesta JSON
            resp.setContentType("application/json");
            JsonObject response = new JsonObject();
            response.addProperty("success", success);
            resp.getWriter().write(gson.toJson(response));
        }
    }
}