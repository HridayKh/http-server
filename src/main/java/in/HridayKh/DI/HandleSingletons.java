package in.HridayKh.DI;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import in.HridayKh.DI.annotations.Singleton;

public class HandleSingletons {

	private Map<Class<?>, Object> singletons = new HashMap<>();

	public void register(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Singleton.class)) {
			Object instance = null;
			try {
				instance = clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			singletons.put(clazz, instance);
		}
	}

}
