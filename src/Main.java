import Core.Renderer.Window;
import Editor.EditorModule;

public class Main {
    public static void main(String[] args) {
        Window.GetPrimaryWindow().run(new EditorModule());
    }
}