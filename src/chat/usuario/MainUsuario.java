package chat.usuario;

/**
 * Esta clase representa al cliente de chat. Cada instancia de esta clase se conecta al servidor,
 * permite al usuario enviar mensajes, recibir mensajes de otros usuarios y gestionar la conexión.
 * Utiliza un socket para la comunicación y se ejecuta en el hilo principal.
 * 
 * @author sdacatrinei
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MainUsuario {

    private Socket socket; // El socket de conexión con el servidor
    private DataInputStream inputStream; // Flujo de entrada para recibir datos del servidor
    private DataOutputStream outputStream; // Flujo de salida para enviar datos al servidor
    private String nombreUsuario; // Nombre del usuario conectado

    /**
     * Constructor para inicializar los flujos de entrada y salida del socket.
     * También se envía el nombre del usuario al servidor para que se registre en el sistema.
     * 
     * @param socket Conexión del cliente al servidor.
     * @param nombre El nombre del usuario para identificarlo en el chat.
     */
    public MainUsuario(Socket socket, String nombre) {
        this.socket = socket;
        this.nombreUsuario = nombre;
        try {
            // Inicializa los flujos de entrada y salida del socket
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            // Enviar el nombre del usuario al servidor para que sea registrado
            outputStream.writeUTF(nombre);
            outputStream.flush();
        } catch (IOException ex) {
            // Si ocurre un error al crear los flujos, mostramos el error y cerramos la conexión
            System.out.println("Error al crear el usuario: " + ex.getMessage());
            cerrarConexion();
        }
    }

    /**
     * Método para enviar mensajes al servidor.
     * Los mensajes se leen desde la consola y se envían al servidor.
     * Si el mensaje es "salir()", se cierra la conexión.
     */
    public void enviarMensaje() {
        try {
            Scanner scn = new Scanner(System.in);
            System.out.println("Ya puedes chatear, o escribe \"salir()\" para abandonar:");
            while (socket.isConnected()) {
                // Leemos el mensaje del usuario
                String mensaje = scn.nextLine();
                // Enviamos el mensaje al servidor
                outputStream.writeUTF(mensaje);
                outputStream.flush();
                if (mensaje.equals("salir()")) {
                    // Si el usuario escribe "salir()", se cierra la conexión
                    cerrarConexion();
                    break;
                }
            }
            scn.close();
        } catch (IOException ex) {
            // Si ocurre un error al enviar el mensaje, mostramos el error
            System.out.println("Error al enviar mensaje: " + ex.getMessage());
        }
    }

    /**
     * Método para recibir mensajes del servidor.
     * Este método se ejecuta en un hilo separado para permitir la recepción simultánea de mensajes
     * mientras el usuario sigue escribiendo.
     */
    public void recibirDatos() {
        new Thread(() -> {
            try {
                while (true) {
                    // Espera a recibir un mensaje del servidor
                    String mensajeRecibido = inputStream.readUTF();
                    // Muestra el mensaje recibido en la consola
                    mostrarMensaje(mensajeRecibido);
                }
            } catch (IOException ex) {
                // Si ocurre un error o la conexión se cierra, cerramos la conexión localmente
                cerrarConexion();
            }
        }).start();
    }

    /**
     * Método para mostrar los mensajes recibidos en la consola.
     * Los mensajes de conexión y desconexión son mostrados siempre, y los mensajes enviados
     * por el propio usuario son ignorados (se evita el eco de los mensajes).
     * 
     * @param mensaje El mensaje que se desea mostrar.
     */
    private void mostrarMensaje(String mensaje) {
        // Si el mensaje es una notificación de conexión o desconexión o si no es un mensaje del usuario actual
        if (mensaje.contains("se ha conectado") || mensaje.contains("se ha desconectado")
                || !mensaje.contains(nombreUsuario + ": ")) {
            System.out.println(mensaje);
        }
    }

    /**
     * Método para cerrar la conexión del cliente, cerrando los flujos y el socket.
     * Además, termina la ejecución del programa.
     */
    public synchronized void cerrarConexion() {
        try {
            // Cerramos los flujos de entrada y salida, y luego el socket
            if (socket != null && !socket.isClosed()) {
                outputStream.close();
                inputStream.close();
                socket.close();
            }
            System.out.println("Conexión cerrada.");
            System.exit(0); // Terminamos la ejecución del programa
        } catch (IOException ex) {
            // Si ocurre un error al cerrar la conexión, mostramos el error
            System.out.println("Error al cerrar la conexión: " + ex.getMessage());
        }
    }

    /**
     * Método principal del programa. Crea una conexión con el servidor y permite al usuario
     * enviar y recibir mensajes.
     * 
     * @param args Argumentos de la línea de comandos (no utilizados aquí).
     */
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        System.out.println("Bienvenido, escribe tu nombre para empezar:");
        String usuario = scn.nextLine();
        try {
            // Conectarse al servidor en localhost, puerto 5050
            Socket socket = new Socket("localhost", 5050);
            MainUsuario cliente = new MainUsuario(socket, usuario);
            // Iniciar la recepción de mensajes en un hilo
            cliente.recibirDatos();
            // Iniciar el envío de mensajes
            cliente.enviarMensaje();
        } catch (IOException ex) {
            // Si ocurre un error al crear la conexión con el servidor, lo mostramos
            System.out.println("Error al crear el socket para el usuario: " + ex.getMessage());
        }
        scn.close();
    }
}