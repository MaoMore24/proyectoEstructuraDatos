PROYECTO: SISTEMA DE GESTIÓN DE CAJAS BANCARIAS - EMPRESA ABC

CURSO: SC-304 Estructura de Datos
UNIVERSIDAD FIDÉLITAS

--------------------------------------------------
1. NÚMERO DE GRUPO ASIGNADO: Grupo # [Ingresar número aquí]
--------------------------------------------------

--------------------------------------------------
2. INTEGRANTES DEL GRUPO:
--------------------------------------------------
- Integrante 1: [Nombre Completo] - [Cédula/Carné]
- Integrante 2: [Nombre Completo] - [Cédula/Carné]
- Integrante 3: [Nombre Completo] - [Cédula/Carné]
- [Opcional] Integrante 4: [Nombre Completo] - [Cédula/Carné]

--------------------------------------------------
INSTRUCCIONES DE EJECUCIÓN (NETBEANS):
--------------------------------------------------
1. Abra NetBeans IDE.
2. Seleccione File -> Open Project y busque la carpeta "EmpresaABC".
3. Haga clic derecho sobre el proyecto "EmpresaABC" y seleccione "Clean and Build".
4. Ejecute el proyecto (haga clic en Run o F6).
5. Ingrese con las credenciales por defecto:
   - Usuario: admin
   - Contraseña: admin123
   (o bien, cajero / cajero123)

--------------------------------------------------
ESTRUCTURAS DE DATOS IMPLEMENTADAS (DESDE CERO):
--------------------------------------------------
- Cola de Prioridades (ColaPrioridad.java, Nodo.java): Lógica personalizada de ordenación por prioridad de atención (Prioridades 1, 2, 3 para casos preferenciales y Prioridad 4 para clientes regulares con FIFO).
- Grafos (Grafo.java): Implementado con matriz de adyacencia sobre arreglos bidimensionales nativos para asociar trámites con productos complementarios.
- Persistencia en Archivos: Carga/Guardado de configuraciones (prod.txt), histórico (db_historico.txt) y colas activas ante apagones (colas_temp.txt).
- Web Service BCCR (BCCRService.java): Consumo del tipo de cambio con HTTP GET nativo y mecanismo de fallback robusto.
