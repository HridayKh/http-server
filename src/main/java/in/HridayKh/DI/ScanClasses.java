package in.HridayKh.DI;

import java.util.Set;

import org.reflections.Reflections;

import in.HridayKh.config.ConfigLoader;

public class ScanClasses {

	private static final Registry registry = null;

	

	public void scan() {
		Reflections reflections = new Reflections(
				ConfigLoader.getInstance().get("server.app.package", "in.hridaykh.sample"));

			Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
	}
}
