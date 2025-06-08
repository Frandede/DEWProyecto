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

        // La lógica para obtener los datos no cambia
        List<Asignatura> asignaturas = CentroEducativoClient.getAsignaturasDeAlumno(dni, key);

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>Mis Asignaturas - " + dni + "</title>");
            
            out.println("    <!-- Google Fonts: Poppins -->");
            out.println("    <link rel='preconnect' href='https://fonts.googleapis.com'>");
            out.println("    <link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
            out.println("    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap' rel='stylesheet'>");
            
            out.println("    <style>");
            // --- INICIO DE CSS MODIFICADO ---
            
            out.println("    :root {");
            out.println("        --bg-start: #4A3F63; /* Púrpura oscuro */");
            out.println("        --bg-end: #634f80;   /* Tono púrpura medio */");
            out.println("        --primary-color: #ffffff;");
            out.println("        --secondary-color: rgba(255, 255, 255, 0.75);");
            out.println("        --card-bg: rgba(255, 255, 255, 0.1);");
            out.println("        --card-border: rgba(255, 255, 255, 0.2);");
            out.println("        --input-bg: rgba(255, 255, 255, 0.12);");
            out.println("        --shadow-color: rgba(0, 0, 0, 0.15);");
            out.println("        --button-bg: #ffffff;");
            out.println("        --button-text: #4A3F63;");
            out.println("        --button-bg-hover: #f0f0f0;");
            out.println("    }");

            out.println("    body, html {");
            out.println("        margin: 0; padding: 0; font-family: 'Poppins', sans-serif;");
            out.println("    }");

            out.println("    .main-container {");
            out.println("        width: 100%; min-height: 100vh; color: var(--primary-color);");
            out.println("        background: linear-gradient(160deg, var(--bg-start), var(--bg-end));");
            out.println("        display: flex; justify-content: center; align-items: flex-start;");
            out.println("        padding: 4rem 2rem; box-sizing: border-box;");
            out.println("    }");
            
            out.println("    .content-box {");
            out.println("        background: var(--card-bg); backdrop-filter: blur(15px); -webkit-backdrop-filter: blur(15px);");
            out.println("        border-radius: 20px; border: 1px solid var(--card-border);");
            out.println("        padding: 2.5rem 3rem; max-width: 1000px; width: 100%;");
            out.println("        box-shadow: 0 8px 32px 0 var(--shadow-color);");
            out.println("    }");
            
            out.println("    .navbar-custom {");
            out.println("        display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;");
            out.println("        margin-bottom: 2rem; padding: 1rem; border-radius: 15px;");
            out.println("        background: rgba(255, 255, 255, 0.08); border: 1px solid var(--card-border);");
            out.println("    }");

            out.println("    .navbar-custom .user-info { font-weight: 600; font-size: 1.1rem; }");
            out.println("    .navbar-custom .nav-actions { display: flex; gap: 1rem; }");

            // --- ESTILO DE BOTÓN UNIFICADO (COMO EL LOGIN) ---
            out.println("    .btn-glass {");
            out.println("        display: inline-block;");
            out.println("        padding: 0.8rem 1.5rem;");
            out.println("        background-color: var(--button-bg);");
            out.println("        color: var(--button-text) !important;");
            out.println("        border: none;");
            out.println("        border-radius: 12px;");
            out.println("        cursor: pointer;");
            out.println("        font-family: 'Poppins', sans-serif;");
            out.println("        font-size: 0.9rem;");
            out.println("        font-weight: 600;");
            out.println("        text-decoration: none;");
            out.println("        text-align: center;");
            out.println("        transition: background-color 0.3s ease, transform 0.2s ease;");
            out.println("    }");

            out.println("    .btn-glass:hover {");
            out.println("        background-color: var(--button-bg-hover);");
            out.println("        transform: translateY(-3px);");
            out.println("    }");

            out.println("    h1 { font-size: 2.5rem; font-weight: 700; margin-bottom: 2rem; text-align: center; }");

            out.println("    .content-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 2rem; }");
            
            out.println("    .table-container, .group-container {");
            out.println("        background: var(--input-bg); /* Usamos un fondo ligeramente diferente como en los inputs */");
            out.println("        border-radius: 15px; padding: 1.5rem; border: 1px solid var(--card-border);");
            out.println("    }");
            
            out.println("    table { width: 100%; border-collapse: collapse; }");
            out.println("    th, td { padding: 1rem; text-align: left; border-bottom: 1px solid var(--card-border); }");
            out.println("    th { font-weight: 600; font-size: 1.1rem; color: var(--primary-color); }");
            out.println("    tr:last-child td { border-bottom: none; }");
            out.println("    td a { color: var(--primary-color); text-decoration: none; font-weight: 400; transition: color 0.2s; }");
            out.println("    td a:hover { color: var(--secondary-color); text-decoration: underline; }");
            
            out.println("    h4 { font-weight: 600; margin-top: 0; margin-bottom: 1rem; font-size: 1.2rem; }");
            out.println("    .group-list { list-style: none; padding-left: 0; }");
            out.println("    .group-list li {");
            out.println("        background: rgba(0,0,0,0.15); margin-bottom: 0.5rem; padding: 0.75rem; border-radius: 8px;");
            out.println("        font-size: 0.9rem;");
            out.println("    }");

            out.println("    .actions-footer { margin-top: 2rem; text-align: center; }");

            out.println("    .footer { margin-top: 3rem; font-size: 0.8rem; color: var(--secondary-color); opacity: 0.8; font-weight: 300; text-align: center; }");

            out.println("    @media (max-width: 992px) { .content-grid { grid-template-columns: 1fr; } }");
            out.println("    @media (max-width: 768px) {");
            out.println("       .main-container { padding: 2rem 1rem; }");
            out.println("       .content-box { padding: 2rem 1.5rem; } h1 { font-size: 2rem; }");
            out.println("       .navbar-custom { flex-direction: column; }");
            out.println("    }");
            
            out.println("    </style>");
            // --- FIN DE CSS MODIFICADO ---
            
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='main-container'>");
            out.println("        <div class='content-box'>");

            out.println("            <nav class='navbar-custom'>");
            out.println("                <span class='user-info'>Usuario: " + dni + "</span>");
            out.println("                <div class='nav-actions'>");
            out.println("                   <a class='btn-glass' href='" + req.getContextPath() + "/alumno/ficha?dni=" + dni + "'>Ver mi ficha</a>");
            out.println("                   <a class='btn-glass' href='" + req.getContextPath() + "/logout'>Cerrar sesión</a>");
            out.println("                </div>");
            out.println("            </nav>");

            out.println("<h1>Mis Asignaturas</h1>");
            
            if (asignaturas == null || asignaturas.isEmpty()) {
                out.println("<p style='text-align:center;'>Error al recuperar asignaturas o no tienes asignaturas inscritas.</p>");
            } else {
                out.println("<div class='content-grid'>");
                
                out.println("<div class='table-container'>");
                out.println("    <table>");
                out.println("        <thead><tr><th>Asignatura</th></tr></thead>");
                out.println("        <tbody>");
                for (Asignatura a : asignaturas) {
                    out.println("<tr>");
                    out.println("    <td><a href='" + req.getContextPath() + "/alumno/nota?asignatura=" + a.getAsignaturasDeAlumno() + "'>" + a.getAsignaturasDeAlumno() + "</a></td>");
                    out.println("</tr>");
                }
                out.println("        </tbody>");
                out.println("    </table>");
                out.println("</div>");
                
                out.println("<div class='group-container'>");
                out.println("    <h4>MI GRUPO</h4>");
                out.println("    <ol class='group-list'>");
                out.println("        <li>Jara Leal García</li>");
                out.println("        <li>Pau Zaragozá Carrascosa</li>");
                out.println("        <li>Aaron Montaraz Gómez</li>");
                out.println("        <li>Lluis Colomar García</li>");
                out.println("        <li>Fran de la Guía González</li>");
                out.println("        <li>Raúl Medrano Llopis</li>");
                out.println("    </ol>");
                out.println("</div>");
                
                out.println("</div>"); 
            }

            out.println("<div class='actions-footer'>");
            out.println("    <form method='GET' action='certificado' style='display: inline-block;'>");
            out.println("        <button type='submit' class='btn-glass'>Imprimir certificado</button>");
            out.println("    </form>");
            out.println("</div>");

            out.println("<footer class='footer'>");
            out.println("    Grupo Gxx – Jara Leal García, Pau Zaragozá Carrascosa, Aaron Montaraz Gómez, Lluis Colomar García, Fran de la Guía González, Raúl Medrano Llopis");
            out.println("</footer>");

            out.println("        </div>");
            out.println("    </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}