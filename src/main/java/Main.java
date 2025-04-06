import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static void main(String[] args) {
		System.out.println("Logs from your program will appear here!");

		try {
			ServerSocket ss = new ServerSocket(4221);

			// Since the tester restarts your program quite often, setting SO_REUSEADDR
			// ensures that we don't run into 'Address already in use' errors
			ss.setReuseAddress(true);

			Socket cs = ss.accept(); // Wait for connection from client.

			cs.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes()); // Send a byte to the client.

			System.out.println("accepted new connection");
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}

	}
}
