package in.HridayKh.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import in.HridayKh.config.ConfigLoader;

public class Http {

	private static final Http INSTANCE = new Http();
	private static HttpServer server = null;

	private Http() {
	}

	public void createServer() {
		System.out.println("HTTP Server Starting...");

		long startTime = System.currentTimeMillis();

		int port = ConfigLoader.getInstance().getInt("server.port", 8080);

		try {
			server = HttpServer.create();
			server.bind(new InetSocketAddress(port), 0);
		} catch (IOException e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			System.out.println("HTTP Server Start Failed in " + (endTime - startTime) + " ms");
			return;
		}

		server.createContext("/", new HandleRootContext());
		server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
		server.start();

		long endTime = System.currentTimeMillis();
		System.out.println("HTTP Server Started in " + (endTime - startTime) + " ms on port " + port);
	}

	public void killServer() {
		System.out.println("Killing HTTP Server...");

		long startTime = System.currentTimeMillis();
		if (server != null) {
			server.stop(0);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("HTTP Server Killed in " + (endTime - startTime) + " ms");
	}

	public static Http getInstance() {
		return INSTANCE;
	}
}