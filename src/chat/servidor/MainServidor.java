package chat.servidor;

/**
 * Clase principal del servidor que maneja las conexiones de los usuarios,
 * registra logs de actividad y controla la cantidad de conexiones activas.
 * El servidor se ejecuta en el puerto 5050 y permite un máximo de 5 conexiones simultáneas.
 * 
 * @author sdacatrinei
 */

import chat.usuario.ControladorUsuario;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainServidor {

    // Puerto en el que el servidor escuchará las conexiones entrantes
    private static final int PUERTO = 5050;

    // Límite de conexiones simultáneas permitidas
    private static final int LIMITE_CONEXIONES = 5;

    // Lista que almacena los usuarios conectados al servidor
    private static List<ControladorUsuario> usuariosConectados = new ArrayList<>();

    // Writer para registrar logs en un archivo
    private static PrintWriter logWriter;

    public static void main(String[] args) {
        // Reemplazamos System.out con un PrintStream personalizado que añade una marca de tiempo a los mensajes
        System.setOut(new CustomPrintStream(System.out));

        try {
            // Intentamos abrir el archivo de log para escritura
            logWriter = new PrintWriter(new FileWriter("log.txt", true));
        } catch (IOException e) {
            // Si ocurre un error al abrir el archivo, lo mostramos por consola
            System.out.println("Error al abrir log.txt: " + e.getMessage());
        }

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            // Imprime un mensaje indicando que el servidor está en ejecución
            log("Servidor iniciado en el puerto " + PUERTO);

            // Bucle principal que mantiene al servidor en ejecución esperando conexiones
            while (true) {
                // Si se ha alcanzado el límite de conexiones, no se aceptan más clientes
                if (usuariosConectados.size() >= LIMITE_CONEXIONES) {
                    log("Límite de conexiones alcanzado. No se puede aceptar más clientes.");
                    continue;
                }

                // Esperamos a que un cliente se conecte
                Socket socketCliente = serverSocket.accept();
                log("Nuevo usuario conectado: " + socketCliente.getInetAddress());

                // Creamos un nuevo manejador para el usuario que se conecta
                ControladorUsuario manejador = new ControladorUsuario(socketCliente, usuariosConectados);
                usuariosConectados.add(manejador); // Agregamos el manejador a la lista de usuarios conectados
                manejador.start(); // Iniciamos el hilo que manejará la comunicación con este usuario

                // Iniciamos un hilo que espera a que el manejador termine para registrar la desconexión del usuario
                new Thread(() -> {
                    try {
                        // Esperamos a que el manejador termine su ejecución
                        manejador.join();

                        // Usamos reflexión para obtener el nombre del usuario (campo privado en ControladorUsuario)
                        String nombre = "";
                        try {
                            Field field = ControladorUsuario.class.getDeclaredField("nombreUsuario");
                            field.setAccessible(true); // Permitimos el acceso a un campo privado
                            nombre = (String) field.get(manejador); // Obtenemos el nombre del usuario
                        } catch (Exception e) {
                            nombre = "Desconocido"; // En caso de error, usamos un nombre por defecto
                        }
                        // Registramos la desconexión del usuario
                        log("Usuario " + nombre + " se ha desconectado.");
                    } catch (InterruptedException e) {
                        // Si se interrumpe el hilo, lo ignoramos
                    }
                }).start(); // Iniciamos el hilo de registro de desconexión
            }
        } catch (IOException ex) {
            // Si ocurre un error al intentar iniciar el servidor, lo registramos
            log("Error al iniciar el servidor: " + ex.getMessage());
        } finally {
            // Al finalizar, cerramos el escritor del log si es que fue creado correctamente
            if (logWriter != null) {
                logWriter.close();
            }
        }
    }

    /**
     * Método que registra un mensaje con una marca de tiempo en la consola y en el archivo de log.
     * @param mensaje El mensaje que se desea registrar.
     */
    private static void log(String mensaje) {
        // Creamos un formato de hora para la marca de tiempo
        String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
        // Componemos el mensaje con la marca de tiempo
        String logMessage = "[" + timestamp + "] " + mensaje;
        // Imprimimos el mensaje en la consola
        System.out.println(logMessage);
        // También lo escribimos en el archivo de log
        if (logWriter != null) {
            logWriter.println(logMessage);
            logWriter.flush(); // Nos aseguramos de que el mensaje se grabe inmediatamente
        }
    }

    /**
     * Clase que sobrescribe PrintStream para añadir una marca de tiempo a los mensajes impresos.
     */
    public static class CustomPrintStream extends PrintStream {
        public CustomPrintStream(PrintStream out) {
            super(out, true); // Llamamos al constructor de PrintStream
        }

        @Override
        public void println(String s) {
            // Si el mensaje no comienza con una marca de tiempo, le añadimos la hora
            String modified = s;
            if (!s.startsWith("[")) {
                String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
                modified = "[" + timestamp + "] " + s;
            }
            // Imprimimos el mensaje modificado en la consola
            super.println(modified);
            // También lo registramos en el archivo de log
            if (logWriter != null) {
                logWriter.println(modified);
                logWriter.flush(); // Nos aseguramos de que se grabe inmediatamente
            }
        }
    }
}