import Core.Renderer.Window;
import Core.Types.SceneBufferData;
import Editor.EditorRenderModule;

import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) {
        Window.GetPrimaryWindow().run(new EditorRenderModule());
    }
}