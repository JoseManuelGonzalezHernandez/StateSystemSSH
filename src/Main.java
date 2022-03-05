import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Session session = null;
        ChannelExec channel = null;
        Scanner scanner = new Scanner(System.in);

        String destination;
        String username;
        String password;
        int port;

        String logFile;
        try {
            System.out.println("Introduce el dominio o la IP de la máquina:");
            destination = scanner.nextLine(); // IP: "127.0.0.1" Dominio: "localhost"

            System.out.println("Introduce el puerto de la máquina:");
            port = scanner.nextInt(); // Port: 22 - SSH

            System.out.println("Introduce su usuario:");
            username = scanner.next(); // Username: "Juan"

            System.out.println("Introduce su contraseña:");
            password = scanner.next(); // Password: "1234"

            session = new JSch().getSession(username, destination, port);
            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            System.out.println("\nINFO: Conexión creada correctamente.\n");

            boolean again = true;
            while (again) {
                System.out.println("¿Qué archivo de log desea ver? (alternatives.log | bootstrap.log | dpkg.log)");
                logFile = scanner.next();

                if (!logFile.endsWith(".log")) {
                    System.err.println("ERROR: Los archivos log tienen una extensión \".log\"\n");
                } else {
                    channel = (ChannelExec) session.openChannel("exec");

                    channel.setCommand("cat /var/log/" + logFile);

                    ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                    channel.setOutputStream(responseStream);
                    channel.connect();

                    while (channel.isConnected()) {
                        Thread.sleep(100);
                    }

                    String responseString = new String(responseStream.toByteArray());
                    if (responseStream.size() == 0) {
                        System.err.println("ERROR: No existe ningún archivo de log llamado: " + logFile + "\n");
                    } else {
                        System.out.println(responseString);
                    }
                }
                System.out.println("Si desea solicitar el contenido de otro archivo log, introduzca \"other\", en caso contrario, presione la tecla Enter.");
                scanner.nextLine(); // Scanner buffer reset.
                if (scanner.nextLine() == "") {
                    again = false;
                }
            }
            System.out.println("INFO: Conexión finalizada.");
        } catch (JSchException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
            scanner.close();
        }
    }
}
