package filtros;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;



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
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                      + "<title>Certificado Académico</title>"
                      + "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
                      + "<style> @media print {.no-print{display:none;}}</style>"
                      + "</head><body class='p-5'>");

            out.println("<div class='text-center mb-4'>"
                      + "<h1>Certificado Académico</h1>"
                      + "<p>DNI del alumno: <strong>" + dni + "</strong></p>"
                      + "<img src='" + req.getContextPath() + "/images/" + dni + ".png' alt='Foto del alumno' width='150' class='rounded-circle mb-3'>"
                      + "</div>");

            if (asignaturas == null || asignaturas.isEmpty()) {
                out.println("<p>No se encontraron asignaturas registradas.</p>");
            } else {
                out.println("<table class='table table-bordered'><thead><tr><th>Asignatura</th><th>Nota</th></tr></thead><tbody>");
                for (Asignatura a : asignaturas) {
                    out.printf("<tr><td>%s</td><td>%s</td></tr>%n",
                               a.getAsignaturasDeAlumno(),
                               (a.getNota() == null || a.getNota().isEmpty()) ? "—" : a.getNota());
                }
                out.println("</tbody></table>");
            }

            out.println("<div class='text-center mt-4 no-print'>"
                      + "<button onclick='window.print()' class='btn btn-success'>Imprimir</button>"
                      + "<a href='" + req.getContextPath() + "/alumno/asignaturas' class='btn btn-secondary'>Volver</a>"
                      + "</div>");

            out.println("</body></html>");
        }
    }
}
