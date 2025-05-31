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

@WebServlet("/profesores/asignaturas")
public class AsignaturasProfesorServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 1. Verificar sesión y autenticación
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("dni") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión para acceder a esta página");
            return;
        }

        // 2. Obtener credenciales de la sesión
        String dni = (String) session.getAttribute("dni");
        String key = (String) session.getAttribute("key");

        // 3. Registrar acceso para depuración
        System.out.println("Solicitud de asignaturas para profesor: " + dni);

        // 4. Obtener las asignaturas del profesor
        List<AsignaturaProfesor> asignaturas = CentroEducativoClient.getAsignaturasDeProfesor(dni, key); 

        // 5. Configurar respuesta
        resp.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = resp.getWriter()) {
            // 6. Construir página HTML
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>Asignaturas del Profesor</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css'>");
            out.println("<style>");
            out.println("body { background-color: #f8f9fa; padding-top: 2rem; }");
            out.println(".header-card { border-bottom: 2px solid #dee2e6; }");
            out.println(".asignatura-card { transition: all 0.3s ease; margin-bottom: 1.5rem; border-radius: 0.5rem; overflow: hidden; }");
            out.println(".asignatura-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }");
            out.println(".asignatura-header { background-color: #3498db; color: white; padding: 1rem; }");
            out.println(".asignatura-body { padding: 1.5rem; background-color: white; }");
            out.println(".asignatura-title { font-weight: 600; margin-bottom: 0.5rem; }");
            out.println(".asignatura-metadata { color: #6c757d; font-size: 0.9rem; margin-bottom: 1rem; }");
            out.println(".btn-ver-alumnos { width: 100%; }");
            out.println(".alumnos-container { margin-top: 1.5rem; display: none; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            
            // 7. Encabezado de la página
            out.println("<div class='card header-card mb-4'>");
            out.println("<div class='card-body'>");
            out.println("<h1 class='h4 mb-0'><i class='bi bi-book'></i> Asignaturas que imparte</h1>");
            out.println("<p class='mb-0 text-muted'><i class='bi bi-person-circle'></i> Profesor: " + dni + "</p>");
            out.println("</div>");
            out.println("</div>");

            // 8. Contenido principal - Listado de asignaturas
            if (asignaturas == null || asignaturas.isEmpty()) {
                out.println("<div class='alert alert-warning'>No se encontraron asignaturas asignadas.</div>");
            } else {
                out.println("<div class='row'>");
                for (AsignaturaProfesor a : asignaturas) {
                    out.println("<div class='col-md-6 col-lg-4'>");
                    out.println("<div class='asignatura-card'>");
                    out.println("<div class='asignatura-header'>");
                    out.println("<h3 class='h5 mb-0'>" + a.getAcronimo() + "</h3>");
                    out.println("</div>");
                    out.println("<div class='asignatura-body'>");
                    out.println("<h4 class='asignatura-title'>" + a.getNombre() + "</h4>");
                    out.println("<div class='asignatura-metadata'>");
                    out.println("<p class='mb-1'><i class='bi bi-bookmark'></i> " + a.getCurso() + "º curso</p>");
                    out.println("<p class='mb-1'><i class='bi bi-calendar'></i> " + a.getCuatrimestre() + "</p>");
                    out.println("<p class='mb-3'><i class='bi bi-award'></i> " + a.getCreditos() + " créditos</p>");
                    out.println("</div>");
                    out.println("<button onclick=\"cargarAlumnos('" + a.getAcronimo() + "', this)\" class='btn btn-success btn-ver-alumnos'>");
                    out.println("<i class='bi bi-people-fill'></i> Ver Alumnos");
                    out.println("</button>");
                    out.println("<div id='alumnos-" + a.getAcronimo() + "' class='alumnos-container'></div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                }
                out.println("</div>");
            }

            // 9. Pie de página con botón de logout
            out.println("<div class='mt-4 text-center'>");
            out.println("<a href='" + req.getContextPath() + "/logout' class='btn btn-danger'>");
            out.println("<i class='bi bi-box-arrow-left'></i> Cerrar sesión");
            out.println("</a>");
            out.println("</div>");

            // 10. Scripts JavaScript con todas las mejoras
            out.println("<script>");
            out.println("function cargarAlumnos(asignatura, boton) {");
            out.println("  const contenedor = document.getElementById('alumnos-' + asignatura);");
            out.println("  ");
            out.println("  // Comportamiento de toggle (mostrar/ocultar)");
            out.println("  if(contenedor.style.display === 'block') {");
            out.println("    contenedor.style.display = 'none';");
            out.println("    boton.innerHTML = '<i class=\"bi bi-people-fill\"></i> Ver Alumnos';");
            out.println("    boton.classList.remove('btn-danger');");
            out.println("    boton.classList.add('btn-success');");
            out.println("    return;");
            out.println("  }");
            out.println("  ");
            out.println("  // Mostrar contenido");
            out.println("  const spinnerHtml = '<div class=\"d-flex justify-content-center mt-3\"><div class=\"spinner-border text-primary\" role=\"status\"><span class=\"visually-hidden\">Cargando...</span></div></div>';");
            out.println("  contenedor.innerHTML = spinnerHtml;");
            out.println("  contenedor.style.display = 'block';");
            out.println("  ");
            out.println("  // Cambiar apariencia del botón");
            out.println("  boton.innerHTML = '<i class=\"bi bi-people-fill\"></i> Ocultar Alumnos';");
            out.println("  boton.classList.remove('btn-success');");
            out.println("  boton.classList.add('btn-danger');");
            out.println("  ");
            out.println("  // Deshabilitar botón durante la carga");
            out.println("  boton.disabled = true;");
            out.println("  ");
            out.println("  // Obtener datos de alumnos");
            out.println("  fetch('" + req.getContextPath() + "/profesores/alumnos-por-asignatura?asignatura=' + encodeURIComponent(asignatura))");
            out.println("    .then(response => {");
            out.println("      if (!response.ok) {");
            out.println("        return response.text().then(text => { ");
            out.println("          let errorMsg = 'Error ' + response.status;");
            out.println("          try { const errJson = JSON.parse(text); if(errJson.error) errorMsg = errJson.error; } catch(e){ errorMsg = text || errorMsg; }");
            out.println("          throw new Error(errorMsg);");
            out.println("        });");
            out.println("      }");
            out.println("      return response.json();");
            out.println("    })");
            out.println("    .then(alumnos => {");
            out.println("      let html = '';");
            out.println("      if (!alumnos || alumnos.length === 0) {");
            out.println("        html = '<div class=\"alert alert-info mt-3\">No hay alumnos matriculados en esta asignatura.</div>';");
            out.println("      } else {");
            out.println("        html = '<div class=\"table-responsive mt-3\">' +");
            out.println("               '<table class=\"table table-hover table-sm\">' +");
            out.println("               '<thead><tr><th>Nombre</th><th>DNI</th><th>Nota</th><th>Acciones</th></tr></thead>' +");
            out.println("               '<tbody>';");
            out.println("        ");
            out.println("        alumnos.forEach(alumno => {");
            out.println("          const nota = alumno.additions1Drop3 || 'Sin calificar';");
            out.println("          const notaDisplay = nota === 'Sin calificar' ? 'Sin nota' : nota;");
            out.println("          ");
            out.println("          html += '<tr>' +");
            out.println("                  '<td>' + (alumno.additions1Drop1 || 'N/A') + '</td>' +");
            out.println("                  '<td>' + (alumno.additions1Drop2 || 'N/A') + '</td>' +");
            out.println("                  '<td><span class=\"badge ' + getColorNota(nota) + '\">' + notaDisplay + '</span></td>' +");
            out.println("                  '<td><button class=\"btn btn-sm btn-outline-primary\" onclick=\"editarNota(\\'' + asignatura + '\\',\\'' + (alumno.additions1Drop2 || '') + '\\',this)\"><i class=\"bi bi-pencil\"></i> Editar</button></td>' +");
            out.println("                  '</tr>';");
            out.println("        });");
            out.println("        ");
            out.println("        html += '</tbody></table>' +");
            out.println("                '<button class=\"btn btn-primary mt-2\" onclick=\"calcularMedia(\\'' + asignatura + '\\')\">' +");
            out.println("                '<i class=\"bi bi-calculator\"></i> Calcular nota media</button>' +");
            out.println("                '</div>';");
            out.println("      }");
            out.println("      ");
            out.println("      contenedor.innerHTML = html;");
            out.println("      boton.disabled = false;");
            out.println("    })");
            out.println("    .catch(error => {");
            out.println("      console.error('Error al cargar alumnos:', error);");
            out.println("      contenedor.innerHTML = '<div class=\"alert alert-danger mt-3\">Error al cargar alumnos: ' + error.message + '</div>';");
            out.println("      boton.disabled = false;");
            out.println("    });");
            out.println("}");
            out.println("");
            out.println("function getColorNota(nota) {");
            out.println("  if (!nota || nota === 'Sin calificar') return 'bg-secondary';");
            out.println("  const notaNum = parseFloat(nota);");
            out.println("  if (isNaN(notaNum)) return 'bg-secondary';");
            out.println("  if (notaNum >= 9) return 'bg-success';");
            out.println("  if (notaNum >= 7) return 'bg-info';");
            out.println("  if (notaNum >= 5) return 'bg-primary';");
            out.println("  return 'bg-danger';");
            out.println("}");
            out.println("");
            out.println("function editarNota(asignatura, dniAlumno, boton) {");
            out.println("  console.log('Editar nota para:', asignatura, dniAlumno);");
            out.println("  // Implementar lógica de edición aquí");
            out.println("  alert('Editar nota para ' + dniAlumno + ' en ' + asignatura);");
            out.println("}");
            out.println("");
            out.println("function calcularMedia(asignatura) {");
            out.println("  console.log('Calcular media para:', asignatura);");
            out.println("  // Implementar lógica de cálculo de media aquí");
            out.println("  alert('Calcular media para ' + asignatura);");
            out.println("}");
            out.println("</script>");

            out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>");
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            System.err.println("Error al generar la página:");
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar la página");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Método no permitido");
    }
}