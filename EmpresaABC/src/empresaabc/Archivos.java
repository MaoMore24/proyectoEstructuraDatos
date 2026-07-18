package empresaabc;

import java.io.*;
import javax.swing.JOptionPane;

public class Archivos {

    File miArchivo;

    public void crearArchivo() {
        miArchivo = new File("prod.txt");
        try {
            if (miArchivo.createNewFile()) {
                // Archivo creado por primera vez
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al crear prod.txt");
        }
    }

    public boolean existeConfiguracion() {
        File f = new File("prod.txt");
        return f.exists() && f.length() > 0;
    }

    public void escribirConfiguracion(String banco, int cantCajas) {
        try {
            FileWriter registro = new FileWriter("prod.txt", false);
            registro.write(banco + "\n");
            registro.write(cantCajas + "\n");
            registro.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar configuración.");
        }
    }

    public String[] leerConfiguracion() {
        try (BufferedReader br = new BufferedReader(new FileReader("prod.txt"))) {
            String banco = br.readLine();
            String cajas = br.readLine();
            if (banco != null && cajas != null) {
                return new String[]{banco.trim(), cajas.trim()};
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer configuración.");
        }
        return null;
    }

    // Guarda un tiquete atendido en reportes.txt
    public void guardarReporte(Nodo cliente, int numCaja) {
        try {
            FileWriter registro = new FileWriter("reportes.txt", true);
            registro.write(
                cliente.getId()          + ";" +
                cliente.getNombre()      + ";" +
                cliente.getEdad()        + ";" +
                cliente.getFecha()       + ";" +
                cliente.getHoraCreacion()+ ";" +
                cliente.getHoraAtencion()+ ";" +
                cliente.getTramite()     + ";" +
                cliente.getTipo()        + ";" +
                numCaja                  + "\n"
            );
            registro.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar en reportes.txt");
        }
    }
}
