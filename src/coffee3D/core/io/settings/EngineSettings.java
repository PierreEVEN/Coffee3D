package coffee3D.core.io.settings;

import imgui.type.ImBoolean;

import java.io.File;

public class EngineSettings {

    public static boolean ENABLE_SHADOWS = true;
    public static boolean ENABLE_PICKING = true;
    public static boolean ENABLE_POSTPROCESSING = true;

    public static boolean FULLSCREEN_MODE = false;
    public static int MSAA_SAMPLES = 4;
    public static File ENGINE_ASSET_PATH = new File("./engineContent/");
    public static File GAME_ASSET_PATH = new File("./gameContent/");

    public static File POST_PROCESS_MATERIAL = new File("engineContent/assets/shaders/postProcessInternal");
    public static File SHADOW_MATERIAL_PATH = new File("engineContent/assets/shaders/internal/shadowShader");
    public static File DEBUG_MATERIAL_PATH = new File("engineContent/assets/shaders/debugMaterial");
    public static File OUTLINE_MATERIAL_PATH = new File("engineContent/assets/shaders/outlineMaterial");
    public static File PICK_MATERIAL_PATH = new File("engineContent/assets/shaders/pickMaterial");
    public static String DEFAULT_MAP_NAME = "defaultWorld";

    public static imgui.type.ImBoolean DRAW_DEBUG_BOUNDS = new ImBoolean(false);
    public static boolean ENABLE_DOUBLE_BUFFERING = false;
    public static boolean TRANSPARENT_FRAMEBUFFER = false;
}
