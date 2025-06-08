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

@WebServlet("/alumno/certificado")
public class CertificadoServlet extends HttpServlet {
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
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("  <meta charset='UTF-8'>");
            out.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("  <title>Certificado Académico - " + dni + "</title>");
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
            out.println("    .main-container { width: 100%; min-height: 100vh; color: var(--primary-color); background: linear-gradient(160deg, var(--bg-start), var(--bg-end)); padding: 2rem; box-sizing: border-box; display: flex; justify-content: center; align-items: flex-start; }");
            out.println("    .content-box { background: var(--card-bg); backdrop-filter: blur(15px); -webkit-backdrop-filter: blur(15px); border-radius: 20px; border: 1px solid var(--card-border); padding: 2.5rem 3rem; max-width: 800px; width: 100%; box-shadow: 0 8px 32px 0 var(--shadow-color); margin-top: 2rem; }");
            out.println("    .certificate-header { text-align: center; padding-bottom: 1.5rem; margin-bottom: 1.5rem; border-bottom: 1px solid var(--card-border); }");
            out.println("    .certificate-header h1 { font-size: 2.5rem; font-weight: 700; margin: 0; }");
            out.println("    .certificate-header p { margin: 0.5rem 0 1.5rem 0; color: var(--secondary-color); font-size: 1.1rem; }");
            out.println("    .student-photo { width: 150px; height: 150px; border-radius: 50%; object-fit: cover; border: 3px solid var(--card-border); box-shadow: 0 4px 15px rgba(0,0,0,0.2); }");
            out.println("    .certificate-table { width: 100%; border-collapse: collapse; }");
            out.println("    .certificate-table th, .certificate-table td { padding: 1rem; text-align: left; border-bottom: 1px solid var(--card-border); }");
            out.println("    .certificate-table th { font-weight: 600; font-size: 1.1rem; }");
            out.println("    .certificate-table tr:last-child td { border-bottom: none; }");
            out.println("    .actions-footer { text-align: center; margin-top: 2rem; }");
            out.println("    .btn-glass { display: inline-flex; align-items: center; gap: 0.5rem; padding: 0.8rem 1.5rem; background-color: var(--button-bg); color: var(--button-text) !important; border: none; border-radius: 12px; cursor: pointer; font-size: 0.9rem; font-weight: 600; text-decoration: none; text-align: center; transition: background-color 0.3s ease, transform 0.2s ease; margin: 0 0.5rem; }");
            out.println("    .btn-glass:hover { background-color: var(--button-bg-hover); transform: translateY(-3px); }");
            
            // --- ESTILOS DE IMPRESIÓN ---
            out.println("    @media print {");
            out.println("      body { -webkit-print-color-adjust: exact; }");
            out.println("      .no-print { display: none !important; }");
            out.println("      .main-container { background: none !important; padding: 0; }");
            out.println("      .content-box { background: none !important; border: 1px solid #000 !important; box-shadow: none !important; backdrop-filter: none !important; color: black !important; }");
            out.println("      h1, p, th, td { color: black !important; }");
            out.println("      .certificate-table, .certificate-table th, .certificate-table td { border-color: #666 !important; }");
            out.println("      .student-photo { border-color: #000 !important; }");
            out.println("    }");
            // --- FIN DE CSS ---
            out.println("  </style>");
            out.println("</head>");
            out.println("<body>");
            out.println("  <div class='main-container'>");
            out.println("    <div class='content-box'>");

            out.println("      <div class='certificate-header'>");
            out.println("        <h1>Certificado Académico</h1>");
            out.println("        <p>DNI del alumno: <strong>" + dni + "</strong></p>");
            out.println("        <img src='" + req.getContextPath() + "/images/alumnos/" + dni + ".png' "
                      + "class='student-photo' alt='Foto del alumno' "
                      + "onerror=\"this.onerror=null;this.src='" + req.getContextPath() + "/images/perfilAnonimo.png'\">");
            out.println("      </div>");

            if (asignaturas == null || asignaturas.isEmpty()) {
                out.println("<p style='text-align:center;'>No se encontraron asignaturas registradas.</p>");
            } else {
                out.println("<table class='certificate-table'><thead><tr><th>Asignatura</th><th>Nota</th></tr></thead><tbody>");
                for (Asignatura a : asignaturas) {
                    out.printf("<tr><td>%s</td><td>%s</td></tr>%n",
                               a.getAsignaturasDeAlumno(),
                               (a.getNota() == null || a.getNota().isEmpty() || a.getNota().equalsIgnoreCase("Sin calificar")) ? "—" : a.getNota());
                }
                out.println("</tbody></table>");
            }

            out.println("<div class='actions-footer no-print'>");
            out.println("  <button onclick='window.print()' class='btn-glass'>Imprimir Certificado</button>");
            out.println("  <a href='" + req.getContextPath() + "/alumno/asignaturas' class='btn-glass'>Volver a Asignaturas</a>");
            out.println("</div>");
            
            out.println("    </div>"); // .content-box
            out.println("  </div>");   // .main-container
            out.println("</body>");
            out.println("</html>");
        }
    }
}