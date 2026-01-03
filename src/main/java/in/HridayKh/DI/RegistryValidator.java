package in.HridayKh.DI;

import java.util.HashMap;
import java.util.Map;
import in.HridayKh.DI.ReflectionMetas.ConfigInjectionPoint;
import in.HridayKh.DI.ReflectionMetas.InjectPoint;
import in.HridayKh.DI.ReflectionMetas.MethodLevelPathMeta;
import in.HridayKh.config.ConfigLoader;

public class RegistryValidator {
	private Registry registry = null;
	private ConfigLoader config = null;

	public RegistryValidator(ConfigLoader config, Registry registry) {
		this.config = config;
		this.registry = registry;
	}

	public void validate() {
		System.out.println("Starting registry validation: routes=" + registry.methodLevelPaths.size()
				+ ", injectPoints=" + registry.injectPoints.size() + ", configInjectionPoints="
				+ registry.configInjectionPoints.size());

		Map<String, MethodLevelPathMeta> seen = new HashMap<>();
		for (MethodLevelPathMeta route : registry.methodLevelPaths) {
			String key = route.httpMethod + " " + route.fullPath;
			if (seen.containsKey(key)) {
				MethodLevelPathMeta existing = seen.get(key);
				System.err.println("Duplicate route: " + key +
						" defined in both " +
						existing.controllerClass.getName() + "." + existing.method.getName() +
						" and " +
						route.controllerClass.getName() + "." + route.method.getName());
			}
			seen.put(key, route);
		}

		for (InjectPoint ip : registry.injectPoints) {
			Class<?> depType = ip.field.getType();
			if (!registry.singletons.containsKey(depType))
				System.err.println("Unsatisfied dependency: " + depType.getName());
		}

		for (ConfigInjectionPoint cip : registry.configInjectionPoints) {
			java.util.Optional<?> opt = config.get(cip.configKey);
			if (!opt.isPresent() || opt.isEmpty())
				System.err.println("Missing config key: " + cip.configKey);
		}

		System.out.println("Completed registry validation");

	}

}
