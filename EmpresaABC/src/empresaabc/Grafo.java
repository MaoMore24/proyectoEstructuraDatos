package empresaabc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// Clase Grafo con matriz de adyacencia para los productos recomendados
public class Grafo {
    private String[] vertices;
    private boolean[][] matrizAdyacencia;
    private static final String ARCHIVO_GRAFO = "db_grafo.txt";

    // Número de vértices en el grafo (3 trámites y 3 productos)
    private static final int NUM_VERTICES = 6;

    public Grafo() {
        vertices = new String[NUM_VERTICES];
        matrizAdyacencia = new boolean[NUM_VERTICES][NUM_VERTICES];
        
        // Inicializar nombres de los vértices
        vertices[0] = "Depósitos";
        vertices[1] = "Retiros";
        vertices[2] = "Cambio de Divisas";
        vertices[3] = "Seguro de Vida / Fraude";
        vertices[4] = "Retiro sin Tarjeta (Cajeros)";
        vertices[5] = "Cuentas de Ahorro / Inversión";

        cargarOInicializarGrafo();
    }

    // Carga las relaciones del grafo desde el archivo txt
    private void cargarOInicializarGrafo() {
        File file = new File(ARCHIVO_GRAFO);
        if (!file.exists()) {
            // Inicializar arcos predeterminados
            agregarArco("Depósitos", "Seguro de Vida / Fraude");
            agregarArco("Retiros", "Retiro sin Tarjeta (Cajeros)");
            agregarArco("Cambio de Divisas", "Cuentas de Ahorro / Inversión");
            guardarGrafoEnArchivo();
        } else {
            // Leer arcos desde el archivo
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] partes = linea.split(",");
                    if (partes.length == 2) {
                        agregarArco(partes[0].trim(), partes[1].trim());
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al cargar el grafo: " + e.getMessage());
            }
        }
    }

    // Metodo para conectar un tramite con un producto
    private void agregarArco(String origen, String destino) {
        int idxOrigen = buscarIndice(origen);
        int idxDestino = buscarIndice(destino);

        if (idxOrigen != -1 && idxDestino != -1) {
            matrizAdyacencia[idxOrigen][idxDestino] = true;
        }
    }

    // Metodo para buscar el indice del vertice en el arreglo
    private int buscarIndice(String nombre) {
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i].equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return -1;
    }

    // Guarda el grafo en el archivo txt
    private void guardarGrafoEnArchivo() {
        try (FileWriter fw = new FileWriter(ARCHIVO_GRAFO)) {
            for (int i = 0; i < NUM_VERTICES; i++) {
                for (int j = 0; j < NUM_VERTICES; j++) {
                    if (matrizAdyacencia[i][j]) {
                        fw.write(vertices[i] + "," + vertices[j] + "\n");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar el grafo en archivo: " + e.getMessage());
        }
    }

    // Metodo para obtener el producto complementario segun el tramite
    public String obtenerProductoComplementario(String tramite) {
        int idxTramite = buscarIndice(tramite);
        if (idxTramite == -1) {
            return "Ninguno";
        }

        // Buscar en la fila del trámite cuál columna está en 'true'
        for (int j = 0; j < NUM_VERTICES; j++) {
            if (matrizAdyacencia[idxTramite][j]) {
                return vertices[j];
            }
        }

        return "Ninguno";
    }

    /**
     * Retorna una representación en texto de las relaciones del grafo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--- Grafo de Productos Complementarios ---\n");
        for (int i = 0; i < NUM_VERTICES; i++) {
            boolean tieneHijos = false;
            StringBuilder destinos = new StringBuilder();
            for (int j = 0; j < NUM_VERTICES; j++) {
                if (matrizAdyacencia[i][j]) {
                    if (tieneHijos) destinos.append(", ");
                    destinos.append(vertices[j]);
                    tieneHijos = true;
                }
            }
            if (tieneHijos) {
                sb.append(vertices[i]).append(" -> [ ").append(destinos.toString()).append(" ]\n");
            }
        }
        return sb.toString();
    }
}
