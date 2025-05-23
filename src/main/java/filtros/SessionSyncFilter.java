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
import java.util.HashMap;
import java.util.Map;

@WebFilter("/*")
public class SessionSyncFilter implements Filter {
    // Mapa: dni → passwordDatos
    private static final Map<String, String> usersData = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Llenar el mapa: DNI (usuarioWeb) → contraseña datos
        usersData.put("Pepe", "123456"); 
        usersData.put("Maria", "123456");   
        usersData.put("Miguel", "123456");       
        usersData.put("Laura", "123456");    
        usersData.put("Minerva", "123456");

        // Ejemplo profesor
        //usersData.put("23456733H", "passwordProfe");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest  req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        // Solo si NO hay key en sesión, la pedimos a CentroEducativo
        if (session.getAttribute("key") == null) {
            String dni = req.getRemoteUser();

            if (dni != null && usersData.containsKey(dni)) {
                String passDatos = usersData.get(dni);

                // Llamada REST: /CentroEducativo/login con { dni, passDatos }
                String key = CentroEducativoClient.login(dni, passDatos);
                if (key != null) {
                    session.setAttribute("dni", dni);
                    session.setAttribute("key", key);
                } else {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error login CentroEducativo");
                    return;
                }
            } else {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no reconocido");
                return;
            }
        }

        // Si ya tenía key, o la acaba de obtener
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { }
}
