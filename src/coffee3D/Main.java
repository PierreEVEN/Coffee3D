package coffee3D;

import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.Window;
import coffee3D.editor.EditorModule;

public class Main {
    public static void main(String[] args) {
        RenderUtils.WITH_EDITOR = true;
        Window.GetPrimaryWindow().run(new EditorModule());
    }
}