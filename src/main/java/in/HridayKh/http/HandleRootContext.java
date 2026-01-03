package in.HridayKh.http;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandleRootContext implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String response = "This is the response";
		byte[] bytes = response.getBytes();

		// -1 means no content, 0 means unknown content length
		int contentLength = bytes.length == 0 ? -1 : bytes.length;

		try (OutputStream os = exchange.getResponseBody()) {
			exchange.sendResponseHeaders(200, contentLength);
			os.write(bytes);
		}
	}
}