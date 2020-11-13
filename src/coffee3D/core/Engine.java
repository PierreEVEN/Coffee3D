package coffee3D.core;

public class Engine {
    public static final int ENGINE_PRIMARY_VERSION = 1;
    public static final int ENGINE_SECONDARY_VERSION = 0;
    public static final int ENGINE_RELEASE_NUMBER = 1;

    public static String GetEngineVersion() { return ENGINE_PRIMARY_VERSION + "." + ENGINE_SECONDARY_VERSION + "." + ENGINE_RELEASE_NUMBER; }
}
