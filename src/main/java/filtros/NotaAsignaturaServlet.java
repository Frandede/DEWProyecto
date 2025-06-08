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

        if (asignaturas != null) {
            for (Asignatura a : asignaturas) {
                if (asignaturaParam.equals(a.getAsignaturasDeAlumno())) {
                    nota = a.getNota();
                    break;
                }
            }
        }

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>Nota de " + asignaturaParam + "</title>");
            
            out.println("    <!-- Google Fonts: Poppins -->");
            out.println("    <link rel='preconnect' href='https://fonts.googleapis.com'>");
            out.println("    <link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
            out.println("    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700;800&display=swap' rel='stylesheet'>");
            
            out.println("    <style>");
            // --- INICIO DE CSS PERSONALIZADO ---
            
            out.println("    :root {");
            out.println("        --bg-start: #4A3F63;");
            out.println("        --bg-end: #634f80;");
            out.println("        --primary-color: #ffffff;");
            out.println("        --secondary-color: rgba(255, 255, 255, 0.75);");
            out.println("        --card-bg: rgba(255, 255, 255, 0.1);");
            out.println("        --card-border: rgba(255, 255, 255, 0.2);");
            out.println("        --shadow-color: rgba(0, 0, 0, 0.15);");
            out.println("        --button-bg: #ffffff;");
            out.println("        --button-text: #4A3F63;");
            out.println("        --button-bg-hover: #f0f0f0;");
            out.println("        /* Colores para notas */");
            out.println("        --fail-color: #dc3545;");
            out.println("        --pass-color: #3b82f6;");
            out.println("        --good-color: #16a34a;");
            out.println("        --excel-color: #c026d3;");
            out.println("        --unrated-color: #6b7280;");
            out.println("    }");

            out.println("    body, html { margin: 0; padding: 0; font-family: 'Poppins', sans-serif; }");

            out.println("    .main-container {");
            out.println("        width: 100%; min-height: 100vh; color: var(--primary-color);");
            out.println("        background: linear-gradient(160deg, var(--bg-start), var(--bg-end));");
            out.println("        display: flex; justify-content: center; align-items: center;");
            out.println("        padding: 2rem; box-sizing: border-box; text-align: center;");
            out.println("    }");

            out.println("    .content-box {");
            out.println("        background: var(--card-bg); backdrop-filter: blur(15px); -webkit-backdrop-filter: blur(15px);");
            out.println("        border-radius: 20px; border: 1px solid var(--card-border);");
            out.println("        padding: 2.5rem 3rem; max-width: 500px; width: 100%;");
            out.println("        box-shadow: 0 8px 32px 0 var(--shadow-color);");
            out.println("    }");

            out.println("    h1 { font-size: 2.2rem; font-weight: 600; margin-top: 0; margin-bottom: 0.5rem; }");
            out.println("    .user-info { color: var(--secondary-color); margin-bottom: 2rem; }");

            // --- ESTILO PARA EL CÍRCULO DE LA NOTA ---
            out.println("    .grade-display {");
            out.println("        width: 200px; height: 200px; margin: 2rem auto;");
            out.println("        border-radius: 50%; display: flex; flex-direction: column;");
            out.println("        justify-content: center; align-items: center;");
            out.println("        color: white; transition: background-color 0.3s ease;");
            out.println("    }");
            out.println("    .grade-number { font-size: 5rem; font-weight: 800; line-height: 1; }");
            out.println("    .grade-label { font-size: 1rem; font-weight: 400; text-transform: uppercase; letter-spacing: 1px; }");
            
            // --- COLORES DINÁMICOS PARA LA NOTA ---
            out.println("    .is-fail { background-color: var(--fail-color); }");
            out.println("    .is-pass { background-color: var(--pass-color); }");
            out.println("    .is-good { background-color: var(--good-color); }");
            out.println("    .is-excel { background-color: var(--excel-color); }");
            out.println("    .is-unrated { background-color: var(--unrated-color); }");
            out.println("    .is-unrated .grade-number { font-size: 1.5rem; font-weight: 600; }");

            out.println("    .actions-footer { margin-top: 2rem; display: flex; flex-direction: column; gap: 1rem; align-items: center; }");

            out.println("    .btn-glass {");
            out.println("        display: inline-block; padding: 0.8rem 1.5rem;");
            out.println("        background-color: var(--button-bg); color: var(--button-text) !important;");
            out.println("        border: none; border-radius: 12px; cursor: pointer;");
            out.println("        font-family: 'Poppins', sans-serif; font-size: 1rem; font-weight: 600;");
            out.println("        text-decoration: none; text-align: center;");
            out.println("        transition: background-color 0.3s ease, transform 0.2s ease;");
            out.println("    }");
            out.println("    .btn-glass:hover { background-color: var(--button-bg-hover); transform: translateY(-3px); }");

            out.println("    @media (max-width: 480px) { .content-box { padding: 2rem 1.5rem; } h1 { font-size: 1.8rem; } .grade-display { width: 160px; height: 160px; } .grade-number { font-size: 4rem; } }");

            out.println("    </style>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='main-container'>");
            out.println("        <div class='content-box'>");
            
            out.println("<h1>" + asignaturaParam + "</h1>");
            out.println("<p class='user-info'>Estudiante: " + dni + "</p>");

            // Lógica para determinar la clase de color de la nota
            String gradeClass = "is-unrated";
            String gradeLabel = "Sin Calificar";
            String gradeText = "S/C";

            if (nota != null && !nota.trim().isEmpty()) {
                try {
                    float notaNum = Float.parseFloat(nota.replace(',', '.'));
                    gradeText = String.format("%.1f", notaNum);
                    if (notaNum < 5) {
                        gradeClass = "is-fail";
                        gradeLabel = "Suspenso";
                    } else if (notaNum < 7) {
                        gradeClass = "is-pass";
                        gradeLabel = "Aprobado";
                    } else if (notaNum < 9) {
                        gradeClass = "is-good";
                        gradeLabel = "Notable";
                    } else {
                        gradeClass = "is-excel";
                        gradeLabel = "Excelente";
                    }
                } catch (NumberFormatException e) {
                    // Si la nota no es un número, se mantiene como "Sin Calificar"
                }
            }

            out.println("<div class='grade-display " + gradeClass + "'>");
            out.println("    <span class='grade-number'>" + gradeText + "</span>");
            out.println("    <span class='grade-label'>" + gradeLabel + "</span>");
            out.println("</div>");
            
            out.println("<div class='actions-footer'>");
            out.println("    <a href='" + req.getContextPath() + "/alumno/asignaturas' class='btn-glass'>Volver a Mis Asignaturas</a>");
            out.println("    <a href='" + req.getContextPath() + "/logout' class='btn-glass' style='background-color: transparent; border: 1px solid var(--button-bg); color: var(--button-bg) !important;'>Cerrar sesión</a>");
            out.println("</div>");

            out.println("        </div>");
            out.println("    </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}