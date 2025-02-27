package chat.util;

/**
 * Esta clase contiene un método para calcular el hash MD5 de un mensaje.
 * MD5 (Message Digest Algorithm 5) es una función hash ampliamente utilizada para producir
 * un valor de longitud fija a partir de una cadena de texto. Se utiliza comúnmente en situaciones
 * como la validación de integridad de datos o almacenamiento de contraseñas (aunque no se recomienda
 * para seguridad crítica, ya que es susceptible a ataques de colisión).
 * 
 * @author sdacatrinei
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashMD5 {

    /**
     * Este método calcula el hash MD5 de un mensaje dado.
     * 
     * @param mensaje El texto de entrada que se desea convertir en un hash MD5.
     * @return El valor del hash MD5 como una cadena hexadecimal.
     */
    public static String calcularMD5(String mensaje) {
        try {
            // Se obtiene una instancia del algoritmo MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            // Se calcula el hash de los bytes del mensaje
            byte[] hashBytes = md.digest(mensaje.getBytes());
            
            // Se construye la cadena hexadecimal que representa el hash
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // Cada byte se convierte a un valor hexadecimal de dos dígitos
                hexString.append(String.format("%02x", b));
            }
            
            // Se devuelve el hash MD5 como una cadena de texto hexadecimal
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Si no se encuentra el algoritmo MD5 (algo que no debería ocurrir),
            // lanzamos una excepción en tiempo de ejecución con el mensaje de error
            throw new RuntimeException("Error al calcular el hash MD5: " + e.getMessage());
        }
    }
}
