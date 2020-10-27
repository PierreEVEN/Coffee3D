package Editor;

import Core.Controller.DefaultController;
import Core.Controller.IGameController;
import Core.IEngineModule;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Window;
import Core.UI.ImGuiImpl.ImGuiImplementation;
import Core.UI.Tools.StatHelper;
import imgui.ImGui;
import imgui.ImGuiIO;

public class TestGameModule implements IEngineModule {
    private RenderScene _rootScene;
    private DefaultController controller;


    @Override
    public void LoadResources() {
        ImGuiImplementation.Get().addFont("resources/fonts/roboto/Roboto-Medium.ttf", 60);
        ImGuiIO io = ImGui.getIO();
        io.setFontGlobalScale(0.4f);
    }

    @Override
    public IGameController GetController() {
        return controller;
    }

    @Override
    public void PreInitialize() {

        _rootScene = new RenderScene(true);
        _rootScene.loadFromFile("truc.map");
        controller = new DefaultController(_rootScene);
        Window.GetPrimaryWindow().showCursor(false);

        Window.GetPrimaryWindow().setWindowTitle("Sample game");
    }

    @Override
    public void DrawScene() {
        _rootScene.renderScene();
    }

    @Override
    public void DrawUI() {}

    @Override
    public void DrawHUD() {
        StatHelper.DrawStats(_rootScene);
    }
}
