import Core.IO.Settings.EngineSettings;
import Core.Renderer.Window;
import Editor.EditorModule;
import Editor.TestGameModule;

public class MainGame {

    public static void main(String[] args) {
        EngineSettings.FULLSCREEN_MODE = false;
        EngineSettings.MSAA_SAMPLES = 8;
        EngineSettings.ENABLE_DOUBLE_BUFFERING = true;
        EngineSettings.TRANSPARENT_FRAMEBUFFER = false;

        Window.GetPrimaryWindow().run(new TestGameModule());
    }
}