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
        
        String asignaturasMatriculadasStr = "No matriculado";
        if (asignaturas != null && !asignaturas.isEmpty()) {
            asignaturasMatriculadasStr = asignaturas.stream()
                .map(Asignatura::getAsignaturasDeAlumno)
                .collect(Collectors.joining(", "));
        }

        // 4. Construcción de la página HTML
        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
             out.println("<!DOCTYPE html>");
             out.println("<html lang='es'>");
             out.println("<head>");
             out.println("  <meta charset='UTF-8'>");
             out.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
             out.println("  <title>Ficha de " + nombreAlumno + "</title>");
             out.println("  <link rel='preconnect' href='https://fonts.googleapis.com'>");
             out.println("  <link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
             out.println("  <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap' rel='stylesheet'>");
             out.println("  <style>");
             // --- INICIO DE CSS ---
             out.println("    :root {");
             out.println("        --bg-start: #4A3F63; --bg-end: #634f80;");
             out.println("        --primary-color: #ffffff; --secondary-color: rgba(255, 255, 255, 0.75);");
             out.println("        --card-bg: rgba(255, 255, 255, 0.1); --card-border: rgba(255, 255, 255, 0.2);");
             out.println("        --shadow-color: rgba(0, 0, 0, 0.15);");
             out.println("        --button-bg: #ffffff; --button-text: #4A3F63; --button-bg-hover: #f0f0f0;");
             out.println("    }");
             out.println("    body, html { margin: 0; padding: 0; font-family: 'Poppins', sans-serif; }");
             out.println("    .main-container { width: 100%; min-height: 100vh; color: var(--primary-color); background: linear-gradient(160deg, var(--bg-start), var(--bg-end)); padding: 2rem; box-sizing: border-box; display: flex; justify-content: center; align-items: center; }");
             out.println("    .content-box { background: var(--card-bg); backdrop-filter: blur(15px); -webkit-backdrop-filter: blur(15px); border-radius: 20px; border: 1px solid var(--card-border); padding: 2.5rem 3rem; max-width: 900px; width: 100%; box-shadow: 0 8px 32px 0 var(--shadow-color); }");
             out.println("    .ficha-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 1.5rem; margin-bottom: 1.5rem; border-bottom: 1px solid var(--card-border); flex-wrap: wrap; gap: 1rem; }");
             out.println("    .ficha-header h2 { margin: 0; font-size: 2rem; }");
             out.println("    .ficha-header p { margin: 0; color: var(--secondary-color); }");
             out.println("    .btn-glass { display: inline-flex; align-items: center; gap: 0.5rem; padding: 0.8rem 1.5rem; background-color: var(--button-bg); color: var(--button-text) !important; border: none; border-radius: 12px; cursor: pointer; font-size: 0.9rem; font-weight: 600; text-decoration: none; text-align: center; transition: background-color 0.3s ease, transform 0.2s ease; }");
             out.println("    .btn-glass:hover { background-color: var(--button-bg-hover); transform: translateY(-3px); }");
             out.println("    .ficha-body { display: flex; gap: 2.5rem; }");
             out.println("    .photo-column { flex: 0 0 200px; text-align: center; }");
             out.println("    #student-photo { width: 100%; height: auto; border-radius: 15px; box-shadow: 0 4px 15px rgba(0,0,0,0.2); }");
             out.println("    .details-column { flex-grow: 1; text-align: left; }");
             out.println("    .details-column h4 { font-weight: 600; margin-top: 0; }");
             out.println("    .details-column p { color: var(--secondary-color); line-height: 1.6; }");
             out.println("    .subject-list { list-style: none; padding: 0; margin-top: 1rem; }");
             out.println("    .subject-list li { background: rgba(0,0,0,0.15); margin-bottom: 0.5rem; padding: 0.75rem; border-radius: 8px; font-size: 0.9rem; }");
             out.println("    @media (max-width: 768px) { .ficha-body { flex-direction: column; align-items: center; text-align: center; } .details-column { text-align: center; } }");
             // --- FIN DE CSS ---
             out.println("  </style>");
             out.println("</head>");
             out.println("<body>");
             out.println("  <div class='main-container'>");
             out.println("    <div class='content-box'>");

             out.println("      <div class='ficha-header'>");
             out.println("        <div>");
             out.println("          <h2>" + nombreAlumno + "</h2>");
             out.println("          <p>DNI: " + dniAlumno + "</p>");
             out.println("        </div>");
             out.println("        <a href='" + req.getContextPath() + "/profesores/asignaturas' class='btn-glass'>Volver a Mis Asignaturas</a>");
             out.println("      </div>");

             out.println("      <div class='ficha-body'>");
             out.println("        <div class='photo-column'>");
             out.println("          <img id='student-photo' src='" + req.getContextPath() + "/images/alumnos/" + dniAlumno + ".png' "
                       + "onerror=\"this.onerror=null;this.src='" + req.getContextPath() + "/images/perfilAnonimo.png'\" "
                       + "alt='Foto de " + nombreAlumno + "'>");
             out.println("        </div>");
             
             out.println("        <div class='details-column'>");
             out.println("          <h4>[Matriculad@ en: " + asignaturasMatriculadasStr + "]</h4>");
             out.println("          <p>");
             out.println("            Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                       + "Aenean commodo ligula eget dolor. Aenean massa. "
                       + "Cum sociis natoque penatibus et magnis dis parturient montes, "
                       + "nascetur ridiculus mus.");
             out.println("          </p>");
             
             out.println("          <h4 style='margin-top: 2rem;'>Asignaturas:</h4>");
             if (asignaturas == null || asignaturas.isEmpty()) {
                 out.println("<p>No hay asignaturas matriculadas.</p>");
             } else {
                 out.println("            <ul class='subject-list'>");
                 for (Asignatura a : asignaturas) {
                     out.println("              <li>" + a.getAsignaturasDeAlumno() + "</li>");
                 }
                 out.println("            </ul>");
             }
             out.println("        </div>");
             out.println("      </div>"); // Fin de ficha-body
             
             out.println("    </div>"); // Fin de content-box
             out.println("  </div>"); // Fin de main-container
             out.println("</body>");
             out.println("</html>");
        }
    }
}