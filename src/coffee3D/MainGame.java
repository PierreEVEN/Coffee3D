package coffee3D;

import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.Window;
import coffee3D.editor.TestGameModule;

public class MainGame {

    public static void main(String[] args) {
        Window.GetPrimaryWindow().run(new TestGameModule());
    }
}