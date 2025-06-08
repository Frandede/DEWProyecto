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
import com.google.gson.Gson;

@WebServlet("/profesores/asignaturas")
public class AsignaturasProfesorServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("dni") == null || session.getAttribute("key") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión para acceder a esta página");
            return;
        }

        String contextPath = req.getContextPath();
        String dni = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");

        System.out.println("Solicitud de asignaturas para profesor: " + dni);

        List<AsignaturaProfesor> asignaturas = CentroEducativoClient.getAsignaturasDeProfesor(dni, key);

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("  <meta charset='UTF-8'>");
            out.println("  <meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("  <title>Asignaturas del Profesor</title>");
            out.println("  <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("  <link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css'>");
            out.println("  <style>");
            out.println("    body { background-color: #f8f9fa; padding-top: 2rem; }");
            out.println("    .header-card { border-bottom: 2px solid #dee2e6; }");
            out.println("    .asignatura-card { transition: all 0.3s ease; margin-bottom: 1.5rem; border-radius: 0.5rem; overflow: hidden; }");
            out.println("    .asignatura-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }");
            out.println("    .asignatura-header { background-color: #3498db; color: white; padding: 1rem; }");
            out.println("    .asignatura-body { padding: 1.5rem; background-color: white; }");
            out.println("    .asignatura-title { font-weight: 600; margin-bottom: 0.5rem; }");
            out.println("    .asignatura-metadata { color: #6c757d; font-size: 0.9rem; margin-bottom: 1rem; }");
            out.println("    .btn-ver-alumnos { width: 100%; }");
            out.println("    .alumnos-container { margin-top: 1.5rem; display: none; }");
            out.println("    .media-container { display: flex; align-items: center; margin-top: 1rem; }");
            out.println("    .media-result { margin-left: 1rem; font-weight: 500; }");
            out.println("    .badge-secondary { background-color: #6c757d; }");
            out.println("    .badge-success { background-color: #28a745; }");
            out.println("    .badge-info { background-color: #17a2b8; }");
            out.println("    .badge-primary { background-color: #007bff; }");
            out.println("    .badge-danger { background-color: #dc3545; }");
            out.println("  </style>");
            out.println("</head>");
            out.println("<body>");
            out.println("  <div class='container'>");

            // Botón de cerrar sesión
            out.println("    <div class='d-flex justify-content-end mb-4'>");
            out.println("      <a href='" + contextPath + "/logout' class='btn btn-outline-danger'>");
            out.println("        <i class='bi bi-box-arrow-left'></i> Cerrar sesión");
            out.println("      </a>");
            out.println("    </div>");

            // Cabecera con datos del profesor
            out.println("    <div class='card header-card mb-4'>");
            out.println("      <div class='card-body'>");
            out.println("        <h1 class='h4 mb-0'><i class='bi bi-book'></i> Asignaturas que imparte</h1>");
            out.println("        <p class='mb-0 text-muted'><i class='bi bi-person-circle'></i> Profesor: " + dni + "</p>");
            out.println("      </div>");
            out.println("    </div>");

            if (asignaturas == null || asignaturas.isEmpty()) {
                out.println("    <div class='alert alert-warning'>No se encontraron asignaturas asignadas.</div>");
            } else {
                out.println("    <div class='row'>");
                for (AsignaturaProfesor a : asignaturas) {
                    out.println("      <div class='col-md-6 col-lg-4'>");
                    out.println("        <div class='asignatura-card'>");
                    out.println("          <div class='asignatura-header'>");
                    out.println("            <h3 class='h5 mb-0'>" + a.getAcronimo() + "</h3>");
                    out.println("          </div>");
                    out.println("          <div class='asignatura-body'>");
                    out.println("            <h4 class='asignatura-title'>" + a.getNombre() + "</h4>");
                    out.println("            <div class='asignatura-metadata'>");
                    out.println("              <p class='mb-1'><i class='bi bi-bookmark'></i> " + a.getCurso() + "º curso</p>");
                    out.println("              <p class='mb-1'><i class='bi bi-calendar'></i> " + a.getCuatrimestre() + "</p>");
                    out.println("              <p class='mb-3'><i class='bi bi-award'></i> " + a.getCreditos() + " créditos</p>");
                    out.println("            </div>");
                    out.println("            <button onclick=\"cargarAlumnos('" + a.getAcronimo() + "', this)\" class='btn btn-success btn-ver-alumnos'>");
                    out.println("              <i class='bi bi-people-fill'></i> Ver Alumnos");
                    out.println("            </button>");
                    out.println("            <div id='alumnos-" + a.getAcronimo() + "' class='alumnos-container'></div>");
                    out.println("          </div>");
                    out.println("        </div>");
                    out.println("      </div>");
                }
                out.println("    </div>");
            }

            // JavaScript
            out.println("    <script>");
            out.println("      const contextPath = '" + contextPath + "';");
            out.println("      window.sessionStorage.setItem('dni', '" + dni + "');");
            out.println("      window.sessionStorage.setItem('key', '" + key + "');");

            // cargarAlumnos()
            out.println("      function cargarAlumnos(asignatura, boton) {");
            out.println("        const contenedor = document.getElementById('alumnos-' + asignatura);");
            out.println("        if (contenedor.style.display === 'block') {");
            out.println("          contenedor.style.display = 'none';");
            out.println("          boton.innerHTML = '<i class=\"bi bi-people-fill\"></i> Ver Alumnos';");
            out.println("          boton.classList.remove('btn-danger');");
            out.println("          boton.classList.add('btn-success');");
            out.println("          return;");
            out.println("        }");
            out.println("        const spinnerHtml = '<div class=\"d-flex justify-content-center mt-3\">' +");
            out.println("                              '<div class=\"spinner-border text-primary\" role=\"status\">' +");
            out.println("                                '<span class=\"visually-hidden\">Cargando...</span>' +");
            out.println("                              '</div>' +");
            out.println("                            '</div>';");
            out.println("        contenedor.innerHTML = spinnerHtml;");
            out.println("        contenedor.style.display = 'block';");
            out.println("        boton.innerHTML = '<i class=\"bi bi-people-fill\"></i> Ocultar Alumnos';");
            out.println("        boton.classList.remove('btn-success');");
            out.println("        boton.classList.add('btn-danger');");
            out.println("        boton.disabled = true;");
            out.println("        fetch(contextPath + '/profesores/alumnos-por-asignatura?asignatura=' + encodeURIComponent(asignatura), {");
            out.println("          credentials: 'same-origin'");
            out.println("        })");
            out.println("        .then(response => {");
            out.println("          if (!response.ok) {");
            out.println("            return response.text().then(text => {");
            out.println("              let errorMsg = 'Error ' + response.status;");
            out.println("              try { const errJson = JSON.parse(text); if (errJson.error) errorMsg = errJson.error; } catch(e) { errorMsg = text || errorMsg; }");
            out.println("              throw new Error(errorMsg);");
            out.println("            });");
            out.println("          }");
            out.println("          return response.json();");
            out.println("        })");
            out.println("        .then(alumnos => {");
            out.println("          let html = '';");
            out.println("          if (!alumnos || alumnos.length === 0) {");
            out.println("            html = '<div class=\"alert alert-info mt-3\">No hay alumnos matriculados en esta asignatura.</div>';");
            out.println("          } else {");
            out.println("            html = '<div class=\"table-responsive mt-3\">' +");
            out.println("                   '<table class=\"table table-hover table-sm\">' +");
            out.println("                   '<thead><tr><th>Nombre</th><th>DNI</th><th>Nota</th><th>Acciones</th></tr></thead>' +");
            out.println("                   '<tbody>';");
            out.println("            alumnos.forEach(alumno => {");
            out.println("              const nota = alumno.additions1Drop3 || 'Sin calificar';");
            out.println("              const notaDisplay = nota === 'Sin calificar' ? 'Sin nota' : nota;");
            out.println("              html += '<tr>' +");
            out.println("                      '<td>' + (alumno.additions1Drop1 || 'N/A') + '</td>' +");
            out.println("                      '<td>' + (alumno.additions1Drop2 || 'N/A') + '</td>' +");
            out.println("                      '<td><span class=\"badge ' + getColorNota(nota) + '\">' + notaDisplay + '</span></td>' +");
            out.println("                      '<td><button class=\"btn btn-sm btn-outline-primary\" onclick=\"editarNota(\\'' + asignatura + '\\',\\'' + (alumno.additions1Drop2 || '') + '\\', this)\"><i class=\"bi bi-pencil\"></i> Editar</button></td>' +");
            out.println("                      '</tr>';");
            out.println("            });");
            out.println("            html += '</tbody></table>' +");
            out.println("                   '<div class=\"media-container\">' +");
            out.println("                     '<button class=\"btn btn-primary\" onclick=\"calcularMedia(\\'' + asignatura + '\\')\">' +");
            out.println("                       '<i class=\"bi bi-calculator\"></i> Calcular nota media</button>' +");
            out.println("                     '<span id=\"media-' + asignatura + '\" class=\"media-result\"></span>' +");
            out.println("                   '</div>' +");
            out.println("                 '</div>';");
            out.println("          }");
            out.println("          contenedor.innerHTML = html;");
            out.println("          boton.disabled = false;");
            out.println("        })");
            out.println("        .catch(error => {");
            out.println("          console.error('Error al cargar alumnos:', error);");
            out.println("          contenedor.innerHTML = '<div class=\"alert alert-danger mt-3\">Error al cargar alumnos: ' + error.message + '</div>';");
            out.println("          boton.disabled = false;");
            out.println("          boton.innerHTML = '<i class=\"bi bi-people-fill\"></i> Ver Alumnos';");
            out.println("          boton.classList.remove('btn-danger');");
            out.println("          boton.classList.add('btn-success');");
            out.println("        });");
            out.println("      }");

            // getColorNota()
            out.println("      function getColorNota(nota) {");
            out.println("        if (nota === 'Sin calificar') return 'badge-secondary';");
            out.println("        const num = parseFloat(nota);");
            out.println("        if (isNaN(num)) return 'badge-secondary';");
            out.println("        if (num < 5) return 'badge-danger';");
            out.println("        if (num < 7) return 'badge-info';");
            out.println("        if (num < 9) return 'badge-primary';");
            out.println("        return 'badge-success';");
            out.println("      }");

            // editarNota()
            out.println("      function editarNota(asignatura, dniAlumno, boton) {");
            out.println("        const fila = boton.closest('tr');");
            out.println("        const celdaNota = fila.querySelector('td:nth-child(3)');");
            out.println("        const actualNota = celdaNota.textContent.trim() === 'Sin nota' ? '' : celdaNota.textContent.trim();");
            out.println("        celdaNota.innerHTML = '<input type=\"number\" min=\"0\" max=\"10\" step=\"0.1\" class=\"form-control form-control-sm\" value=\"' + actualNota + '\">';");
            out.println("        boton.textContent = 'Guardar';");
            out.println("        boton.onclick = function() { guardarNota(asignatura, dniAlumno, boton); };");
            out.println("      }");

            // guardarNota()
            out.println("      function guardarNota(asignatura, dniAlumno, boton) {");
            out.println("        const fila = boton.closest('tr');");
            out.println("        const inputNota = fila.querySelector('td:nth-child(3) input');");
            out.println("        const nuevaNota = inputNota.value.trim();");
            out.println("        if (nuevaNota === '') {");
            out.println("          alert('La nota no puede estar vacía.');");
            out.println("          return;");
            out.println("        }");
            out.println("        const numNota = parseFloat(nuevaNota);");
            out.println("        if (isNaN(numNota) || numNota < 0 || numNota > 10) {");
            out.println("          alert('La nota debe ser un número entre 0 y 10.');");
            out.println("          return;");
            out.println("        }");
            out.println("        const dniProfesor = window.sessionStorage.getItem('dni');");
            out.println("        boton.disabled = true;");
            out.println("        fetch(contextPath + '/profesores/alumnos-por-asignatura', {");
            out.println("          method: 'POST',");
            out.println("          credentials: 'same-origin',");
            out.println("          headers: { 'Content-Type': 'application/json' },");
            out.println("          body: JSON.stringify({");
            out.println("            action: 'update-grade',");
            out.println("            asignatura: asignatura,");
            out.println("            dniAlumno: dniAlumno,");
            out.println("            nota: numNota,");  // <-- aquí va numNota, no numNota.toFloat()
            out.println("            dniProfesor: dniProfesor");
            out.println("          })");
            out.println("        })");
            out.println("        .then(response => {");
            out.println("          if (!response.ok) {");
            out.println("            throw new Error('HTTP ' + response.status);");
            out.println("          }");
            out.println("          return response.json();");
            out.println("        })");
            out.println("        .then(data => {");
            out.println("          if (data.success) {");
            out.println("            const celdaNota = fila.querySelector('td:nth-child(3)');");
            out.println("            celdaNota.innerHTML = '<span class=\"badge ' + getColorNota(numNota) + '\">' + numNota + '</span>';");
            out.println("            boton.textContent = 'Editar';");
            out.println("            boton.disabled = false;");
            out.println("            boton.onclick = function() { editarNota(asignatura, dniAlumno, boton); };");
            out.println("            alert('Nota actualizada correctamente');");
            out.println("          } else {");
            out.println("            alert('Error al actualizar la nota: ' + (data.message || 'Error desconocido'));");
            out.println("            boton.disabled = false;");
            out.println("          }");
            out.println("        })");
            out.println("        .catch(error => {");
            out.println("          alert('Error en la comunicación: ' + error.message);");
            out.println("          boton.disabled = false;");
            out.println("        });");
            out.println("      }");

            // calcularMedia()
            out.println("      function calcularMedia(asignatura) {");
            out.println("        fetch(contextPath + '/profesores/alumnos-por-asignatura?asignatura=' + encodeURIComponent(asignatura), {");
            out.println("          credentials: 'same-origin'");
            out.println("        })");
            out.println("        .then(response => response.json())");
            out.println("        .then(alumnos => {");
            out.println("          let suma = 0;");
            out.println("          let count = 0;");
            out.println("          alumnos.forEach(alumno => {");
            out.println("            const nota = alumno.additions1Drop3;");
            out.println("            if (nota && nota !== 'Sin calificar') {");
            out.println("              const num = parseFloat(nota);");
            out.println("              if (!isNaN(num)) { suma += num; count++; }");
            out.println("            }");
            out.println("          });");
            out.println("          const mediaSpan = document.getElementById('media-' + asignatura);");
            out.println("          if (count === 0) {");
            out.println("            mediaSpan.textContent = 'No hay notas para calcular.';");
            out.println("          } else {");
            out.println("            const media = (suma / count).toFixed(2);");
            out.println("            mediaSpan.textContent = 'Nota media: ' + media;");
            out.println("          }");
            out.println("        });");
            out.println("      }");
            out.println("    </script>");

            out.println("  </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}