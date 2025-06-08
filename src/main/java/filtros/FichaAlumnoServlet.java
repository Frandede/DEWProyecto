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

@WebServlet("/alumno/ficha")
public class FichaAlumnoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1) Comprobamos sesión
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("dni") == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión");
            return;
        }
        String key    = (String) session.getAttribute("key");
        String dni = req.getParameter("dni");
        String name = (String) CentroEducativoClient.getAlumnoPorDNI(dni, key);
        List<Asignatura> asignaturas = CentroEducativoClient.getAsignaturasDeAlumno(dni, key);
        if (dni == null || dni.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro dni");
            return;
        }

       
        // 3) Construimos la página HTML con Bootstrap
        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
        	 out.println("<!DOCTYPE html>");
        	    out.println("<html lang='es'>");
        	    out.println("<head>");
        	    out.println("  <meta charset='UTF-8'>");
        	    out.println("  <meta name='viewport' content='width=device-width, initial-scale=1, shrink-to-fit=no'>");
        	    out.println("  <title>Ficha de Alumno</title>");
        	    out.println("  <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
        	    out.println("</head>");
        	    out.println("<body class='bg-light'>");

        	    out.println("  <div class='container py-5'>");
        	    out.println("    <div class='card shadow'>");

        	    // Header con título y botón
        	    out.println("      <div class='card-header bg-white d-flex justify-content-between align-items-center'>");
        	    out.println("        <div>");
        	    out.println("          <h4 class='mb-0 text-primary'>" + name + "</h4>");
        	    out.println("          <small class='text-muted'>DNI: " + dni + "</small>");
        	    out.println("        </div>");
        	    out.println("        <a href='" + req.getContextPath() + "/alumno/asignaturas?dni=" + dni + "' "
        	              + "class='btn btn-outline-primary btn-sm'>Ver todas las asignaturas</a>");
        	    out.println("      </div>");

        	    // Body con imagen y contenido
        	    out.println("      <div class='card-body'>");
        	    out.println("        <div class='row gx-4 gy-3'>");

        	    // Columna de la foto
        	    out.println("          <div class='col-md-4 text-center'>");
        	    out.println("            <img src='" + req.getContextPath() + "/images/alumnos/" + dni + ".png' "
        	              + "class='img-fluid rounded mb-3' alt='Foto de " + name + "'>");
        	    // Imagen perfil anónimo
        	    out.println("            <div>");
        	    out.println("              <img src='" + req.getContextPath() + "/images/perfilAnonimo.png' "
        	              + "class='img-thumbnail' style='max-width:100px; max-height:100px;' "
        	              + "alt='Perfil Anónimo'>");
        	    out.println("            </div>");
        	    out.println("          </div>");

        	    // Columna de detalles
        	    out.println("          <div class='col-md-8'>");
        	    out.println("            <p class='fw-bold mb-2'>[Matriculad@ en: DCU, DSW]</p>");
        	    out.println("            <p class='text-justify'>");
        	    out.println("              Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
        	              + "Aenean commodo ligula eget dolor. Aenean massa. "
        	              + "Cum sociis natoque penatibus et magnis dis parturient montes, "
        	              + "nascetur ridiculus mus.");
        	    out.println("            </p>");
        	    out.println("            <hr>");

        	    // Listado de asignaturas
        	    out.println("            <h6 class='fw-bold'>Asignaturas matriculadas:</h6>");
        	    out.println("            <ul class='list-group list-group-flush'>");
        	    for (Asignatura a : asignaturas) {
        	        String acr = a.getAsignaturasDeAlumno();
        	        out.println("              <li class='list-group-item p-2'>"
        	                  + "<a href='" + req.getContextPath()
        	                  + "/alumno/nota?asignatura=" + acr + "' class='text-decoration-none'>"
        	                  + acr
        	                  + "</a>"
        	                  + "</li>");
        	    }
        	    out.println("            </ul>");

        	    out.println("          </div>");  // .col-md-8
        	    out.println("        </div>");    // .row
        	    out.println("      </div>");      // .card-body

        	    out.println("    </div>");        // .card
        	    out.println("  </div>");          // .container

        	    out.println("  <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>");
        	    out.println("</body>");
        	    out.println("</html>");
        }
    }
}
