package empresaabc;

import javax.swing.*;
import java.awt.Dimension;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Clase principal del sistema
public class Main {

    private static String nombreBanco = "Banco ABC";
    private static int cantCajas = 3;
    private static Caja[] cajas;
    private static BaseDatos db;
    private static Grafo grafo;

    public static void main(String[] args) {
        db = new BaseDatos();

        // 1. Mostrar Login
        if (!mostrarLogin()) {
            JOptionPane.showMessageDialog(null, "Acceso denegado. Cerrando el programa.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // 2. Módulo 0: Cargar o Solicitar Configuración del Sistema
        inicializarConfiguracion();

        // 3. Inicializar Cajas según configuración
        inicializarCajas();

        // 4. Cargar colas guardadas temporalmente (Escenario de apagón)
        db.cargarColasTemporales(cajas);

        // 5. Inicializar Grafo de Productos
        grafo = new Grafo();

        // 6. Ciclo del Menú Principal
        ejecutarMenu();
    }

    // Muestra la ventana de login
    private static boolean mostrarLogin() {
        JTextField txtUsuario = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        Object[] message = {
            "Usuario:", txtUsuario,
            "Contraseña:", txtPassword
        };

        int intentos = 3;
        while (intentos > 0) {
            int option = JOptionPane.showConfirmDialog(null, message, "Login de Seguridad - Empresa ABC", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (option == JOptionPane.OK_OPTION) {
                String usuario = txtUsuario.getText();
                String password = new String(txtPassword.getPassword());
                
                if (db.validarLogin(usuario, password)) {
                    JOptionPane.showMessageDialog(null, "¡Bienvenido al sistema de Cajas, " + usuario + "!", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    intentos--;
                    JOptionPane.showMessageDialog(null, "Credenciales incorrectas. Intentos restantes: " + intentos, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                return false; // El usuario canceló
            }
        }
        return false;
    }

    // Inicializa la configuracion del banco
    private static void inicializarConfiguracion() {
        String[] config = db.cargarConfiguracion();
        if (config != null) {
            nombreBanco = config[0];
            cantCajas = Integer.parseInt(config[1]);
            JOptionPane.showMessageDialog(null, "Configuración cargada de prod.txt:\n" +
                    "Banco: " + nombreBanco + "\n" +
                    "Cajas de Atención: " + cantCajas, "Módulo 0: Configuración", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Primera ejecución
            JOptionPane.showMessageDialog(null, "Bienvenido al asistente de configuración inicial.", "Módulo 0: Configuración", JOptionPane.INFORMATION_MESSAGE);
            
            String bancoIngresado = "";
            while (bancoIngresado.trim().isEmpty()) {
                bancoIngresado = JOptionPane.showInputDialog(null, "Ingrese el nombre del Banco:", "Configuración Inicial", JOptionPane.QUESTION_MESSAGE);
                if (bancoIngresado == null) {
                    System.exit(0); // Canceló
                }
            }
            nombreBanco = bancoIngresado.trim();

            int cajasIngresadas = 0;
            while (cajasIngresadas < 3) {
                String input = JOptionPane.showInputDialog(null, "Ingrese la cantidad de cajas de atención (Mínimo 3):", "Configuración Inicial", JOptionPane.QUESTION_MESSAGE);
                if (input == null) {
                    System.exit(0); // Canceló
                }
                try {
                    cajasIngresadas = Integer.parseInt(input);
                    if (cajasIngresadas < 3) {
                        JOptionPane.showMessageDialog(null, "La cantidad mínima de cajas es 3. Intente de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Por favor ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            cantCajas = cajasIngresadas;

            // Guardar configuración
            db.configurarSistema(nombreBanco, cantCajas);
            JOptionPane.showMessageDialog(null, "Configuración guardada en prod.txt correctamente.", "Configuración Completa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Inicializa las cajas segun los tipos
    private static void inicializarCajas() {
        cajas = new Caja[cantCajas];
        // Caja 1 (índice 0): Preferencial
        cajas[0] = new Caja(1, "P");
        // Caja 2 (índice 1): Rápida (1 trámite)
        cajas[1] = new Caja(2, "A");
        // Resto de cajas: Regulares (B)
        for (int i = 2; i < cantCajas; i++) {
            cajas[i] = new Caja(i + 1, "B");
        }
    }

    // Metodo que corre el menu principal
    private static void ejecutarMenu() {
        String seleccion;
        do {
            seleccion = JOptionPane.showInputDialog(null,
                    "--- BIENVENID@ AL SISTEMA DE GESTIÓN BANCARIA DE " + nombreBanco.toUpperCase() + " ---\n\n" +
                    "1) Crear Tiquete\n" +
                    "2) Atender Cliente\n" +
                    "3) Ver Estado de Filas\n" +
                    "4) Reportes e Histórico\n" +
                    "5) Consultar Tipo de Cambio (Dólar BCCR)\n" +
                    "6) Salir\n\n" +
                    "Seleccione la opción de su interés: ");

            if (seleccion == null) {
                seleccion = "6";
            }

            switch (seleccion) {
                case "1":
                    moduloCreacionTiquetes();
                    break;
                case "2":
                    moduloAtencionCliente();
                    break;
                case "3":
                    mostrarEstadoFilas();
                    break;
                case "4":
                    mostrarReportes();
                    break;
                case "5":
                    moduloTipoCambio();
                    break;
                case "6":
                    db.guardarColasTemporales(cajas);
                    JOptionPane.showMessageDialog(null, "¡Gracias por utilizar el sistema bancario!", "Cerrando Sistema", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Seleccione una opción válida", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } while (!seleccion.equals("6"));
    }

    // Modulo para crear un tiquete de cliente
    private static void moduloCreacionTiquetes() {
        // 1. Solicitar Datos del Cliente
        String nombre = "";
        while (nombre.trim().isEmpty()) {
            nombre = JOptionPane.showInputDialog(null, "Ingrese el nombre del cliente:", "Creación de Tiquete", JOptionPane.QUESTION_MESSAGE);
            if (nombre == null) return; // Canceló
        }
        nombre = nombre.trim();

        String id = "";
        while (id.trim().isEmpty()) {
            id = JOptionPane.showInputDialog(null, "Ingrese la Cédula/ID del cliente:", "Creación de Tiquete", JOptionPane.QUESTION_MESSAGE);
            if (id == null) return;
        }
        id = id.trim();

        int edad = -1;
        while (edad < 0 || edad > 120) {
            String input = JOptionPane.showInputDialog(null, "Ingrese la edad del cliente:", "Creación de Tiquete", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return;
            try {
                edad = Integer.parseInt(input);
                if (edad < 0 || edad > 120) {
                    JOptionPane.showMessageDialog(null, "Edad fuera de rango permitido (0-120).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Por favor ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 2. Determinar Tipo de Cliente y Prioridad
        String tipo = "B";
        int prioridad = 4; // Por defecto regular

        int respPref = JOptionPane.showConfirmDialog(null, "¿El cliente califica para atención Preferencial?\n(Embarazo, Discapacidad, Adulto Mayor, Empresarial)", "Condición Preferencial", JOptionPane.YES_NO_OPTION);
        if (respPref == JOptionPane.YES_OPTION) {
            tipo = "P";
            String seleccionPref = JOptionPane.showInputDialog(null,
                    "Seleccione la condición preferencial:\n" +
                    "1) Discapacidad / Embarazo\n" +
                    "2) Adulto Mayor\n" +
                    "3) Empresarial\n\n" +
                    "Digite una opción (1-3): ");

            if (seleccionPref != null) {
                if (seleccionPref.equals("1")) {
                    prioridad = 1; // Máxima
                } else if (seleccionPref.equals("2")) {
                    prioridad = 2;
                } else if (seleccionPref.equals("3")) {
                    prioridad = 3;
                } else {
                    JOptionPane.showMessageDialog(null, "Opción inválida. Se asignará prioridad Empresarial (3) por defecto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    prioridad = 3;
                }
            } else {
                return; // Canceló
            }
        } else {
            // Si no es preferencial, preguntar por cantidad de trámites
            String seleccionCant = JOptionPane.showInputDialog(null,
                    "Seleccione la cantidad de trámites a realizar:\n" +
                    "1) Un solo trámite (Caja Rápida)\n" +
                    "2) Dos o más trámites (Caja Regular)\n\n" +
                    "Digite una opción (1-2): ");

            if (seleccionCant != null) {
                if (seleccionCant.equals("1")) {
                    tipo = "A";
                } else {
                    tipo = "B";
                }
            } else {
                return; // Canceló
            }
        }

        // 3. Seleccionar trámite específico
        String seleccionTramite = JOptionPane.showInputDialog(null,
                "Seleccione el trámite a realizar:\n" +
                "1) Depósitos\n" +
                "2) Retiros\n" +
                "3) Cambio de Divisas\n\n" +
                "Digite una opción (1-3): ");
        
        if (seleccionTramite == null) return;
        String tramiteSelected = "";
        if (seleccionTramite.equals("1")) {
            tramiteSelected = "Depósitos";
        } else if (seleccionTramite.equals("2")) {
            tramiteSelected = "Retiros";
        } else if (seleccionTramite.equals("3")) {
            tramiteSelected = "Cambio de Divisas";
        } else {
            JOptionPane.showMessageDialog(null, "Opción inválida. Se canceló la creación del tiquete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Crear Nodo Tiquete con Hora de Creación
        String horaCreacion = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Nodo nuevoCliente = new Nodo(nombre, id, edad, horaCreacion, tramiteSelected, tipo, prioridad);

        // 5. Asignar Caja y Calcular Fila
        Caja cajaAsignada = null;
        int personasDelante = 0;

        if ("P".equals(tipo)) {
            // Caja Preferencial es Caja 1 (índice 0)
            cajaAsignada = cajas[0];
        } else if ("A".equals(tipo)) {
            // Caja Rápida es Caja 2 (índice 1)
            cajaAsignada = cajas[1];
        } else {
            // Cajas Regulares (índice 2 en adelante)
            // Asignar a la caja con la cola de menor tamaño
            int menorTamanno = Integer.MAX_VALUE;
            int idxMenorCaja = 2;
            for (int i = 2; i < cantCajas; i++) {
                int tam = cajas[i].getCola().getTamanno();
                if (tam < menorTamanno) {
                    menorTamanno = tam;
                    idxMenorCaja = i;
                }
            }
            cajaAsignada = cajas[idxMenorCaja];
        }

        personasDelante = cajaAsignada.getCola().getTamanno();
        cajaAsignada.getCola().encolar(nuevoCliente);

        // Actualizar persistencia temporal de las filas en disco
        db.guardarColasTemporales(cajas);

        // 6. Impresión de Tiquete
        StringBuilder tiqueteMsg = new StringBuilder();
        tiqueteMsg.append("================ TIQUETE GENERADO ================\n");
        tiqueteMsg.append("Cliente: ").append(nombre).append(" | Cédula: ").append(id).append("\n");
        tiqueteMsg.append("Trámite: ").append(tramiteSelected).append(" (Tipo ").append(tipo).append(")\n");
        tiqueteMsg.append("Hora de Creación: ").append(horaCreacion).append("\n");
        tiqueteMsg.append("-------------------------------------------------\n");
        tiqueteMsg.append("Caja Asignada: Caja ").append(cajaAsignada.getNumero())
                  .append(" (").append(cajaAsignada.getDescripcionTipo()).append(")\n");

        if (personasDelante == 0 && !cajaAsignada.esOcupada()) {
            tiqueteMsg.append("¡ES SU TURNO DE ATENCIÓN! Pase de inmediato a la caja.\n");
        } else {
            tiqueteMsg.append("Personas esperando por delante de usted: ").append(personasDelante).append("\n");
        }
        tiqueteMsg.append("=================================================");

        JOptionPane.showMessageDialog(null, tiqueteMsg.toString(), "Tiquete Impreso", JOptionPane.INFORMATION_MESSAGE);
    }

    // Modulo para que el cajero atienda al siguiente cliente
    private static void moduloAtencionCliente() {
        StringBuilder sb = new StringBuilder("Seleccione la caja para gestionar atención:\n\n");
        for (int i = 0; i < cantCajas; i++) {
            Caja c = cajas[i];
            String estado = c.esOcupada() ? "OCUPADA (Con: " + c.getClienteActual().getNombre() + ")" : "LIBRE";
            sb.append(c.getNumero()).append(") Caja ").append(c.getNumero())
              .append(" (").append(c.getTipo()).append(") - ").append(estado)
              .append(" | Cola: ").append(c.getCola().getTamanno()).append("\n");
        }
        sb.append("\nDigite el número de caja a gestionar: ");

        String seleccion = JOptionPane.showInputDialog(null, sb.toString());
        if (seleccion == null || seleccion.trim().isEmpty()) return;

        int numCaja = -1;
        try {
            numCaja = Integer.parseInt(seleccion.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese un número de caja válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (numCaja < 1 || numCaja > cantCajas) {
            JOptionPane.showMessageDialog(null, "El número de caja está fuera de rango.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Caja caja = cajas[numCaja - 1];

        // 1. Si la caja está ocupada, se debe liberar primero (finalizar servicio)
        if (caja.esOcupada()) {
            Nodo clienteAtendido = caja.getClienteActual();
            int resp = JOptionPane.showConfirmDialog(null,
                    "La Caja " + caja.getNumero() + " está ocupada atendiendo a:\n" +
                    clienteAtendido.getNombre() + " (ID: " + clienteAtendido.getId() + ")\n" +
                    "¿Desea dar por finalizada la atención y registrarla en el histórico?",
                    "Liberar Caja",
                    JOptionPane.YES_NO_OPTION);

            if (resp == JOptionPane.YES_OPTION) {
                // Registrar en la "base de datos" de histórico
                db.registrarHistorico(clienteAtendido, caja.getNumero());
                caja.liberarCaja();
                db.guardarColasTemporales(cajas);
                JOptionPane.showMessageDialog(null, "Atención registrada e histórico guardado con éxito.", "Caja Liberada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                return; // Si decide no liberarlo, se queda ahí
            }
        }

        // 2. Llamar al siguiente cliente en la fila
        if (caja.getCola().esVacia()) {
            JOptionPane.showMessageDialog(null, "No hay clientes en fila para la Caja " + caja.getNumero() + ".", "Cola Vacía", JOptionPane.WARNING_MESSAGE);
        } else {
            String horaAtencion = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            Nodo clienteLlamado = caja.atenderSiguiente(horaAtencion);
            db.guardarColasTemporales(cajas);

            if (clienteLlamado != null) {
                // Consultar en el Grafo el producto complementario a ofrecer
                String productoSugerido = grafo.obtenerProductoComplementario(clienteLlamado.getTramite());

                // Notificación emergente al cajero para recordarle ofrecer el producto complementario
                StringBuilder popupMsg = new StringBuilder();
                popupMsg.append("================ LLAMANDO CLIENTE ================\n");
                popupMsg.append("Llamando a: ").append(clienteLlamado.getNombre()).append("\n");
                popupMsg.append("Cédula/ID: ").append(clienteLlamado.getId()).append("\n");
                popupMsg.append("Trámite: ").append(clienteLlamado.getTramite()).append("\n");
                popupMsg.append("-------------------------------------------------\n");
                popupMsg.append("¡ATENCIÓN CAJERO! Recuerde ofrecer:\n");
                popupMsg.append("👉 \"").append(productoSugerido).append("\"\n");
                popupMsg.append("=================================================");

                JOptionPane.showMessageDialog(null, popupMsg.toString(), "Llamada de Cliente - Caja " + caja.getNumero(), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Muestra el estado actual de las colas en las cajas
    private static void mostrarEstadoFilas() {
        StringBuilder sb = new StringBuilder();
        sb.append("================ ESTADO DE FILAS EN TIEMPO REAL ================\n\n");
        for (Caja caja : cajas) {
            sb.append("Caja ").append(caja.getNumero()).append(" [Tipo ").append(caja.getTipo())
              .append(" - ").append(caja.getDescripcionTipo()).append("]\n");
            
            if (caja.esOcupada()) {
                sb.append("  ➜ ESTADO: OCUPADA atendiendo a: ").append(caja.getClienteActual().getNombre())
                  .append(" (ID: ").append(caja.getClienteActual().getId()).append(")\n");
            } else {
                sb.append("  ➜ ESTADO: LIBRE\n");
            }
            
            sb.append("  ➜ CLIENTES EN FILA: ").append(caja.getCola().getTamanno()).append("\n");
            if (caja.getCola().esVacia()) {
                sb.append("      (Sin clientes esperando)\n");
            } else {
                sb.append(caja.getCola().toString());
            }
            sb.append("\n-----------------------------------------------------------------------\n\n");
        }

        // Usar un JTextArea dentro de un JScrollPane para que se pueda hacer scroll
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setRows(20);
        textArea.setColumns(50);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(null, scrollPane, "Estado de Filas", JOptionPane.PLAIN_MESSAGE);
    }

    // Muestra los reportes del historico de atencion
    private static void mostrarReportes() {
        String reporte = db.generarReporteHistorico();
        
        JTextArea textArea = new JTextArea(reporte);
        textArea.setEditable(false);
        textArea.setRows(20);
        textArea.setColumns(60);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 450));

        JOptionPane.showMessageDialog(null, scrollPane, "Reportes Históricos de Atención", JOptionPane.PLAIN_MESSAGE);
    }

    // Modulo para consultar el tipo de cambio al BCCR
    private static void moduloTipoCambio() {
        // Pedir credenciales de consulta (opcional, si se cancela usa mock de fallback)
        JTextField txtNombre = new JTextField("Banco ABC");
        JTextField txtCorreo = new JTextField();
        JTextField txtToken = new JPasswordField();
        Object[] message = {
            "Nombre del Suscriptor:", txtNombre,
            "Correo Electrónico (Registrado en BCCR):", txtCorreo,
            "Token de Suscripción del BCCR:", txtToken
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Consulta Web Service BCCR", JOptionPane.OK_CANCEL_OPTION);
        
        String nombre = "Banco ABC";
        String correo = "";
        String token = "";

        if (option == JOptionPane.OK_OPTION) {
            nombre = txtNombre.getText();
            correo = txtCorreo.getText();
            token = new String(((JPasswordField) txtToken).getPassword());
        } else {
            // Si el usuario cancela, no intentamos el Web Service real y mostramos directamente el simulado.
            correo = ""; 
            token = "";
        }

        JOptionPane.showMessageDialog(null, "Consultando tipo de cambio al Banco Central de Costa Rica...\nPor favor espere.", "Consulta en Progreso", JOptionPane.INFORMATION_MESSAGE);

        // Consultar Compra (Indicador 317)
        String compra = BCCRService.obtenerTipoCambio("317", nombre, correo, token);
        // Consultar Venta (Indicador 318)
        String venta = BCCRService.obtenerTipoCambio("318", nombre, correo, token);

        StringBuilder sb = new StringBuilder();
        sb.append("================ TIPO DE CAMBIO DEL DÓLAR ================\n\n");
        sb.append("  💵 Compra: ₡").append(compra).append("\n");
        sb.append("  💵 Venta:  ₡").append(venta).append("\n\n");
        
        if (correo.isEmpty() || token.isEmpty()) {
            sb.append("  ⚠️ Nota: Se están mostrando valores SIMULADOS de referencia.\n");
            sb.append("  Para conectarse al servicio oficial en tiempo real, complete\n");
            sb.append("  su Correo y Token en el diálogo de consulta.");
        } else {
            sb.append("  ✓ Datos obtenidos en tiempo real del Web Service del BCCR.");
        }
        sb.append("\n=======================================================");

        JOptionPane.showMessageDialog(null, sb.toString(), "Tipo de Cambio al Día", JOptionPane.INFORMATION_MESSAGE);
    }
}
