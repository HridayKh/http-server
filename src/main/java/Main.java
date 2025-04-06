import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

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

			HashMap<String, String> req = parseRequest(readStream(cs));
			String[] path = req.get("path").split("/");
			HashMap<String, String> headers = new HashMap<>();

			String[] headerLines = req.get("headers").split("\n");
			for (String headerLine : headerLines) {
				String[] parts = headerLine.split(":", 2);
				headers.put(parts[0].trim(), parts[1].trim());
			}
			if (!req.get("method").equals("GET")) {
				cs.getOutputStream().write("HTTP/1.1 405 Method Not Allowed\r\n\r\n".getBytes(StandardCharsets.UTF_8));
			} else if (path[0].equals("")) {
				cs.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes(StandardCharsets.UTF_8));
			} else if (path[0].equals("echo")) {
				String content = "";
				for (int i = 1; i < path.length; i++) {
					content += path[i] + "/";
				}
				String c = content.endsWith("/") ? content.substring(0, content.length() - 1) : content;

				String response = "HTTP/1.1 200 OK\r\n" +
						"Content-Type: text/plain\r\n" +
						"Content-Length: " + c.length() + "\r\n\r\n" +
						c;
				cs.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
			} else if (path[0].equals("user-agent")) {
				String agent = headers.get("User-Agent");
				String response = "HTTP/1.1 200 OK\r\n" +
						"Content-Type: text/plain\r\n" +
						"Content-Length: " + agent.length() + "\r\n\r\n" +
						agent;
				cs.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
			} else {
				cs.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes(StandardCharsets.UTF_8));
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage() + "\n\n");
		}

		System.out.println("\naccepted new connection\n");
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
		return req.split("\r\n\r\n");
	}

	public HashMap<String, String> parseRequest(String[] request) {
		HashMap<String, String> pr = new HashMap<>();

		String[] req = request[0].split("\r\n");

		pr.put("reqStr", request[0]);

		pr.put("method", req[0].split(" ")[0]);

		String[] path = Arrays.stream(
				req[0].split(" ")[1].split("/"))
				.filter(p -> !p.isEmpty())
				.toArray(String[]::new);
		pr.put("path", String.join("/", path));

		pr.put("httpVersion", req[0].split(" ")[2].split("/")[1]);

		String[] headers = new String[req.length - 1];
		for (int i = 1; i < req.length; i++) {
			String headerLine = req[i];
			int colonIndex = headerLine.indexOf(':');
			if (colonIndex != -1) {
				String name = headerLine.substring(0, colonIndex).trim();
				String value = headerLine.substring(colonIndex + 1).trim();
				headers[i - 1] = name + ":" + value;
			} else {
				headers[i - 1] = headerLine.trim(); // fallback for weird lines
			}
		}
		pr.put("headers", String.join("\n", headers));

		pr.put("body", request.length > 1 ? request[1] : "");

		return pr;
	}

}
