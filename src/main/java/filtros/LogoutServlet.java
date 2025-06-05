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

    	  // 1) Invalidar la sesión de la aplicación
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2) Eliminar cookie de sesión (opcional pero limpio)
        Cookie deleteSession = new Cookie("JSESSIONID", "");
        deleteSession.setMaxAge(0);
        deleteSession.setPath(req.getContextPath()); // asegúrate de usar el path correcto
        resp.addCookie(deleteSession);

        // 3) Redirigir directamente al menú de bienvenida (ej. index.html)
        resp.sendRedirect(req.getContextPath() + "/index.html");
        
    }
}
