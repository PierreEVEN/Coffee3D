import Core.IO.Settings.EngineSettings;
import Core.Renderer.Window;
import Editor.EditorModule;
import Editor.TestGameModule;

public class MainGame {

    public static void main(String[] args) {
        EngineSettings.FULLSCREEN_MODE = true;
        EngineSettings.MSAA_SAMPLES = 8;

        Window.GetPrimaryWindow().run(new TestGameModule());
    }
}