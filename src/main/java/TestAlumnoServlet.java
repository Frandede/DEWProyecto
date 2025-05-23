
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/alumno/test")
public class TestAlumnoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        response.setContentType("text/plain");
        try (PrintWriter out = response.getWriter()) {
            // Usuario autenticado por Tomcat
            String user = request.getRemoteUser();
            out.println("Usuario autenticado: " + user);

            // DNI (o nombreWeb) y contraseña que guardaste en sesión
            String dni = (String) session.getAttribute("dni");
            String password = (String) session.getAttribute("password");
            String key = (String) session.getAttribute("key");

            out.println("DNI en sesión: " + (dni != null ? dni : "no disponible"));
            out.println("Contraseña en sesión: " + (password != null ? password : "no disponible"));
            out.println("Key de CentroEducativo: " + (key != null ? key : "no disponible"));
        }
    }
}
