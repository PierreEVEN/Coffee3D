package coffee3D;

import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.Window;
import coffee3D.editor.TestGameModule;

public class MainGame {

    public static void main(String[] args) {
        EngineSettings.FULLSCREEN_MODE = true;
        EngineSettings.MSAA_SAMPLES = 2;
        EngineSettings.ENABLE_DOUBLE_BUFFERING = true;
        EngineSettings.TRANSPARENT_FRAMEBUFFER = false;
        EngineSettings.ENABLE_PICKING = false;
        EngineSettings.ENABLE_SHADOWS = true;
        EngineSettings.ENABLE_POSTPROCESSING = true;

        Window.GetPrimaryWindow().run(new TestGameModule());
    }
}