package filtros;



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/alumno/nota")
public class NotaAsignaturaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("dni") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No estás autenticado");
            return;
        }

        String dni = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");
        String asignaturaParam = req.getParameter("asignatura");

        if (asignaturaParam == null || asignaturaParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro 'asignatura'");
            return;
        }

        List<Asignatura> asignaturas = CentroEducativoClient.getAsignaturasDeAlumno(dni, key);
        String nota = null;
        String nombreAsignatura = asignaturaParam; // Por defecto usamos el parámetro

        if (asignaturas != null) {
            for (Asignatura a : asignaturas) {
                if (asignaturaParam.equals(a.getAsignaturasDeAlumno())) {
                    nota = a.getNota();
                    // Si el objeto Asignatura tiene más campos, podríamos usarlos aquí
                    nombreAsignatura = a.getAsignaturasDeAlumno(); // O cualquier otro campo disponible
                    break;
                }
            }
        }

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                    + "<title>Nota de " + nombreAsignatura + "</title>"
                    + "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
                    + "</head><body class='p-5'>");

            out.println("<nav class='navbar navbar-light bg-light mb-4'>"
                    + "<div class='container-fluid'>"
                    + "<span class='navbar-text'>Usuario: " + dni + "</span>"
                    + "<a class='btn btn-outline-danger' href='" 
                    + req.getContextPath() + "/logout'>Cerrar sesión</a>"
                    + "</div></nav>");

            out.println("<div class='container'>");
            out.println("<h2>Detalles de " + nombreAsignatura + "</h2>");

            // Mostrar nota
            out.println("<div class='card mb-4'>");
            out.println("<div class='card-body'>");
            out.println("<h5 class='card-title'>Tu nota</h5>");
            
            if (nota == null || nota.isEmpty()) {
                out.println("<p class='card-text'>No tienes nota asignada aún en <strong>" + nombreAsignatura + "</strong></p>");
            } else {
                out.println("<p class='card-text'>Tu nota en <strong>" + nombreAsignatura + "</strong> es: <span class='badge bg-success'>" + nota + "</span></p>");
            }
            out.println("</div></div>");

            // Información básica que podemos mostrar sin acceso a detalles de profesor
            out.println("<div class='card'>");
            out.println("<div class='card-body'>");
            out.println("<h5 class='card-title'>Información básica</h5>");
            out.println("<ul class='list-group list-group-flush'>");
            out.println("<li class='list-group-item'><strong>Asignatura:</strong> " + nombreAsignatura + "</li>");
            out.println("<li class='list-group-item'><strong>Estado:</strong> " + (nota != null ? "Calificada" : "En curso") + "</li>");
            out.println("</ul>");
            out.println("</div></div>");

            out.println("<a href='" + req.getContextPath() + "/alumno/asignaturas' class='btn btn-secondary mt-3'>Volver a asignaturas</a>");
            out.println("</div></body></html>");
        }
    }
}