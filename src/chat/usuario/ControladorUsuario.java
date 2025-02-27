package chat.usuario;

/**
 * Esta clase controla la conexión y la comunicación de cada usuario conectado al servidor.
 * Maneja la recepción y el envío de mensajes, la desconexión y la notificación a otros usuarios conectados.
 * Cada instancia de esta clase se ejecuta en un hilo, permitiendo que múltiples usuarios interactúen simultáneamente.
 * 
 * @author sdacatrinei
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ControladorUsuario extends Thread {

    private Socket socket; // Socket para la comunicación con el usuario
    private DataInputStream inputStream; // Flujo de entrada para recibir mensajes
    private DataOutputStream outputStream; // Flujo de salida para enviar mensajes
    private List<ControladorUsuario> usuariosConectados; // Lista de todos los usuarios conectados
    private String nombreUsuario; // Nombre del usuario conectado

    /**
     * Constructor de la clase, inicializa los flujos de entrada y salida del socket del usuario.
     * 
     * @param socket Conexión del cliente.
     * @param usuariosConectados Lista de usuarios conectados para poder reenviar mensajes.
     */
    public ControladorUsuario(Socket socket, List<ControladorUsuario> usuariosConectados) {
        this.socket = socket;
        this.usuariosConectados = usuariosConectados;
        try {
            // Inicializa los flujos de datos para la comunicación con el cliente
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            // Si ocurre un error al crear los flujos, lo mostramos en consola
            System.out.println("Error al crear el ManejadorUsuario: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Leer el nombre del usuario que se conecta
            nombreUsuario = inputStream.readUTF();
            System.out.println("[" + obtenerHora() + "] Usuario " + nombreUsuario + " listo para chatear.");
            enviarMensajeConexion(); // Notificar a los demás usuarios que este usuario se ha conectado

            while (true) {
                // Leer los mensajes enviados por el usuario
                String mensaje = inputStream.readUTF();
                if (mensaje.equals("salir()")) {
                    // Si el mensaje es "salir()", cerramos la conexión
                    cerrarConexion();
                    break;
                }

                // Mostrar el mensaje recibido en el servidor y reenviarlo a los demás usuarios
                mostrarMensaje(nombreUsuario, mensaje);
                reenviarMensaje(nombreUsuario, mensaje);
            }
        } catch (IOException ex) {
            // Si ocurre un error o el usuario se desconecta inesperadamente, lo mostramos y cerramos la conexión
            System.out.println("[" + obtenerHora() + "] " + nombreUsuario + " se ha desconectado inesperadamente.");
            cerrarConexion();
        }
    }

    /**
     * Método que reenvía el mensaje a todos los usuarios conectados, excluyendo al que envió el mensaje.
     * 
     * @param usuario Nombre del usuario que envió el mensaje.
     * @param mensaje El mensaje que se desea reenviar.
     */
    private void reenviarMensaje(String usuario, String mensaje) {
        String mensajeFormateado = "[" + obtenerHora() + "] " + usuario + ": " + mensaje;
        // Recorremos la lista de usuarios conectados y les enviamos el mensaje
        for (ControladorUsuario usuarioConectado : usuariosConectados) {
            try {
                // Verificamos que el socket del usuario esté abierto y activo antes de enviar el mensaje
                if (usuarioConectado.socket != null && !usuarioConectado.socket.isClosed()) {
                    usuarioConectado.outputStream.writeUTF(mensajeFormateado);
                    usuarioConectado.outputStream.flush(); // Aseguramos que el mensaje se envíe inmediatamente
                }
            } catch (IOException ex) {
                // Si ocurre un error al reenviar el mensaje, lo mostramos en consola
                System.out.println("Error al reenviar mensaje: " + ex.getMessage());
            }
        }
    }

    /**
     * Método para mostrar el mensaje en la consola del servidor.
     * 
     * @param usuario Nombre del usuario que envió el mensaje.
     * @param mensaje El mensaje que se desea mostrar.
     */
    private void mostrarMensaje(String usuario, String mensaje) {
        // Imprime en consola el mensaje con la hora y el nombre del usuario
        System.out.println("[" + obtenerHora() + "] " + usuario + ": " + mensaje);
    }

    /**
     * Método que notifica a los demás usuarios cuando un nuevo usuario se conecta.
     */
    private void enviarMensajeConexion() {
        String mensajeConexion = "[" + obtenerHora() + "] " + nombreUsuario + " se ha conectado";
        reenviarNotificacion(mensajeConexion); // Reenvía la notificación a todos los usuarios
    }

    /**
     * Método que notifica a los demás usuarios cuando un usuario se desconecta.
     */
    private void enviarMensajeDesconexion() {
        String mensajeDesconexion = "[" + obtenerHora() + "] " + nombreUsuario + " se ha desconectado";
        reenviarNotificacion(mensajeDesconexion); // Reenvía la notificación a todos los usuarios
    }

    /**
     * Método que reenvía una notificación a todos los usuarios conectados (excepto al emisor).
     * 
     * @param mensaje El mensaje de notificación a enviar.
     */
    private void reenviarNotificacion(String mensaje) {
        for (ControladorUsuario usuarioConectado : usuariosConectados) {
            try {
                // Verificamos que el socket esté abierto y que no sea el usuario que envía la notificación
                if (usuarioConectado.socket != null && !usuarioConectado.socket.isClosed() && usuarioConectado != this) {
                    usuarioConectado.outputStream.writeUTF(mensaje);
                    usuarioConectado.outputStream.flush(); // Aseguramos que el mensaje se envíe inmediatamente
                }
            } catch (IOException ex) {
                // Si ocurre un error al enviar la notificación, lo mostramos en consola
                System.out.println("Error al enviar notificación: " + ex.getMessage());
            }
        }
    }

    /**
     * Método para obtener la hora actual en formato HH:mm (hora y minutos).
     * 
     * @return La hora actual como un String.
     */
    private String obtenerHora() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

    /**
     * Método que cierra la conexión del usuario, enviando primero una notificación de desconexión
     * a los demás usuarios y luego cerrando el socket.
     */
    private void cerrarConexion() {
        enviarMensajeDesconexion(); // Notificar a los demás usuarios sobre la desconexión
        try {
            // Cerrar el socket y eliminar al usuario de la lista de usuarios conectados
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            usuariosConectados.remove(this); // Remover al usuario de la lista
        } catch (IOException ex) {
            // Si ocurre un error al cerrar la conexión, lo mostramos en consola
            System.out.println("Error al cerrar la conexión: " + ex.getMessage());
        }
    }
}