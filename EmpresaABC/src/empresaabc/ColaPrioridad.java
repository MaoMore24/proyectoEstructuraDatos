package empresaabc;

// Estructura de Cola de Prioridad para los tiquetes del banco
public class ColaPrioridad {
    private Nodo prim;
    private Nodo ult;

    public ColaPrioridad() {
        this.prim = null;
        this.ult = null;
    }

    // Metodo para ver si la cola esta vacia
    public boolean esVacia() {
        return prim == null;
    }

    // Metodo para agregar un cliente ordenado por prioridad
    public void encolar(Nodo nuevo) {
        if (esVacia()) {
            prim = ult = nuevo;
            return;
        }

        // Caso especial: El nuevo nodo tiene mayor prioridad (menor número) que la cabeza de la cola
        if (nuevo.getPrioridad() < prim.getPrioridad()) {
            nuevo.setSig(prim);
            prim = nuevo;
            return;
        }

        // Caso general: Buscar la posición correcta de inserción
        // Queremos insertar después del último elemento que tenga prioridad menor o igual
        Nodo prev = prim;
        Nodo current = prim.getSig();

        while (current != null && current.getPrioridad() <= nuevo.getPrioridad()) {
            prev = current;
            current = current.getSig();
        }

        // Insertar entre prev y current
        prev.setSig(nuevo);
        nuevo.setSig(current);

        // Si se insertó al final de la cola, actualizar el puntero 'ult'
        if (current == null) {
            ult = nuevo;
        }
    }

    // Metodo para atender (desencolar) al primero
    public Nodo atiende() {
        if (esVacia()) {
            return null;
        }

        Nodo extraido = prim;
        prim = prim.getSig();

        if (prim == null) {
            ult = null;
        }

        extraido.setSig(null); // Desconectar del resto de la cola
        return extraido;
    }

    // Metodo para obtener el tamaño de la cola
    public int getTamanno() {
        int contador = 0;
        Nodo aux = prim;
        while (aux != null) {
            contador++;
            aux = aux.getSig();
        }
        return contador;
    }

    // Obtener el primer nodo sin sacarlo
    public Nodo getPrimero() {
        return prim;
    }

    @Override
    public String toString() {
        if (esVacia()) {
            return "La cola está vacía.";
        }

        StringBuilder sb = new StringBuilder();
        Nodo aux = prim;
        int posicion = 1;
        while (aux != null) {
            sb.append(posicion).append(". ").append(aux.getNombre())
              .append(" (ID: ").append(aux.getId()).append(")")
              .append(" [Trámite: ").append(aux.getTramite()).append("]")
              .append(" [Prioridad: ").append(aux.getPrioridad()).append("]\n");
            posicion++;
            aux = aux.getSig();
        }
        return sb.toString();
    }
}
