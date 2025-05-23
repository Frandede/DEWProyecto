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

@WebServlet("/alumno/asignaturas")
public class AsignaturasAlumnoServlet extends HttpServlet {
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

        List<Asignatura> asignaturas = CentroEducativoClient.getAsignaturasDeAlumno(dni, key);

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                      + "<title>Mis Asignaturas</title>"
                      + "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
                      + "</head><body class='p-5'>");
            out.println("<h1>Asignaturas de " + dni + "</h1>");

            if (asignaturas == null) {
                out.println("<p class='text-danger'>Error al recuperar asignaturas.</p>");
            } else if (asignaturas.isEmpty()) {
                out.println("<p>No tienes asignaturas inscritas.</p>");
            } else {
                out.println("<table class='table table-striped'>"
                          + "<thead><tr><th>Asignatura</th><th>Nota</th></tr></thead><tbody>");
                for (Asignatura a : asignaturas) {
                    out.printf("<tr><td>%s</td><td>%s</td></tr>%n",
                               a.getAsignaturasDeAlumno(),
                               (a.getNota() == null || a.getNota().isEmpty()) ? "—" : a.getNota());
                }
                out.println("</tbody></table>");
            }
            out.println("<a href='test' class='btn btn-secondary'>Volver</a>");
            out.println("</body></html>");
        }
    }
}
