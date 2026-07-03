package empresaabc;

public class Nodo {

    private String nombre;
    private String id;
    private int edad;
    private String horaCreacion;
    private String horaAtencion; // "-1" hasta que sea atendido
    private String tramite;      // Depósitos, Retiros, Cambio de Divisas
    private String tipo;         // P: preferencial, A: un trámite, B: dos o más trámites
    private int prioridad;       // 1=Discapacidad/Embarazo, 2=Adulto Mayor, 3=Empresarial, 4=Regular
    private Nodo sig;

    public Nodo(String nombre, String id, int edad, String horaCreacion, String horaAtencion, String tramite, String tipo, int prioridad) {
        this.nombre = nombre;
        this.id = id;
        this.edad = edad;
        this.horaCreacion = horaCreacion;
        this.horaAtencion = horaAtencion;
        this.tramite = tramite;
        this.tipo = tipo;
        this.prioridad = prioridad;        
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getHoraCreacion() {
        return horaCreacion;
    }

    public void setHoraCreacion(String horaCreacion) {
        this.horaCreacion = horaCreacion;
    }

    public String getHoraAtencion() {
        return horaAtencion;
    }

    public void setHoraAtencion(String horaAtencion) {
        this.horaAtencion = horaAtencion;
    }

    public String getTramite() {
        return tramite;
    }

    public void setTramite(String tramite) {
        this.tramite = tramite;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public Nodo getSig() {
        return sig;
    }

    public void setSig(Nodo sig) {
        this.sig = sig;
    }

    
    @Override
    public String toString() {
        return "  - " + nombre + " | Cédula: " + id + " | Edad: " + edad +
               "\n    Trámite: " + tramite + " | Tipo: " + tipo +
               " | Creado: " + horaCreacion;
    }
}
