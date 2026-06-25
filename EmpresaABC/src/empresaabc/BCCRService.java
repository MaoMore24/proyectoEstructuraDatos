package empresaabc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Clase BCCRService para consumir el tipo de cambio del Banco Central
public class BCCRService {

    // URL base del servicio HTTP GET del BCCR
    private static final String URL_BASE = "https://gee.bccr.fi.cr/Indicadores/Suscripciones/WS/wsindicadoreseconomicos.asmx/ObtenerIndicadoresEconomicos";

    // Metodo para obtener el tipo de cambio (compra o venta)
    public static String obtenerTipoCambio(String indicador, String nombre, String correo, String token) {
        // Si no se proporcionaron credenciales completas, usamos directamente el fallback
        if (correo == null || correo.trim().isEmpty() || token == null || token.trim().isEmpty()) {
            return obtenerValorSimulado(indicador);
        }

        try {
            // Formatear la fecha actual al formato dd/MM/yyyy
            String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            // Construir los parámetros codificados
            String params = "?Indicador=" + URLEncoder.encode(indicador, "UTF-8")
                    + "&FechaInicio=" + URLEncoder.encode(fechaHoy, "UTF-8")
                    + "&FechaFinal=" + URLEncoder.encode(fechaHoy, "UTF-8")
                    + "&Nombre=" + URLEncoder.encode(nombre, "UTF-8")
                    + "&SubNiveles=" + URLEncoder.encode("N", "UTF-8")
                    + "&CorreoElectronico=" + URLEncoder.encode(correo, "UTF-8")
                    + "&Token=" + URLEncoder.encode(token, "UTF-8");

            URL url = new URL(URL_BASE + params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 segundos max
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new Exception("Código de respuesta HTTP incorrecto: " + status);
            }

            // Leer respuesta
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Procesar el XML recibido
            return extraerValorDeXML(response.toString(), indicador);

        } catch (Exception e) {
            System.out.println("Error al consultar BCCR (Indicador " + indicador + "): " + e.getMessage() + ". Usando fallback...");
            return obtenerValorSimulado(indicador);
        }
    }

    // Metodo para extraer el valor del XML
    private static String extraerValorDeXML(String xml, String indicador) throws Exception {
        // Reemplazar entidades XML comunes para simplificar la búsqueda
        String limpio = xml.replace("&lt;", "<")
                           .replace("&gt;", ">")
                           .replace("&amp;", "&");

        int startTag = limpio.indexOf("<NUM_VALOR>");
        int endTag = limpio.indexOf("</NUM_VALOR>");

        if (startTag != -1 && endTag != -1) {
            String valorStr = limpio.substring(startTag + 11, endTag).trim();
            // Validar que sea un número válido
            double valor = Double.parseDouble(valorStr);
            // Formatear a dos decimales
            return String.format("%.2f", valor);
        } else {
            throw new Exception("No se encontró la etiqueta <NUM_VALOR> en la respuesta del BCCR.");
        }
    }

    // Retorna valores simulados por si no hay internet o token
    private static String obtenerValorSimulado(String indicador) {
        // Tipo de cambio simulado
        if ("317".equals(indicador)) {
            return "520.45"; // Compra
        } else {
            return "526.80"; // Venta
        }
    }
}
