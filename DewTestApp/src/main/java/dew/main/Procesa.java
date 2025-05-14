package dew.main;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class Procesa
 */
@WebServlet("/Procesa")
public class Procesa extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Procesa() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		/* Leer los campos enviados desde el formulario y asignarlos a variables */
		String nombre = request.getParameter("nombre");
		String apellidos = request.getParameter("apellidos");
		String numero = request.getParameter("numero");
		String[] aficiones = request.getParameterValues("aficiones");
		
		int num = 0;
		
		try {
			num = Integer.parseInt(numero);
		}
		catch(NumberFormatException ex) {
			response.sendError(400);
		}
		
		if(nombre == null || apellidos == null || numero == null) {
			response.sendError(400);
		}
		
		// código HTML en función del número introducido
		String respuesta = this.discriminaNumero(num);
		
		// código HTML en función de las aficiones marcadas
		String respuestaAficiones = "Tus aficiones son...";
		
		for(String aficion : aficiones) {
			respuestaAficiones = respuestaAficiones + "<br>" + aficion;
		}
		
		
		/* Generamos respuesta personalizada */
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		out.println("<!DOCTYPE html>");
		out.println("<html lang=\"en\">");
		out.println("<head>");
		out.println("<meta charset=\"UTF-8\">");
		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
		out.println("<title>Aplicación de ejemplo DEW</title>");
		out.println("<link href=\"./css/estilo.css\" rel=\"stylesheet\">");
		out.println("</head>");
		
		out.println("<body>");
		out.println("<div>");
		out.println("<p>");
		out.println("¡Hola, " + nombre + " " + apellidos + "!");
		out.println("</p>");
		out.println("<p>");
		out.println(respuesta);
		out.println("</p>");
		out.println("<p>");
		out.println(respuestaAficiones);
		out.println("</p>");
		out.println("</div>");
		
		out.println("<footer>");
		out.println("<h2><a href=\"/DewTestApp\" target=\"_blank\">Volver a la página inicial</a></h2>");
		out.println("</footer>");
		out.println("</body>");
		
		out.println("</html>");
		
		out.close();
	}
	
	protected String discriminaNumero(int num) {
		String respuesta;
		
		if(num % 2 == 0) { //número par
				respuesta = "Además, has introducido un número <strong>par</strong>.";
		}
		else { //número impar
			respuesta = "Además, has introducido un número <strong>impar</strong>.";
		}
		return respuesta;
	}
}
