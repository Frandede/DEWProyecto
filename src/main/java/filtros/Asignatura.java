package filtros;

public class Asignatura {
    private String asignatura;
    private String nota;

    // Constructor vacío (requerido por Gson)
    public Asignatura() { }

    // Getters y setters
    public String getAsignaturasDeAlumno() {
        return asignatura;
    }
    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getNota() {
        return nota;
    }
    public void setNota(String nota) {
        this.nota = nota;
    }
}
