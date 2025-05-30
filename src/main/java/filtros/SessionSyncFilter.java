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
    // Mapa: nombreWeb -> { dniCentroEducativo, passCentroEducativo }
    private static final Map<String, String[]> usersData = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Mapea cada “username web” al (dni, password) que usa CentroEducativo
        usersData.put("pepe",    new String[]{"12345678W", "123456"});
        usersData.put("maria",   new String[]{"23456387R", "123456"});
        usersData.put("miguel",  new String[]{"34567891F", "123456"});
        usersData.put("laura",   new String[]{"93847525G", "123456"});
        usersData.put("minerva", new String[]{"37264096W", "123456"});
        // Ejemplo profesor:
        usersData.put("ramon",   new String[]{"23456733H", "123456"});
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        String context = req.getContextPath();

        // 0) Permitir acceso libre a página de login y recursos públicos
        if (uri.equals(context + "/") ||
            uri.equals(context + "/index.html") ||
            uri.startsWith(context + "/static/") ||
            uri.startsWith(context + "/css/") ||
            uri.startsWith(context + "/js/") ||
            uri.startsWith(context + "/images/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession();

        // 1) Si la sesión no contiene la “key” de CentroEducativo, la solicitamos
        if (session.getAttribute("key") == null) {
            // 2) Obtiene el “username web” que validó Tomcat
            String nombreWeb = req.getRemoteUser(); // ej. “pepe”, “maria”, “ramon”

            if (nombreWeb != null && usersData.containsKey(nombreWeb)) {
                // 3) Extraer DNI y contraseña datos del mapa
                String[] datos = usersData.get(nombreWeb);
                String dniDato  = datos[0];
                String passDato = datos[1];

                // 4) Llamar a CentroEducativo/login para obtener “key”
                String key = CentroEducativoClient.login(dniDato, passDato);
                if (key != null) {
                    // 5) Guardar dni y key en session
                    session.setAttribute("dni", dniDato);
                    session.setAttribute("key", key);
                } else {
                    // Si el REST de CentroEducativo rechaza las credenciales
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                   "Error de login en CentroEducativo");
                    return;
                }
            } else {
                // Usuario web no mapeado al REST
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                               "Usuario no reconocido en filtro");
                return;
            }
        }

        // Si ya había “key” en sesión, o la acabamos de obtener, continúa
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpieza si es necesaria
    }
}
