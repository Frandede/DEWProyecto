package filtros;



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

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

        String asignatura = req.getParameter("asignatura");
        if (asignatura == null || asignatura.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro 'asignatura'");
            return;
        }

        // Aquí llamarías al cliente para obtener la nota real del alumno en esa asignatura
        // Por ejemplo:
        // Double nota = CentroEducativoClient.getNotaDeAlumnoEnAsignatura(dni, key, asignatura);
        // Para este ejemplo, lo simulamos con un valor fijo
        Double nota = 8.5;  // Valor ficticio

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                    + "<title>Nota de " + asignatura + "</title>"
                    + "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
                    + "</head><body class='p-5'>");

            out.println("<nav class='navbar navbar-light bg-light mb-4'>"
                    + "<div class='container-fluid'>"
                    + "<span class='navbar-text'>Usuario: " + dni + "</span>"
                    + "<a class='btn btn-outline-danger' href='" 
                    + req.getContextPath() + "/logout'>Logout</a>"
                    + "</div></nav>");

            out.println("<div class='container'>"
                    + "<h2>Nota en " + asignatura + "</h2>");

            if (nota == null) {
                out.println("<div class='alert alert-warning'>No se encontró la nota para esta asignatura.</div>");
            } else {
                out.println("<p>Tu nota en <strong>" + asignatura + "</strong> es: <span class='badge bg-success'>" + nota + "</span></p>");
            }

            out.println("<a href='" + req.getContextPath() + "/alumno/asignaturas' class='btn btn-secondary mt-3'>Volver a asignaturas</a>");
            out.println("</div></body></html>");
        }
    }
}