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

        List<AsignaturaProfesor> asignaturas = CentroEducativoClient.getAsignaturasDeProfesor(dni, key);

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>Mis Asignaturas - Profesor</title>");
            
            out.println("    <link rel='preconnect' href='https://fonts.googleapis.com'>");
            out.println("    <link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
            out.println("    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
            
            out.println("    <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css'>");

            out.println("    <style>");
            // --- INICIO DE CSS ---
            
            out.println("    :root {");
            out.println("        --bg-start: #4A3F63; --bg-end: #634f80;");
            out.println("        --primary-color: #ffffff; --secondary-color: rgba(255, 255, 255, 0.75);");
            out.println("        --card-bg: rgba(255, 255, 255, 0.1); --card-border: rgba(255, 255, 255, 0.2);");
            out.println("        --input-bg: rgba(255, 255, 255, 0.12); --shadow-color: rgba(0, 0, 0, 0.15);");
            out.println("        --button-bg: #ffffff; --button-text: #4A3F63; --button-bg-hover: #f0f0f0;");
            out.println("        --danger-color: #dc3545; --pass-color: #3b82f6; --good-color: #16a34a; --excel-color: #c026d3; --unrated-color: #6b7280;");
            out.println("    }");

            out.println("    body, html { margin: 0; padding: 0; font-family: 'Poppins', sans-serif; }");
            out.println("    .main-container { width: 100%; min-height: 100vh; color: var(--primary-color); background: linear-gradient(160deg, var(--bg-start), var(--bg-end)); padding: 2rem; box-sizing: border-box; }");
            out.println("    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; flex-wrap: wrap; gap: 1rem; }");
            out.println("    h1 { font-size: 2.2rem; font-weight: 700; margin: 0; }");
            out.println("    .btn-glass { display: inline-flex; align-items: center; gap: 0.5rem; padding: 0.8rem 1.5rem; background-color: var(--button-bg); color: var(--button-text) !important; border: none; border-radius: 12px; cursor: pointer; font-family: 'Poppins', sans-serif; font-size: 0.9rem; font-weight: 600; text-decoration: none; text-align: center; transition: background-color 0.3s ease, transform 0.2s ease; }");
            out.println("    .btn-glass:hover { background-color: var(--button-bg-hover); transform: translateY(-3px); }");
            out.println("    .btn-glass-small { padding: 0.4rem 0.9rem; font-size: 0.8rem; }");
            out.println("    .asignaturas-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(380px, 1fr)); gap: 1.5rem; align-items: start; }");
            out.println("    .asignatura-card { background: var(--card-bg); backdrop-filter: blur(10px); -webkit-backdrop-filter: blur(10px); border-radius: 15px; border: 1px solid var(--card-border); overflow: hidden; transition: all 0.3s ease; display: flex; flex-direction: column; }");
            out.println("    .asignatura-card:hover { transform: translateY(-5px); box-shadow: 0 8px 32px 0 var(--shadow-color); }");
            out.println("    .card-header { padding: 1rem 1.5rem; background: rgba(255,255,255,0.05); border-bottom: 1px solid var(--card-border); }");
            out.println("    .card-header h3 { margin: 0; font-size: 1.3rem; font-weight: 600; }");
            out.println("    .card-body { padding: 1.5rem; flex-grow: 1; display: flex; flex-direction: column; }");
            out.println("    .card-body h4 { margin-top: 0; margin-bottom: 0.5rem; font-weight: 600; font-size: 1.1rem; }");
            out.println("    .card-metadata { color: var(--secondary-color); font-size: 0.9rem; margin-bottom: 1.5rem; flex-grow: 1; }");
            out.println("    .card-metadata p { margin: 0.2rem 0; } .card-metadata i { margin-right: 0.5rem; width: 15px; text-align: center; }");
            out.println("    .alumnos-container { margin-top: 1.5rem; display: none; }");
            out.println("    .spinner { width: 40px; height: 40px; border: 4px solid var(--card-border); border-top-color: var(--primary-color); border-radius: 50%; animation: spin 1s linear infinite; margin: 2rem auto; }");
            out.println("    @keyframes spin { to { transform: rotate(360deg); } }");
            out.println("    .alert-glass { padding: 1rem; margin-top: 1rem; border-radius: 10px; border: 1px solid var(--card-border); background: var(--input-bg); text-align: center; }");
            out.println("    .table-wrapper { overflow-x: auto; }");
            out.println("    table { width: 100%; border-collapse: collapse; margin-top: 1rem; table-layout: fixed; }");
            out.println("    th, td { padding: 0.75rem; border-bottom: 1px solid var(--card-border); font-size: 0.9rem; vertical-align: middle; word-wrap: break-word; }");
            out.println("    th { font-weight: 600; text-align: left; }");
            // --- CAMBIO 1: AJUSTAR ANCHO DE COLUMNAS PARA 'EDITAR' ---
            out.println("    th:nth-child(1), td:nth-child(1) { width: 40%; }");
            out.println("    th:nth-child(2), td:nth-child(2) { width: 25%; }");
            out.println("    th:nth-child(3), td:nth-child(3) { width: 15%; text-align: center; }");
            out.println("    th:nth-child(4), td:nth-child(4) { width: 20%; text-align: center; }");
            // --- CAMBIO 2: HACER ENLACES MÁS INTUITIVOS ---
            out.println("    td a { color: var(--primary-color); text-decoration: none; font-weight: 500; transition: color 0.2s ease; }");
            out.println("    td a:hover { color: var(--secondary-color); text-decoration: underline; }");
            out.println("    .grade-badge { padding: 0.3em 0.6em; border-radius: 8px; color: white; font-weight: 500; display: inline-block; min-width: 35px; }");
            out.println("    .grade-badge.is-fail { background-color: var(--danger-color); } .grade-badge.is-pass { background-color: var(--pass-color); } .grade-badge.is-good { background-color: var(--good-color); } .grade-badge.is-excel { background-color: var(--excel-color); } .grade-badge.is-unrated { background-color: var(--unrated-color); }");
            out.println("    .grade-input { background-color: rgba(0,0,0,0.3); border: 1px solid var(--card-border); color: var(--primary-color); border-radius: 8px; padding: 0.5rem 0.2rem; width: 100%; box-sizing: border-box; text-align: center; font-size: 1.1rem; font-weight: 600; transition: all 0.2s ease; }");
            out.println("    .grade-input:focus { outline: none; border-color: var(--primary-color); background-color: rgba(0,0,0,0.4); box-shadow: 0 0 0 2px rgba(255,255,255,0.3); }");
            out.println("    .grade-input::-webkit-outer-spin-button, .grade-input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }");
            out.println("    .grade-input[type=number] { -moz-appearance: textfield; }");
            out.println("    .media-container { margin-top: 1.5rem; display: flex; align-items: center; gap: 1rem; }");
            
            out.println("    </style>");
            out.println("</head>");
            out.println("<body>");
            out.println("  <div class='main-container'>");
            out.println("    <header class='page-header'>");
            out.println("      <div><h1>Panel del Profesor</h1><p style='color: var(--secondary-color) !important; margin:0;'><i class='fa-solid fa-user-shield' style='margin-right: 0.5rem;'></i>Profesor: " + dni + "</p></div>");
            out.println("      <a href='" + contextPath + "/logout' class='btn-glass'><i class='fa-solid fa-right-from-bracket'></i> Cerrar sesión</a>");
            out.println("    </header>");

            if (asignaturas == null || asignaturas.isEmpty()) {
                out.println("    <div class='alert-glass'>No se encontraron asignaturas asignadas.</div>");
            } else {
                out.println("    <div class='asignaturas-grid'>");
                for (AsignaturaProfesor a : asignaturas) {
                    out.println("      <div class='asignatura-card'>");
                    out.println("        <div class='card-header'><h3>" + a.getAcronimo() + "</h3></div>");
                    out.println("        <div class='card-body'>");
                    out.println("          <h4>" + a.getNombre() + "</h4>");
                    out.println("          <div class='card-metadata'>");
                    out.println("            <p><i class='fa-solid fa-graduation-cap'></i> " + a.getCurso() + "º curso</p>");
                    out.println("            <p><i class='fa-solid fa-calendar-days'></i> " + a.getCuatrimestre() + "</p>");
                    out.println("            <p><i class='fa-solid fa-star'></i> " + a.getCreditos() + " créditos</p>");
                    out.println("          </div>");
                    out.println("          <button onclick=\"cargarAlumnos('" + a.getAcronimo() + "', this)\" class='btn-glass' style='width:100%'>");
                    out.println("            <i class='fa-solid fa-users'></i> Ver Alumnos");
                    out.println("          </button>");
                    out.println("          <div id='alumnos-" + a.getAcronimo() + "' class='alumnos-container'></div>");
                    out.println("        </div>");
                    out.println("      </div>");
                }
                out.println("    </div>");
            }

            out.println("    <script>");
            out.println("      const contextPath = '" + contextPath + "';");
            out.println("      function cargarAlumnos(asignatura, boton) {");
            out.println("        const contenedor = document.getElementById('alumnos-' + asignatura);");
            out.println("        if (contenedor.style.display === 'block') {");
            out.println("          contenedor.style.display = 'none';");
            out.println("          boton.innerHTML = '<i class=\"fa-solid fa-users\"></i> Ver Alumnos';");
            out.println("          return;");
            out.println("        }");
            out.println("        contenedor.innerHTML = '<div class=\"spinner\"></div>';");
            out.println("        contenedor.style.display = 'block';");
            out.println("        boton.innerHTML = '<i class=\"fa-solid fa-eye-slash\"></i> Ocultar Alumnos';");
            out.println("        boton.disabled = true;");
            out.println("        fetch(contextPath + '/profesores/alumnos-por-asignatura?asignatura=' + encodeURIComponent(asignatura))");
            out.println("        .then(response => { if (!response.ok) throw new Error('Error ' + response.status); return response.json(); })");
            out.println("        .then(alumnos => {");
            out.println("          let html = '';");
            out.println("          if (!alumnos || alumnos.length === 0) {");
            out.println("            html = '<div class=\"alert-glass\">No hay alumnos matriculados.</div>';");
            out.println("          } else {");
            // --- CAMBIO 1: CAMBIAR 'ACCIÓN' POR 'EDITAR' EN EL HTML GENERADO ---
            out.println("            html = '<div class=\"table-wrapper\"><table><thead><tr><th>Nombre</th><th>DNI</th><th>Nota</th><th>Editar</th></tr></thead><tbody>';");
            out.println("            alumnos.forEach(alumno => {");
            out.println("              const nota = alumno.additions1Drop3 || 'Sin calificar';");
            out.println("              const notaDisplay = nota === 'Sin calificar' ? 'Sin nota' : nota;");
            out.println("              const nombreAlumno = alumno.additions1Drop1 || 'N/A';");
            out.println("              const dniAlumno = alumno.additions1Drop2 || 'N/A';");
            out.println("              html += '<tr>' +");
            out.println("                      '<td><a href=\"' + contextPath + '/profesor/ficha-alumno?dni=' + encodeURIComponent(dniAlumno) + '\">' + nombreAlumno + '</a></td>' +");
            out.println("                      '<td>' + dniAlumno + '</td>' +");
            out.println("                      '<td><span class=\"grade-badge ' + getColorNota(nota) + '\">' + notaDisplay + '</span></td>' +");
            out.println("                      '<td><button class=\"btn-glass btn-glass-small\" onclick=\"editarNota(\\'' + asignatura + '\\',\\'' + dniAlumno + '\\', this)\"><i class=\"fa-solid fa-pencil\"></i></button></td>' +");
            out.println("                      '</tr>';");
            out.println("            });");
            out.println("            html += '</tbody></table></div><div class=\"media-container\"><button class=\"btn-glass\" onclick=\"calcularMedia(\\'' + asignatura + '\\')\"><i class=\"fa-solid fa-calculator\"></i> Calcular Media</button><span id=\"media-' + asignatura + '\"></span></div>';");
            out.println("          }");
            out.println("          contenedor.innerHTML = html;");
            out.println("          boton.disabled = false;");
            out.println("        })");
            out.println("        .catch(error => {");
            out.println("          contenedor.innerHTML = '<div class=\"alert-glass\">Error al cargar alumnos: ' + error.message + '</div>';");
            out.println("          boton.disabled = false;");
            out.println("          boton.innerHTML = '<i class=\"fa-solid fa-users\"></i> Ver Alumnos';");
            out.println("        });");
            out.println("      }");

            out.println("      function getColorNota(nota) { if (nota === 'Sin calificar') return 'is-unrated'; const num = parseFloat(nota); if (isNaN(num)) return 'is-unrated'; if (num < 5) return 'is-fail'; if (num < 7) return 'is-pass'; if (num < 9) return 'is-good'; return 'is-excel'; }");
            out.println("      function editarNota(asignatura, dniAlumno, boton) { const fila = boton.closest('tr'); const celdaAccion = fila.cells[3]; const celdaNota = fila.cells[2]; const spanNota = celdaNota.querySelector('span'); const notaActual = spanNota.textContent.trim() === 'Sin nota' ? '' : spanNota.textContent.trim(); celdaNota.innerHTML = '<input type=\"number\" min=\"0\" max=\"10\" step=\"0.1\" class=\"grade-input\" value=\"' + notaActual + '\" onkeydown=\"if(event.key===\\'Enter\\') guardarNota(\\''+asignatura+'\\', \\''+dniAlumno+'\\', this.parentElement.nextElementSibling.querySelector(\\'button\\'))\">'; celdaAccion.innerHTML = '<button class=\"btn-glass btn-glass-small\" onclick=\"guardarNota(\\''+asignatura+'\\', \\''+dniAlumno+'\\', this)\"><i class=\"fa-solid fa-save\"></i></button>'; celdaNota.querySelector('input').focus(); }");
            out.println("      function guardarNota(asignatura, dniAlumno, boton) { const fila = boton.closest('tr'); const inputNota = fila.querySelector('input'); const nuevaNota = inputNota.value; if (nuevaNota !== '' && (isNaN(parseFloat(nuevaNota)) || parseFloat(nuevaNota) < 0 || parseFloat(nuevaNota) > 10)) { alert('La nota debe ser un número entre 0 y 10 o estar vacío.'); return; } const notaFinal = nuevaNota === '' ? 'Sin calificar' : parseFloat(nuevaNota).toString(); boton.disabled = true; fetch(contextPath + '/profesores/alumnos-por-asignatura', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ action: 'update-grade', asignatura: asignatura, dniAlumno: dniAlumno, nota: notaFinal, dniProfesor: '" + dni + "' }) }).then(response => { if(!response.ok) { throw new Error('El servidor respondió con un error'); } return response.json(); }).then(data => { if (data.success) { const celdaNota = fila.cells[2]; const celdaAccion = fila.cells[3]; const notaDisplay = notaFinal === 'Sin calificar' ? 'Sin nota' : notaFinal; celdaNota.innerHTML = '<span class=\"grade-badge ' + getColorNota(notaFinal) + '\">' + notaDisplay + '</span>'; celdaAccion.innerHTML = '<button class=\"btn-glass btn-glass-small\" onclick=\"editarNota(\\''+asignatura+'\\', \\''+dniAlumno+'\\', this)\"><i class=\"fa-solid fa-pencil\"></i></button>'; } else { alert('Error al guardar: ' + (data.message || 'Desconocido')); } boton.disabled = false; }).catch(error => { alert('Error de comunicación: ' + error.message); boton.disabled = false; }); }");
            out.println("      function calcularMedia(asignatura) { const tabla = document.getElementById('alumnos-' + asignatura).querySelector('table tbody'); if (!tabla) return; let suma = 0, count = 0; tabla.querySelectorAll('tr').forEach(fila => { const notaSpan = fila.cells[2].querySelector('span'); if (notaSpan && !notaSpan.classList.contains('is-unrated')) { const num = parseFloat(notaSpan.textContent); if (!isNaN(num)) { suma += num; count++; } } }); const mediaSpan = document.getElementById('media-' + asignatura); if (count === 0) { mediaSpan.textContent = 'No hay notas para calcular.'; } else { mediaSpan.textContent = 'Nota media: ' + (suma / count).toFixed(2); } }");
            out.println("    </script>");
            out.println("  </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}