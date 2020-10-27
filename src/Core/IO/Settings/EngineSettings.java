package Core.IO.Settings;

import imgui.type.ImBoolean;

import java.io.File;

public class EngineSettings {

    public static boolean FULLSCREEN_MODE = false;
    public static int MSAA_SAMPLES = 4;
    public static File DEFAULT_ASSET_PATH = new File("./resources/");
    public static String DEBUG_MATERIAL_PATH = "resources/shaders/debugMaterial";
    public static String PICK_MATERIAL_PATH = "resources/shaders/pickMaterial";
    public static imgui.type.ImBoolean DRAW_DEBUG_BOUNDS = new ImBoolean(false);
    public static boolean ENABLE_DOUBLE_BUFFERING = false;
    public static boolean TRANSPARENT_FRAMEBUFFER = false;
}
