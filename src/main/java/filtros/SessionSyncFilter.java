package filtros;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;


public class SessionSyncFilter implements Filter {

    private Map<String, String[]> userData = Map.of(
        "alumno", new String[]{"12345678W", "1234"},
        "profe", new String[]{"23456733H", "abcd"},
        "dew", new String[]{"111111111", "654321"} // admin
    );

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        
        if (session.getAttribute("key") == null) {
            String login = req.getRemoteUser();

            if (login != null && userData.containsKey(login)) {
                String[] datos = userData.get(login);
                String dni = datos[0];
                String pass = datos[1];

                String key = CentroEducativoClient.login(dni, pass); // Método que tú implementas

                if (key != null) {
                    session.setAttribute("dni", dni);
                    session.setAttribute("pass", pass);
                    session.setAttribute("key", key);
                } else {
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login REST fallido");
                    return;
                }
            } else {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autenticado por Tomcat");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
