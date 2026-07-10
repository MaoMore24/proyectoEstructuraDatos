package empresaabc;

import java.time.LocalDate;
import javax.swing.JOptionPane;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Main {

    private static String nombreBanco;
    private static int cantCajas;
    private static Caja[] cajas;
    private static Archivos archivos = new Archivos();

    // Usuarios del sistema (mínimo 2)
    private static final String[][] USUARIOS = {
        {"admin",  "admin123"},
        {"cajero", "cajero123"}
    };

    public static void main(String[] args) {
        archivos.crearArchivo();

        // Login
        if (!login()) {
            JOptionPane.showMessageDialog(null, "Acceso denegado. Cerrando el programa.");
            System.exit(0);
        }

        // Módulo 0: Configuración
        modulo0Configuracion();

        // Inicializar cajas según configuración
        inicializarCajas();

        // Menú principal
        menuPrincipal();
    }

    // --- LOGIN ---
    private static boolean login() {
        int intentos = 3;
        while (intentos > 0) {
            String usuario  = JOptionPane.showInputDialog(null, "Ingrese su usuario:");
            if (usuario == null) return false;

            String password = JOptionPane.showInputDialog(null, "Ingrese su contraseña:");
            if (password == null) return false;

            for (String[] u : USUARIOS) {
                if (u[0].equals(usuario.trim()) && u[1].equals(password.trim())) {
                    JOptionPane.showMessageDialog(null, "¡Bienvenido, " + usuario.trim() + "!");
                    return true;
                }
            }

            intentos--;
            if (intentos > 0) {
                JOptionPane.showMessageDialog(null, "Credenciales incorrectas. Intentos restantes: ");
            }
        }
        return false;
    }

    // --- MÓDULO 0: CONFIGURACIÓN ---
    private static void modulo0Configuracion() {
        if (archivos.existeConfiguracion()) {
            // Cargar configuración existente
            String[] config = archivos.leerConfiguracion();
            nombreBanco = config[0];
            cantCajas   = Integer.parseInt(config[1]);
            JOptionPane.showMessageDialog(null,
                "Configuración cargada de prod.txt:\n" +
                "Banco: " + nombreBanco + "\n" +
                "Cajas: " + cantCajas,
                "Módulo 0: Configuración", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Primera ejecución: solicitar datos
            JOptionPane.showMessageDialog(null, "Primera ejecución. Configure el sistema.");

            String banco = "";
            while (banco.trim().isEmpty()) {
                banco = JOptionPane.showInputDialog(null, "Ingrese el nombre del Banco:");
                if (banco == null) System.exit(0);
            }
            nombreBanco = banco.trim();

            int numCajas = 0;
            while (numCajas < 3) {
                String entrada = JOptionPane.showInputDialog(null, "Ingrese la cantidad de cajas (mínimo 3):", "Configuración", JOptionPane.QUESTION_MESSAGE);
                if (entrada == null) System.exit(0);
                try {
                    numCajas = Integer.parseInt(entrada.trim());
                    if (numCajas < 3) {
                        JOptionPane.showMessageDialog(null, "La cantidad mínima de cajas es 3.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Ingrese un número entero válido.");
                }
            }
            cantCajas = numCajas;

            archivos.escribirConfiguracion(nombreBanco, cantCajas);
            JOptionPane.showMessageDialog(null, "Configuración guardada en prod.txt.");
        }
    }

    // Crea el arreglo de cajas según los tipos requeridos
    private static void inicializarCajas() {
        cajas = new Caja[cantCajas];
        cajas[0] = new Caja(1, "P"); // Caja 1: Preferencial (única)
        cajas[1] = new Caja(2, "A"); // Caja 2: Rápida / 1 trámite (única)
        for (int i = 2; i < cantCajas; i++) {
            cajas[i] = new Caja(i + 1, "B"); // Resto: Regulares
        }
    }

    // --- MENÚ PRINCIPAL ---
    private static void menuPrincipal() {
        String opcion;
        do {
            opcion = JOptionPane.showInputDialog(null,
                "--- SISTEMA BANCARIO: " + nombreBanco.toUpperCase() + " ---\n\n" +
                "1) Crear Tiquete\n" +
                "2) Atender Siguiente Cliente\n" +
                "3) Ver Estado de Filas\n" +
                "4) Salir\n\n" +
                "Seleccione: ",
                "Menú Principal", JOptionPane.QUESTION_MESSAGE);

            if (opcion == null) opcion = "4";

            switch (opcion) {
                case "1": modulo1CrearTiquete(); break;
                case "2": atenderCliente();      break;
                case "3": verEstadoFilas();       break;
                case "4": JOptionPane.showMessageDialog(null, "¡Hasta luego!"); break;
                default:  JOptionPane.showMessageDialog(null, "Seleccione una opción válida.");
            }
        } while (!opcion.equals("4"));
    }

    // --- MÓDULO 1.1: CREACIÓN DE TIQUETES ---
    private static void modulo1CrearTiquete() {
        // 1. Nombre
        String nombre = "";
        while (nombre.trim().isEmpty()) {
            nombre = JOptionPane.showInputDialog(null, "Nombre del cliente:");
            if (nombre == null) return;
        }

        // 2. ID / Cédula
        String id = "";
        while (id.trim().isEmpty()) {
            id = JOptionPane.showInputDialog(null, "Cédula / ID del cliente:");
            if (id == null) return;
        }

        // 3. Edad
        int edad = -1;
        while (edad < 0 || edad > 120) {
            String entrada = JOptionPane.showInputDialog(null, "Edad del cliente:");
            if (entrada == null) return;
            try {
                edad = Integer.parseInt(entrada.trim());
                if (edad < 0 || edad > 120) {
                    JOptionPane.showMessageDialog(null, "Edad fuera de rango (0-120).");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingrese un número entero válido.");
            }
        }

        // 4. Tipo de cliente
        String tipo;
        int prioridad;

        int respPref = JOptionPane.showConfirmDialog(null,
            "¿El cliente califica para atención Preferencial?\n(Discapacidad, Embarazo, Adulto Mayor, Empresarial)",
    "Tipo de Cliente", 0);

        if (respPref == 0) {
            tipo = "P";
            String condicion = JOptionPane.showInputDialog(null,
                "Seleccione la condición:\n" +
                "1) Discapacidad / Embarazo\n" +
                "2) Adulto Mayor\n" +
                "3) Empresarial");

            if ("1".equals(condicion)) {
                prioridad = 1;
            } else if ("2".equals(condicion)) {
                prioridad = 2;
            } else {
                prioridad = 3;
            }

        } else {
            String cantTramites = JOptionPane.showInputDialog(null,
                "Seleccione la cantidad de trámites:\n" +
                "1) Un solo trámite (Caja Rápida)\n" +
                "2) Dos o más trámites (Caja Regular)");

            if ("1".equals(cantTramites)) {
                tipo = "A";
            } else {
                tipo = "B";
            }

            prioridad = 4;
        }

        // 5. Trámite
        String tramite;
        String selTramite = JOptionPane.showInputDialog(null,
            "Seleccione el trámite:\n" +
            "1) Depósitos\n" +
            "2) Retiros\n" +
            "3) Cambio de Divisas");
            

        if ("1".equals(selTramite)) {
            tramite = "Depósitos";
        } else if ("2".equals(selTramite)) {
            tramite = "Retiros";
        } else if ("3".equals(selTramite)) {
            tramite = "Cambio de Divisas";
        } else {
            JOptionPane.showMessageDialog(null, "Opción de trámite inválida. Se cancela la creación.");
            return;
        }

        // 6. Crear nodo tiquete
        String horaCreacion = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Nodo nuevo = new Nodo(nombre.trim(), id.trim(), edad, horaCreacion, "-1", tramite, tipo, fecha, prioridad);

        // 7. Asignar caja según tipo
        Caja cajaAsignada;
        if ("P".equals(tipo)) {
            cajaAsignada = cajas[0];
        } else if ("A".equals(tipo)) {
            cajaAsignada = cajas[1];
        } else {
            // Caja B con menor cantidad de personas en fila
            cajaAsignada = cajas[2];
            for (int i = 3; i < cantCajas; i++) {
                if (cajas[i].getCola().getTamanno() < cajaAsignada.getCola().getTamanno()) {
                    cajaAsignada = cajas[i];
                }
            }
        }

        int personasDelante = cajaAsignada.getCola().getTamanno();
        cajaAsignada.getCola().encolar(nuevo);

        // 8. Imprimir tiquete
        StringBuilder tiquete = new StringBuilder();
        tiquete.append("========= TIQUETE GENERADO =========\n");
        tiquete.append("Cliente  : ").append(nombre.trim()).append("\n");
        tiquete.append("Cédula   : ").append(id.trim()).append("\n");
        tiquete.append("Edad     : ").append(edad).append("\n");
        tiquete.append("Trámite  : ").append(tramite).append(" (Tipo ").append(tipo).append(")\n");
        tiquete.append("Fecha : ").append(fecha).append("\n");
        tiquete.append("Hora     : ").append(horaCreacion).append("\n");
        tiquete.append("------------------------------------\n");
        tiquete.append("Caja     : ").append(cajaAsignada.getNumero())
               .append(" - ").append(cajaAsignada.getDescripcionTipo()).append("\n");

        if (personasDelante == 0 && !cajaAsignada.esOcupada()) {
            tiquete.append("¡ES SU TURNO! Pase de inmediato a la caja.\n");
        } else {
            tiquete.append("Personas delante de usted: ").append(personasDelante).append("\n");
        }
        tiquete.append("====================================");

        JOptionPane.showMessageDialog(null, tiquete.toString(), "Tiquete", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- ATENDER CLIENTE (necesario para controlar el estado ocupado/libre de cada caja) ---
    private static void atenderCliente() {
        StringBuilder sb = new StringBuilder("Seleccione la caja a gestionar:\n\n");
        for (Caja c : cajas) {
            String estado = c.esOcupada() ? "OCUPADA - " + c.getClienteActual().getNombre() : "LIBRE";
            sb.append(c.getNumero()).append(") Caja ").append(c.getNumero())
              .append(" (").append(c.getTipo()).append(") - ").append(estado)
              .append(" | En fila: ").append(c.getCola().getTamanno()).append("\n");
        }

        String sel = JOptionPane.showInputDialog(null, sb.toString(), "Atender Cliente", JOptionPane.QUESTION_MESSAGE);
        if (sel == null) return;

        int numCaja;
        try {
            numCaja = Integer.parseInt(sel.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ingrese un número de caja válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (numCaja < 1 || numCaja > cantCajas) {
            JOptionPane.showMessageDialog(null, "Número de caja fuera de rango.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Caja caja = cajas[numCaja - 1];

        // Liberar caja si está ocupada
        if (caja.esOcupada()) {
            int resp = JOptionPane.showConfirmDialog(null,
                "Caja " + caja.getNumero() + " está atendiendo a: " + caja.getClienteActual().getNombre() + "\n" +
                "¿Finalizar atención?",
                "Liberar Caja", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                caja.liberarCaja();
            } else {
                return;
            }
        }

        // Llamar al siguiente
        if (caja.getCola().esVacia()) {
            JOptionPane.showMessageDialog(null, "No hay clientes en fila para la Caja " + caja.getNumero() + ".");
        } else {
            String horaAtencion = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            Nodo atendido = caja.atenderSiguiente(horaAtencion);
            JOptionPane.showMessageDialog(null,
                "Llamando a: " + atendido.getNombre() + "\n" +
                "Cédula: "     + atendido.getId()     + "\n" +
                "Trámite: "    + atendido.getTramite() + "\n" +
                "Hora: "       + horaAtencion,
                "Atendiendo - Caja " + caja.getNumero(), 1);
        }
    }

    // --- VER ESTADO DE FILAS ---
    private static void verEstadoFilas() {
        StringBuilder sb = new StringBuilder();
        sb.append("====== ESTADO DE FILAS ======\n\n");
        for (Caja c : cajas) {
            sb.append("Caja ").append(c.getNumero()).append(" [").append(c.getDescripcionTipo()).append("]\n");
            sb.append("  Estado : ").append(c.esOcupada() ? "OCUPADA - " + c.getClienteActual().getNombre() : "LIBRE").append("\n");
            sb.append("  En fila: ").append(c.getCola().getTamanno()).append("\n");
            sb.append(c.getCola().toString());
            sb.append("----------------------------\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Estado de Filas", JOptionPane.INFORMATION_MESSAGE);
    }
}
