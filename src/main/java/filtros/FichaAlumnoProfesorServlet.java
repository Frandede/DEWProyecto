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
import java.util.stream.Collectors;

@WebServlet("/profesor/ficha-alumno")
public class FichaAlumnoProfesorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 1. Autorización: Se comprueba la sesión del PROFESOR
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("dni") == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión como profesor para acceder a esta página");
            return;
        }

        String keyProfesor = (String) session.getAttribute("key");

        // 2. Obtención de datos del ALUMNO
        String dniAlumno = req.getParameter("dni");
        if (dniAlumno == null || dniAlumno.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro DNI del alumno");
            return;
        }

        String nombreAlumno = (String) CentroEducativoClient.getAlumnoPorDNI(dniAlumno, keyProfesor);
        List<Asignatura> asignaturas = CentroEducativoClient.getAsignaturasDeAlumno(dniAlumno, keyProfesor);
        
        // --> 3. Lógica para replicar el texto "[Matriculad@ en: ...]" dinámicamente
        String asignaturasMatriculadasStr = "No matriculado";
        if (asignaturas != null && !asignaturas.isEmpty()) {
            asignaturasMatriculadasStr = asignaturas.stream()
                .map(Asignatura::getAsignaturasDeAlumno) // Usamos el mismo método que en el servlet del alumno
                .collect(Collectors.joining(", "));
        }

        // 4. Construcción de la página HTML (copiada de FichaAlumnoServlet y adaptada)
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

             // --> Header: Idéntico al del alumno, usando los datos obtenidos
             out.println("      <div class='card-header bg-white d-flex justify-content-between align-items-center'>");
             out.println("        <div>");
             out.println("          <h4 class='mb-0 text-primary'>" + nombreAlumno + "</h4>");
             out.println("          <small class='text-muted'>DNI: " + dniAlumno + "</small>");
             out.println("        </div>");
             // --> Botón: Se mantiene el texto, pero se cambia el enlace para que sea útil al profesor
             //     Enlazar a /alumno/asignaturas daría un error 403. Lo lógico es volver a la vista del profesor.
             out.println("        <a href='" + req.getContextPath() + "/profesores/asignaturas' "
                       + "class='btn btn-outline-primary btn-sm'>Volver a Mis Asignaturas</a>");
             out.println("      </div>");

             out.println("      <div class='card-body'>");
             out.println("        <div class='row gx-4 gy-3'>");
             
             // --> Columna de la foto: Se mantiene la estructura exacta
             out.println("          <div class='col-md-4 text-center'>");
             out.println("            <img src='" + req.getContextPath() + "/images/alumnos/" + dniAlumno + ".png' "
                       + "class='img-fluid rounded mb-3' alt='Foto de " + nombreAlumno + "'>");
             // --> Nota: El servlet original mostraba AMBAS imágenes. Esto es probablemente un error.
             //     Para replicar el comportamiento, lo dejamos, pero lo ideal sería usar un 'onerror' en la primera imagen.
             out.println("            <div>");
             out.println("              <img src='" + req.getContextPath() + "/images/perfilAnonimo.png' "
                       + "class='img-thumbnail' style='max-width:100px; max-height:100px;' "
                       + "alt='Perfil Anónimo'>");
             out.println("            </div>");
             out.println("          </div>");
             
             // --> Columna de detalles: Se mantiene la estructura exacta
             out.println("          <div class='col-md-8'>");
             out.println("            <p class='fw-bold mb-2'>[Matriculad@ en: " + asignaturasMatriculadasStr + "]</p>");
             out.println("            <p class='text-justify'>");
             out.println("              Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                       + "Aenean commodo ligula eget dolor. Aenean massa. "
                       + "Cum sociis natoque penatibus et magnis dis parturient montes, "
                       + "nascetur ridiculus mus.");
             out.println("            </p>");
             out.println("            <hr>");
             
             // --> Listado de asignaturas: Se muestra la misma lista, pero SIN enlaces.
             //     Esto es CRÍTICO: Los enlaces originales apuntaban a /alumno/nota, que daría un error 403 al profesor.
             //     Se muestra la misma INFORMACIÓN, pero se adapta la FUNCIONALIDAD al rol del profesor.
             out.println("            <h6 class='fw-bold'>Asignaturas matriculadas:</h6>");
             if (asignaturas == null || asignaturas.isEmpty()) {
                 out.println("<p class='text-muted'>No hay asignaturas matriculadas.</p>");
             } else {
                 out.println("            <ul class='list-group list-group-flush'>");
                 for (Asignatura a : asignaturas) {
                     String acr = a.getAsignaturasDeAlumno();
                     out.println("              <li class='list-group-item p-2'>" + acr + "</li>"); // Se elimina la etiqueta <a>
                 }
                 out.println("            </ul>");
             }
             out.println("          </div>");
             out.println("        </div>");
             out.println("      </div>");
             out.println("    </div>");
             out.println("  </div>");
             out.println("  <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>");
             out.println("</body>");
             out.println("</html>");
        }
    }
}