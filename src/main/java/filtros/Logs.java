package filtros;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;

public class Logs implements Filter {
    private String logPath;

    @Override
    public void init(FilterConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        logPath = context.getInitParameter("logPath");
        if (logPath == null) {
            throw new ServletException("Parámetro 'logPath' no definido en web.xml");
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        String usuario = request.getRemoteUser();
        if (usuario == null) usuario = "anonimo";

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String metodo = request.getMethod();
        LocalDateTime fecha = LocalDateTime.now();

        String log = fecha + " " + usuario + " " + ip + " " + uri + " " + metodo + "\n";

        synchronized (this) {
            Files.write(Paths.get(logPath), log.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
} //PRUEBA 
