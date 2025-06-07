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

            out.println("<nav class='navbar navbar-light bg-light mb-4'>"
                    + "<div class='container-fluid'>"
                    + "<span class='navbar-text'>Usuario: " + dni + "</span>"
                    + "<a class='btn btn-outline-danger' href='" 
                    + req.getContextPath() + "/logout'>Cerrar sesión</a>"
                    + "</div></nav>");

            out.println("<div class='container'>");
            out.println("<h1>Asignaturas de " + dni + "</h1>");

            if (asignaturas == null) {
                out.println("<p class='text-danger'>Error al recuperar asignaturas.</p>");
            } else if (asignaturas.isEmpty()) {
                out.println("<p>No tienes asignaturas inscritas.</p>");
            } else {
                out.println("<table class='table table-striped'>"
                        + "<thead><tr><th>Asignatura</th><th>GRUPO</th></tr></thead><tbody>");
                
                // Mostramos solo la primera fila con la lista del grupo
                out.println("<tr>");
                out.println("<td><a href='" + req.getContextPath() + "/alumno/nota?asignatura=" + 
                           asignaturas.get(0).getAsignaturasDeAlumno() + "'>" + 
                           asignaturas.get(0).getAsignaturasDeAlumno() + "</a></td>");
                
                // Celda con la lista del grupo (ocupa todas las filas)
                out.println("<td rowspan='" + (asignaturas.size() + 1) + "'>");
                out.println("<h4>GRUPO</h4>");
                out.println("<ol class='list-group list-group-numbered'>");
                out.println("<li class='list-group-item'>Jara Leal García</li>");
                out.println("<li class='list-group-item'>Pau Zaragozá Carrascosa</li>");
                out.println("<li class='list-group-item'>Aaron Montaraz Gómez</li>");
                out.println("<li class='list-group-item'>Lluis Colomar García</li>");
                out.println("<li class='list-group-item'>Fran de la Guía González</li>");
                out.println("<li class='list-group-item'>Raúl Medrano Llopis</li>");
                out.println("</ol>");
                out.println("</td>");
                out.println("</tr>");
                
                // Resto de asignaturas
                for (int i = 1; i < asignaturas.size(); i++) {
                    Asignatura a = asignaturas.get(i);
                    out.println("<tr>");
                    out.println("<td><a href='" + req.getContextPath() + "/alumno/nota?asignatura=" + 
                              a.getAsignaturasDeAlumno() + "'>" + 
                              a.getAsignaturasDeAlumno() + "</a></td>");
                    out.println("</tr>");
                }
                
                out.println("</tbody></table>");
            }

            // Botón para imprimir certificado
            out.println("<form method='GET' action='certificado' class='mb-3'>");
            out.println("<button type='submit' class='btn btn-primary'>Imprimir certificado</button>");
            out.println("</form>");

            out.println("</div></body></html>");
        }
    }

}