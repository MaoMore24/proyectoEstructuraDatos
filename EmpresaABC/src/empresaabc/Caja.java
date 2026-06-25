package empresaabc;

// Clase Caja para representar a los cajeros del banco
public class Caja {
    private int numero;
    private String tipo; // P, A, B
    private ColaPrioridad cola;
    private Nodo clienteActual;

    public Caja(int numero, String tipo) {
        this.numero = numero;
        this.tipo = tipo;
        this.cola = new ColaPrioridad();
        this.clienteActual = null;
    }

    // Getters y Setters
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public ColaPrioridad getCola() {
        return cola;
    }

    public Nodo getClienteActual() {
        return clienteActual;
    }

    public void setClienteActual(Nodo clienteActual) {
        this.clienteActual = clienteActual;
    }

    // Verifica si la caja está ocupada
    public boolean esOcupada() {
        return clienteActual != null;
    }

    // Pasa al siguiente cliente de la cola
    public Nodo atenderSiguiente(String horaAtencion) {
        Nodo siguiente = cola.atiende();
        if (siguiente != null) {
            siguiente.setHoraAtencion(horaAtencion);
            clienteActual = siguiente;
        } else {
            clienteActual = null;
        }
        return siguiente;
    }

    // Libera la caja
    public void liberarCaja() {
        clienteActual = null;
    }

    // Retorna una descripcion en texto del tipo de caja
    public String getDescripcionTipo() {
        switch (tipo) {
            case "P":
                return "Preferencial";
            case "A":
                return "Trámite Rápido (1 Trámite)";
            case "B":
                return "Regular (Multi-trámite)";
            default:
                return "Desconocido";
        }
    }
}
