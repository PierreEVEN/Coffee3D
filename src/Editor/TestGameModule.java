package Editor;

import Core.IEngineModule;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Window;
import Core.UI.ImGuiImpl.ImGuiImplementation;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.joml.Vector4f;

public class TestGameModule implements IEngineModule {
    Scene _rootScene;

    @Override
    public void LoadResources() {

        Window.GetPrimaryWindow().setBackgroundColor(new Vector4f(0,0,0,0));

        ImGuiImplementation.Get().addFont("resources/fonts/roboto/Roboto-Medium.ttf", 60);
        ImGuiIO io = ImGui.getIO();
        io.setFontGlobalScale(0.4f);
    }

    @Override
    public void PreInitialize() {

        _rootScene = new Scene();
        _rootScene.loadFromFile("truc.map");
        Window.GetPrimaryWindow().showCursor(false);

        Window.GetPrimaryWindow().setWindowTitle("Sample game");
    }

    @Override
    public void DrawScene() {
        _rootScene.renderScene();
    }

    @Override
    public void DrawUI() {

    }

    @Override
    public void DrawHUD() {

    }
}
