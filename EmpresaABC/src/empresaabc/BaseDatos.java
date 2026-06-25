package empresaabc;

import java.io.*;

// Clase BaseDatos para manejar los archivos de texto (Persistencia)
public class BaseDatos {
    private static final String ARCHIVO_PROD = "prod.txt";
    private static final String ARCHIVO_HISTORICO = "db_historico.txt";
    private static final String ARCHIVO_COLAS = "colas_temp.txt";
    private static final String ARCHIVO_USUARIOS = "usuarios.txt";

    public BaseDatos() {
        inicializarUsuariosPorDefecto();
    }

    // Inicializa los usuarios por defecto (admin y cajero)
    private void inicializarUsuariosPorDefecto() {
        File file = new File(ARCHIVO_USUARIOS);
        if (!file.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("admin;admin123");
                pw.println("cajero;cajero123");
            } catch (IOException e) {
                System.out.println("Error al crear usuarios por defecto: " + e.getMessage());
            }
        }
    }

    // Valida si el usuario y contraseña son correctos
    public boolean validarLogin(String usuario, String password) {
        File file = new File(ARCHIVO_USUARIOS);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 2) {
                    if (partes[0].trim().equals(usuario) && partes[1].trim().equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo de usuarios: " + e.getMessage());
        }
        return false;
    }

    // Guarda el nombre del banco y cantidad de cajas en prod.txt
    public void configurarSistema(String banco, int cantCajas) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_PROD))) {
            pw.println(banco);
            pw.println(cantCajas);
        } catch (IOException e) {
            System.out.println("Error al guardar la configuración: " + e.getMessage());
        }
    }

    // Carga la configuracion de prod.txt
    public String[] cargarConfiguracion() {
        File file = new File(ARCHIVO_PROD);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String banco = br.readLine();
            String cantCajasStr = br.readLine();
            if (banco != null && cantCajasStr != null) {
                return new String[]{banco.trim(), cantCajasStr.trim()};
            }
        } catch (IOException e) {
            System.out.println("Error al cargar la configuración: " + e.getMessage());
        }
        return null;
    }

    // Registra un tiquete atendido en db_historico.txt
    public void registrarHistorico(Nodo cliente, int numCaja) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_HISTORICO, true))) {
            pw.println(cliente.getId() + ";" +
                       cliente.getNombre() + ";" +
                       cliente.getEdad() + ";" +
                       cliente.getHoraCreacion() + ";" +
                       cliente.getHoraAtencion() + ";" +
                       cliente.getTramite() + ";" +
                       cliente.getTipo() + ";" +
                       numCaja);
        } catch (IOException e) {
            System.out.println("Error al registrar tiquete en histórico: " + e.getMessage());
        }
    }

    // Guarda el estado de las colas en colas_temp.txt por si ocurre un apagón
    public void guardarColasTemporales(Caja[] cajas) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_COLAS))) {
            for (Caja caja : cajas) {
                if (caja == null) continue;

                // 1. Guardar cliente actual si está siendo atendido
                if (caja.esOcupada()) {
                    Nodo act = caja.getClienteActual();
                    pw.println(caja.getNumero() + ";ACTUAL;" +
                               act.getNombre() + ";" +
                               act.getId() + ";" +
                               act.getEdad() + ";" +
                               act.getHoraCreacion() + ";" +
                               act.getHoraAtencion() + ";" +
                               act.getTramite() + ";" +
                               act.getTipo() + ";" +
                               act.getPrioridad());
                }

                // 2. Guardar clientes en cola
                ColaPrioridad col = caja.getCola();
                if (!col.esVacia()) {
                    Nodo aux = col.getPrimero();
                    while (aux != null) {
                        pw.println(caja.getNumero() + ";COLA;" +
                                   aux.getNombre() + ";" +
                                   aux.getId() + ";" +
                                   aux.getEdad() + ";" +
                                   aux.getHoraCreacion() + ";" +
                                   aux.getHoraAtencion() + ";" +
                                   aux.getTramite() + ";" +
                                   aux.getTipo() + ";" +
                                   aux.getPrioridad());
                        aux = aux.getSig();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar colas temporales: " + e.getMessage());
        }
    }

    // Recupera las colas del archivo colas_temp.txt
    public void cargarColasTemporales(Caja[] cajas) {
        File file = new File(ARCHIVO_COLAS);
        if (!file.exists() || file.length() == 0) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 10) {
                    int numCaja = Integer.parseInt(partes[0]);
                    String estado = partes[1];
                    String nombre = partes[2];
                    String id = partes[3];
                    int edad = Integer.parseInt(partes[4]);
                    String horaCreacion = partes[5];
                    String horaAtencion = partes[6];
                    String tramite = partes[7];
                    String tipo = partes[8];
                    int prioridad = Integer.parseInt(partes[9]);

                    // Buscar la caja correspondiente
                    Caja cajaDestino = null;
                    for (Caja caja : cajas) {
                        if (caja != null && caja.getNumero() == numCaja) {
                            cajaDestino = caja;
                            break;
                        }
                    }

                    if (cajaDestino != null) {
                        Nodo nuevoNodo = new Nodo(nombre, id, edad, horaCreacion, tramite, tipo, prioridad);
                        nuevoNodo.setHoraAtencion(horaAtencion);

                        if ("ACTUAL".equals(estado)) {
                            cajaDestino.setClienteActual(nuevoNodo);
                        } else if ("COLA".equals(estado)) {
                            cajaDestino.getCola().encolar(nuevoNodo);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar colas temporales: " + e.getMessage());
        }
    }

    // Borra el archivo temporal
    public void limpiarColasTemporales() {
        File file = new File(ARCHIVO_COLAS);
        if (file.exists()) {
            file.delete();
        }
    }

    // Lee db_historico.txt y genera un reporte resumido
    public String generarReporteHistorico() {
        File file = new File(ARCHIVO_HISTORICO);
        if (!file.exists() || file.length() == 0) {
            return "No hay registros históricos de atención.";
        }

        int totalAtendidos = 0;
        int preferenciales = 0;
        int unTramite = 0;
        int variosTramites = 0;

        int depositos = 0;
        int retiros = 0;
        int divisas = 0;

        StringBuilder listado = new StringBuilder("--- Listado de Clientes Atendidos ---\n");
        listado.append(String.format("%-10s | %-15s | %-5s | %-8s | %-8s | %-12s | %-12s | %-5s\n",
                "ID", "Nombre", "Edad", "Creado", "Atendido", "Trámite", "Tipo", "Caja"));
        listado.append("---------------------------------------------------------------------------------------------------------\n");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 8) {
                    totalAtendidos++;
                    String id = partes[0];
                    String nombre = partes[1];
                    String edad = partes[2];
                    String creado = partes[3];
                    String atendido = partes[4];
                    String tramite = partes[5];
                    String tipo = partes[6];
                    String caja = partes[7];

                    // Conteos por tipo
                    if ("P".equalsIgnoreCase(tipo)) preferenciales++;
                    else if ("A".equalsIgnoreCase(tipo)) unTramite++;
                    else if ("B".equalsIgnoreCase(tipo)) variosTramites++;

                    // Conteos por trámite
                    if ("Depósitos".equalsIgnoreCase(tramite)) depositos++;
                    else if ("Retiros".equalsIgnoreCase(tramite)) retiros++;
                    else if ("Cambio de Divisas".equalsIgnoreCase(tramite)) divisas++;

                    listado.append(String.format("%-10s | %-15s | %-5s | %-8s | %-8s | %-12s | %-12s | %-5s\n",
                            id, nombre, edad, creado, atendido, tramite, tipo, caja));
                }
            }
        } catch (IOException e) {
            return "Error al leer histórico: " + e.getMessage();
        }

        StringBuilder reporteCompleto = new StringBuilder();
        reporteCompleto.append("================ REPORTES DE HISTÓRICO ================\n");
        reporteCompleto.append("Total de clientes atendidos: ").append(totalAtendidos).append("\n\n");
        
        reporteCompleto.append("--- Clasificación por Tipo de Cliente ---\n");
        reporteCompleto.append("  - Preferenciales (P): ").append(preferenciales).append("\n");
        reporteCompleto.append("  - Trámite Rápido (A): ").append(unTramite).append("\n");
        reporteCompleto.append("  - Multi-trámite Regular (B): ").append(variosTramites).append("\n\n");

        reporteCompleto.append("--- Clasificación por Tipo de Trámite ---\n");
        reporteCompleto.append("  - Depósitos: ").append(depositos).append("\n");
        reporteCompleto.append("  - Retiros: ").append(retiros).append("\n");
        reporteCompleto.append("  - Cambio de Divisas: ").append(divisas).append("\n\n");

        reporteCompleto.append(listado.toString());

        return reporteCompleto.toString();
    }
}
