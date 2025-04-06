import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		System.out.println("\n\n Server Start");

		try {
			ServerSocket ss = new ServerSocket(4221);
			ss.setReuseAddress(true);
			Socket cs = ss.accept();
			String[] req = readStream(cs);
			String reqString = "Request: ";
			for (String r : req) {
				reqString += r + "\r\n";
			}
			System.out.println(reqString);
			// GET /abcdefg HTTP/1.1\r\n
			// Host: localhost:4221\r\n
			// User-Agent: curl/7.81.0\r\n
			// Accept: */*\r\n\r\n

			String method = req[0].split(" ")[0];
			String path = req[0].split(" ")[1];
			String httpVersion = req[0].split(" ")[2].split("/")[1];

			String[][] headers = new String[req.length - 1][2];
			for (int i = 1; i < req.length; i++) {
				headers[i - 1] = req[i].replace(": ", ":").split(":");
			}

			System.out.println("Method: " + method);
			System.out.println("Path: " + path);
			System.out.println("HTTP Version: " + httpVersion);
			System.out.println("Headers: ");
			for (String[] header : headers) {
				System.out.println("\t" + header[0] + ": " + header[1]);
			}

			if (method.equals("GET") && path.equals("/")) {
				cs.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
			} else {
				cs.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
			}
			System.out.println("\naccepted new connection\n");
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage() + "\n\n");
		}
	}

	public String[] readStream(Socket cs) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(cs.getInputStream(), StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\r\n");
			if (line.isEmpty())
				break;
		}
		String req = sb.toString();
		return req.split("\r\n");
	}

}
