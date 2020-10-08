import Core.Renderer.Window;
import Editor.EditorRenderModule;

public class Main {
    public static void main(String[] args) {
        Window.GetPrimaryWindow().run(new EditorRenderModule());
    }
}