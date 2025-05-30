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

        List<AsignaturaProfesor> asignaturas = CentroEducativoClient.getAsignaturasDeProfesor(dni, key); 

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Asignaturas del Profesor</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<style>");
            out.println(".asignatura-card { border-left: 4px solid #3498db; border-radius: 0.5rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); transition: all 0.3s ease; margin-bottom: 1.5rem; }");
            out.println(".asignatura-card:hover { transform: translateY(-3px); box-shadow: 0 6px 12px rgba(0,0,0,0.15); }");
            out.println(".asignatura-header { border-bottom: 1px solid #eee; padding-bottom: 0.5rem; margin-bottom: 1rem; }");
            out.println(".asignatura-acronimo { font-size: 1.5rem; font-weight: bold; color: #2c3e50; }");
            out.println(".asignatura-creditos { background-color: #3498db; color: white; padding: 0.25rem 0.75rem; border-radius: 1rem; font-size: 0.9rem; }");
            out.println(".asignatura-title { font-size: 1.2rem; font-weight: 600; color: #2c3e50; }");
            out.println(".asignatura-meta { color: #6c757d; font-size: 0.9rem; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body class='bg-light p-5'>");
            
            out.println("<div class='container'>");
            out.println("<div class='card mb-4 border-0 shadow-sm'>");
            out.println("<div class='card-header bg-primary text-white'>");
            out.println("<h1 class='h4 mb-0'>Asignaturas que imparte</h1>");
            out.println("<p class='mb-0 small'>Profesor: " + dni + "</p>");
            out.println("</div>");
            out.println("<div class='card-body'>");

            if (asignaturas == null || asignaturas.isEmpty()) {
                out.println("<div class='alert alert-warning'>No se encontraron asignaturas asignadas.</div>");
            } else {
                out.println("<div class='row'>");
                for (AsignaturaProfesor a : asignaturas) {
                    out.println("<div class='col-md-6 col-lg-4 mb-4'>");
                    out.println("<div class='asignatura-card card h-100'>");
                    out.println("<div class='card-body'>");
                    
                    // Header con acrónimo y créditos
                    out.println("<div class='asignatura-header d-flex justify-content-between align-items-center'>");
                    out.println("<span class='asignatura-acronimo'>" + a.getAcronimo() + "</span>");
                    out.println("<span class='asignatura-creditos'>" + a.getCreditos() + " créditos</span>");
                    out.println("</div>");
                    
                    // Detalles de la asignatura
                    out.println("<h3 class='asignatura-title card-title'>" + a.getNombre() + "</h3>");
                    out.println("<div class='asignatura-meta card-text mb-3'>");
                    out.println("<span class='me-3'><i class='bi bi-bookmark'></i> " + a.getCurso() + "º curso</span>");
                    out.println("<span><i class='bi bi-calendar'></i> " + a.getCuatrimestre() + "</span>");
                    out.println("</div>");
                    
                    // Botón de acción
                    out.println("<a href='" + req.getContextPath() + "/profesor/alumnos?asignatura=" + a.getAcronimo() + "' class='btn btn-success btn-sm'>Ver Alumnos</a>");
                    
                    out.println("</div>"); // cierre card-body
                    out.println("</div>"); // cierre asignatura-card
                    out.println("</div>"); // cierre col
                }
                out.println("</div>"); // cierre row
            }

            out.println("</div>"); // cierre card-body
            out.println("<div class='card-footer bg-transparent text-center'>");
            out.println("<a href='" + req.getContextPath() + "/logout' class='btn btn-danger'>Cerrar sesión</a>");
            out.println("</div>");
            out.println("</div>"); // cierre card
            out.println("</div>"); // cierre container
            
            // Bootstrap JS y dependencias
            out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}