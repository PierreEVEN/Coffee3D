package coffee3D;

import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.Window;
import coffee3D.editor.EditorModule;

public class Main {

    public static void main(String[] args) {
        EngineSettings.ENABLE_DOUBLE_BUFFERING = true;
        Window.GetPrimaryWindow().run(new EditorModule());
    }
}