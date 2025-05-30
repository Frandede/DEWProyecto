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

@WebServlet("/profesores/asignaturas")
public class AsignaturasProfesorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("dni") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No estás autenticado.");
            return;
        }

        String dni = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");

        List<Asignatura> asignaturas = CentroEducativoClient.getAsignaturasDeProfesor(dni, key);

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                      + "<title>Asignaturas del Profesor</title>"
                      + "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
                      + "</head><body class='p-5'>");

            out.println("<h1>Asignaturas que imparte el profesor " + dni + "</h1>");

            if (asignaturas == null || asignaturas.isEmpty()) {
                out.println("<p class='text-danger'>No se encontraron asignaturas.</p>");
            } else {
                out.println("<ul class='list-group'>");
                for (Asignatura a : asignaturas) {
                    String acronimo = a.getAsignaturasDeProfesor();
                    out.printf("<li class='list-group-item'>%s</li>%n", acronimo);
                }
                out.println("</ul>");
            }

            out.println("<a href='" + req.getContextPath() + "/logout' class='btn btn-danger mt-3'>Cerrar sesión</a>");
            out.println("</body></html>");
        }
    }
}