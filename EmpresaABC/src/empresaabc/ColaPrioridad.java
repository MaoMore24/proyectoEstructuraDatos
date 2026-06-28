package empresaabc;

public class ColaPrioridad {

    private Nodo prim;
    private Nodo ult;

    public ColaPrioridad() {
        this.prim = null;
        this.ult = null;
    }

    public boolean esVacia() {
        return prim == null;
    }

    public void encolar(Nodo nuevo) {
        if (esVacia()) {
            prim = ult = nuevo;
            return;
        }

        // Insertar al frente si tiene mayor prioridad que el primero
        if (nuevo.getPrioridad() < prim.getPrioridad()) {
            nuevo.setSig(prim);
            prim = nuevo;
            return;
        }

        // Buscar la posición correcta según prioridad
        Nodo prev = prim;
        Nodo current = prim.getSig();

        while (current != null && current.getPrioridad() <= nuevo.getPrioridad()) {
            prev = current;
            current = current.getSig();
        }

        prev.setSig(nuevo);
        nuevo.setSig(current);

        if (current == null) {
            ult = nuevo;
        }
    }

    public Nodo atiende() {
        if (esVacia()) {
            return null;
        }
        Nodo extraido = prim;
        prim = prim.getSig();
        if (prim == null) {
            ult = null;
        }
        extraido.setSig(null);
        return extraido;
    }

    public int getTamanno() {
        int contador = 0;
        Nodo aux = prim;
        while (aux != null) {
            contador++;
            aux = aux.getSig();
        }
        return contador;
    }

    public Nodo getPrimero() {
        return prim;
    }

    @Override
    public String toString() {
        if (esVacia()) {
            return "  (Sin clientes en espera)\n";
        }
        StringBuilder sb = new StringBuilder();
        Nodo aux = prim;
        int pos = 1;
        while (aux != null) {
            sb.append("  ").append(pos).append(". ").append(aux.getNombre())
              .append(" | Trámite: ").append(aux.getTramite())
              .append(" | Tipo: ").append(aux.getTipo()).append("\n");
            pos++;
            aux = aux.getSig();
        }
        return sb.toString();
    }
}
