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
        try {
            System.out.println("Introduce el dominio o la IP de la máquina:");
            destination = scanner.nextLine(); // IP: "127.0.0.1" Dominio: "localhost"

            System.out.println("Introduce el puerto de la máquina:");
            port = scanner.nextInt(); // Port: 22 - SSH

            System.out.println("Introduce su usuario:");
            username = scanner.nextLine(); // Username: "Juan"

            System.out.println("Introduce su contraseña:");
            password = scanner.nextLine(); // Password: "1234"

            session = new JSch().getSession(username, destination, port);
            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");
            
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");

            channel.setCommand("ls -l /");

            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = new String(responseStream.toByteArray());
            System.out.println(responseString);
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
