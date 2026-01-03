package in.HridayKh.DI.ReflectionMetas;

public class ClassLevelPathMeta {
    public final Class<?> controllerClass;
    public final String basePath;

    public ClassLevelPathMeta(Class<?> controllerClass, String basePath) {
        this.controllerClass = controllerClass;
        this.basePath = basePath;
    }
}

