package empresaabc;

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

    public int getNumero() { return numero; }

    public String getTipo() { return tipo; }

    public ColaPrioridad getCola() { return cola; }

    public Nodo getClienteActual() { return clienteActual; }

    public boolean esOcupada() {
        return clienteActual != null;
    }

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

    public void liberarCaja() {
        clienteActual = null;
    }

    public String getDescripcionTipo() {
        switch (tipo) {
            case "P": return "Preferencial";
            case "A": return "Trámite Rápido (1 trámite)";
            case "B": return "Regular (2 o más trámites)";
            default:  return "Desconocido";
        }
    }
}
