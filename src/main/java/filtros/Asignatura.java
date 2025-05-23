package filtros;

public class Asignatura {
    private int    idAsignatura;   // o String, según tu API
    private String codigo;
    private String nombre;
    private String cuatrimestre;

    // Constructor vacío (requerido por Gson)
    public Asignatura() { }

    // Getters y setters
    public int getIdAsignatura() {
        return idAsignatura;
    }
    public void setIdAsignatura(int idAsignatura) {
        this.idAsignatura = idAsignatura;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCuatrimestre() {
        return cuatrimestre;
    }
    public void setCuatrimestre(String cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
    }
}
