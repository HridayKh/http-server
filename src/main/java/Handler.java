import java.util.HashMap;

@FunctionalInterface
interface Handler {
	String handle(HashMap<String, String> req, HashMap<String, String> headers);
}

class Route {
	String method;
	String pathPrefix;
	Handler handler;

	public Route(String method, String pathPrefix, Handler handler) {
		this.method = method;
		this.pathPrefix = pathPrefix;
		this.handler = handler;
	}
}
