package in.HridayKh.DI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.HridayKh.DI.ReflectionMetas.ConfigInjectionPoint;
import in.HridayKh.DI.ReflectionMetas.InjectPoint;
import in.HridayKh.DI.ReflectionMetas.ClassLevelPathMeta;
import in.HridayKh.DI.ReflectionMetas.MethodLevelPathMeta;
import in.HridayKh.DI.ReflectionMetas.SingletonMeta;

public class Registry {

	public static final Registry INSTANCE = new Registry();

	private Registry(){

	}

	public static Registry getInstance(){
		return INSTANCE;
	}

	/* ================= CONFIG ================= */

	// All @Config injection points
	public static final List<ConfigInjectionPoint> configInjectionPoints = new ArrayList<>();

	/* ================= DI ================= */

	// All singleton classes discovered
	public static final Map<Class<?>, SingletonMeta> singletons = new HashMap<>();

	// All @Inject field locations
	public static final List<InjectPoint> injectPoints = new ArrayList<>();

	/* ================= HTTP ================= */

	// Base paths on classes
	public static final Map<Class<?>, ClassLevelPathMeta> classLevelPaths = new HashMap<>();

	// All HTTP routes (method-level)
	public static final List<MethodLevelPathMeta> methodLevelPaths = new ArrayList<>();

}
