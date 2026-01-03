package in.HridayKh.DI.ReflectionMetas;

import java.lang.reflect.Field;

public class ConfigInjectionPoint {
	public final Class<?> ownerClass;
	public final Field field;
	public final String configKey;

	public ConfigInjectionPoint(Class<?> ownerClass, Field field, String configKey) {
		this.ownerClass = ownerClass;
		this.field = field;
		this.configKey = configKey;
	}
}
