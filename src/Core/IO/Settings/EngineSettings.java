package Core.IO.Settings;

import imgui.type.ImBoolean;

import java.io.File;

public class EngineSettings {

    public static boolean FULLSCREEN_MODE = false;
    public static int MSAA_SAMPLES = 4;
    public static File ENGINE_ASSET_PATH = new File("./engineContent/");
    public static File GAME_ASSET_PATH = new File("./gameContent/");

    public static File DEBUG_MATERIAL_PATH = new File("engineContent/assets/shaders/debugMaterial");
    public static File OUTLINE_MATERIAL_PATH = new File("engineContent/assets/shaders/outlineMaterial");
    public static File PICK_MATERIAL_PATH = new File("engineContent/assets/shaders/pickMaterial");

    public static imgui.type.ImBoolean DRAW_DEBUG_BOUNDS = new ImBoolean(false);
    public static boolean ENABLE_DOUBLE_BUFFERING = false;
    public static boolean TRANSPARENT_FRAMEBUFFER = false;
}
