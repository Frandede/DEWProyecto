package filtros;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1) Invalidamos la sesión de la aplicación
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2) Forzamos 401 Basic para que el navegador olvide las credenciales
        resp.setHeader("WWW-Authenticate", "Basic realm=\"NOL 2425\"");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 3) Enviamos HTML minimalista con fondo blanco y ocultando elementos previos
        String context = req.getContextPath() + "/";
        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            // Estilos: fondo blanco, sin márgenes, ocultar todo excepto nuestro mensaje
            out.println("<style>"
                      + "html,body{background:#fff;margin:0;padding:0;height:100%;}"
                      + "body>*{display:none!important;}"
                      + "</style>");
            // Redirección automática a la raíz tras 2 segundos
            out.println("<meta http-equiv='refresh' content='2;url=" + context + "'>");
            out.println("<title>Logout</title></head><body>");
            // Mensaje centrado
            out.println("<div style='position:absolute;top:50%;left:50%;"
                      + "transform:translate(-50%,-50%);text-align:center;'>");
            out.println("<h3 style='color:#333;'>Has salido correctamente.</h3>");
            out.println("<p>Redirigiendo al inicio...</p>");
            out.println("<p><a href='" + context + "'>Si no eres redirigido, pulsa aquí</a></p>");
            out.println("</div></body></html>");
        }
    }
}
