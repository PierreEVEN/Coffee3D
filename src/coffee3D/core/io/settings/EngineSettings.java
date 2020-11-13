package coffee3D.core.io.settings;

import imgui.type.ImBoolean;

import java.io.File;

public class EngineSettings extends GameSettings {

    private static final long serialVersionUID = 2265223558592485318L;

    @Override
    public String getFileName() { return "engine"; }

    public boolean enableShadows = true;
    public boolean enablePicking = true;
    public boolean enablePostProcessing = true;
    public boolean enableStencilTest = true;

    public boolean fullscreen = false;
    public int msaaSamples = 4;
    public boolean doubleBuffering = false;
    public boolean transparentFramebuffer = false;

    public String defaultMapName = "defaultWorld";
    public File engineAssetsPath = new File("./engineContent/");
    public File gameAssetsPath = new File("./gameContent/");

    public static EngineSettings Get() {
        EngineSettings set = GameSettings.GetSettings("engine");
        return set == null ? new EngineSettings() : set;
    }
}
