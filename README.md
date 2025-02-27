# 🔒 **Chat Encriptado**

¡Bienvenido a **Chat Encriptado**! 💬 Este proyecto crea un sistema de chat simple pero seguro, donde los mensajes entre los usuarios están **encriptados** para garantizar la privacidad y confidencialidad. Con este chat, los usuarios pueden comunicarse de manera fluida y segura.

## 🛠️ **Tecnologías utilizadas**:

### ⚙️ **Backend**:
- ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Apache Ant](https://img.shields.io/badge/Apache%20Ant-A81C7D?style=for-the-badge&logo=Apache%20Ant&logoColor=white)

### 🔐 **Seguridad y Encriptación**:
- **MD5** para la encriptación de los mensajes antes de ser enviados.
  
### 🌐 **Control de Versiones**:
- ![Git](https://img.shields.io/badge/Git-%23F05032.svg?style=for-the-badge&logo=git&logoColor=white) ![GitHub](https://img.shields.io/badge/GitHub-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)

---

## 📖 **Descripción**:

**Chat Encriptado** permite a los usuarios intercambiar mensajes de texto de forma segura y privada, ya que todos los mensajes son encriptados utilizando el algoritmo MD5. El chat está diseñado para ser simple de usar pero seguro, asegurando que la información compartida esté protegida de terceros.

### 👨🏻‍💻 **Características principales**:
- 👤 **Inicio de sesión**: Los usuarios ingresan su nombre al inicio del chat.
- 💬 **Mensajes en tiempo real**: Los usuarios pueden enviar y recibir mensajes instantáneamente.
- 🔐 **Encriptación**: Todos los mensajes se encriptan antes de ser enviados.
- 🚪 **Cerrar sesión**: Los usuarios pueden desconectarse cuando deseen.

---

## 🏁 **¿Cómo empezar?**

1. Clona este repositorio en tu máquina local.
```bash
git clone https://github.com/sdacatrinei/chat-encriptado.git
```
2. Asegúrate de tener **Java** y **Apache Ant** instalados.
3. Compila el proyecto utilizando **Ant**.
```bash
cd chat-encriptado
ant build
```
4. Ejecuta el servidor y el cliente en terminales separadas.
  • En una terminal, ejecuta el servidor:
```bash
ant run
```
 • En otra terminal, ejecuta el cliente:
```bash
ant run-client
```
5. Conéctate y empieza a chatear.

---

## ☁️ **¿Cómo funciona?**

- **Servidor**: Maneja las conexiones entre los clientes y encripta los mensajes antes de enviarlos.
- **Cliente**: Se conecta al servidor, envía y recibe mensajes encriptados en tiempo real.

---

## 💬 **Conéctate conmigo**:
Si tienes preguntas o sugerencias, no dudes en contactarme.

---

## 📃 **Notas**:
- El algoritmo MD5 es adecuado para este ejemplo simple, pero en aplicaciones reales se recomienda usar algoritmos más seguros como SHA-256 o AES.

---

¡Gracias por visitar este repositorio! 🤲🏻
