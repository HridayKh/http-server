import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	static String FILEPATH;

	public static void main(String[] args) {
		FILEPATH = args.length > 0 ? args[1] : "";
		new Main();
	}

	public Main() {
		System.out.println("\n\n Server Start");

		try (ServerSocket ss = new ServerSocket(4221)) {
			ss.setReuseAddress(true);

			ExecutorService pool = Executors.newFixedThreadPool(50);
			while (true) {
				Socket cs = ss.accept();
				System.out.println("\naccepted new connection\n");

				pool.submit(() -> handleClient(cs));

			}
		} catch (IOException e) {
			System.out.println("Server error: " + e.getMessage());
		}
	}

	public void handleClient(Socket cs) {
		try (cs) {
			HashMap<String, String> request = parseRequest(readStream(cs));
			String[] path = request.get("path").split("/");
			HashMap<String, String> headerMap = new HashMap<>();

			for (String headerLine : request.get("headers").split("\n")) {
				String[] parts = headerLine.split(":", 2);
				headerMap.put(parts[0].trim(), parts[1].trim());
			}

			String response = null;

			List<Route> routes = List.of(
					new Route("GET", "", (_, _) -> "HTTP/1.1 200 OK\r\n\r\n"),

					new Route("GET", "echo", (_, _) -> {
						String content = String.join("/", Arrays.copyOfRange(path, 1, path.length));
						return "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + content.length()
								+ "\r\n\r\n" + content;
					}),

					new Route("GET", "user-agent", (_, headers) -> {
						String agent = headers.getOrDefault("User-Agent", "");
						return "HTTP/1.1 200 OK\r\n" +
								"Content-Type: text/plain\r\n" +
								"Content-Length: " + agent.length() + "\r\n\r\n" +
								agent;
					}),

					new Route("GET", "files", (_, _) -> {
						String file = FILEPATH + "/" + path[1];
						String content = readFile(file);
						if (content == null) {
							return "HTTP/1.1 404 Not Found\r\n\r\n";
						} else {
							return "HTTP/1.1 200 OK\r\n" +
									"Content-Type: application/octet-stream\r\n" +
									"Content-Length: " + content.length() + "\r\n\r\n" +
									content;
						}
					}),

					new Route("POST", "files", (req, headers) -> {
						String file = FILEPATH + "/" + path[1];
						String content = req.get("body");

						String lengthStr = headers.get("Content-Length");
						if (lengthStr == null || Integer.parseInt(lengthStr) != content.length()) {
							return "HTTP/1.1 400 Bad Request\r\n\r\n";
						}
						createFile(file, content);

						return "HTTP/1.1 201 Created\r\n\r\n";
					}));

			for (Route route : routes) {
				if (request.get("method").equals(route.method) && request.get("path").startsWith(route.pathPrefix)) {
					response = route.handler.handle(request, headerMap);
					break;
				}
			}
			if (response == null) {
				if (!request.get("method").equals("GET")) {
					response = "HTTP/1.1 405 Method Not Allowed\r\n\r\n";
				} else {
					response = "HTTP/1.1 404 Not Found\r\n\r\n";
				}
			}

			cs.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			System.out.println("Client handling error: " + e.getMessage());
		}
	}

	public void createFile(String filePath, String content) {
		try {
			File file = new File(filePath);

			// Create parent directories if they don't exist
			File parentDir = file.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs(); // make all parent directories
			}

			// Now write to file
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(content);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public String readFile(String filePath) {
		try {
			byte[] bytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath));
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("File read error: " + e.getMessage());
			return null;
		}
	}

	public String[] readStream(Socket cs) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(cs.getInputStream(), StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		String line;

		int contentLength = 0;

		// Read headers
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\r\n");
			if (line.toLowerCase().startsWith("content-length:")) {
				contentLength = Integer.parseInt(line.split(":")[1].trim());
			}
			if (line.isEmpty())
				break; // End of headers
		}

		String req = sb.toString();

		// Read body
		char[] bodyChars = new char[contentLength];
		reader.read(bodyChars);
		sb.append(bodyChars);

		String body = new String(bodyChars); // separate from headers

		return new String[] { req, body };
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
