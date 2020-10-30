package coffee3D.editor;

import coffee3D.core.IEngineModule;
import coffee3D.core.controller.IGameController;
import coffee3D.core.controller.TopViewController;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.ui.imgui.ImGuiImplementation;
import imgui.ImGui;
import imgui.ImGuiIO;

public class TestGameModule implements IEngineModule {
    private RenderScene _rootScene;
    private TopViewController controller;


    @Override
    public void LoadResources() {
        ImGuiImplementation.Get().addFont("engineContent/assets/fonts/roboto/Roboto-Medium.ttf", 60);
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
        controller = new TopViewController(_rootScene);
        Window.GetPrimaryWindow().showCursor(true);

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
        //StatHelper.DrawStats(_rootScene);
        /*
        ImGui.image(AssetManager.<Texture2D>FindAsset("mud").getTextureID(), 50, 50);



        int gridTexture = AssetManager.<Texture2D>FindAsset("mud").getTextureID();
        int grassTexture = AssetManager.<Texture2D>FindAsset("mud").getTextureID();

        if (HudUtils.BeginContainer(NodeAnchor.Get(.05f, .1f, .95f, .95f), PixelOffset.DEFAULT)) {

            HudUtils.ImageButton(
                    NodeAnchor.TOP_LEFT,
                    PixelOffset.Get(100, 20, 500, 400),
                    ButtonBehavior.Get(2.5f),
                    ImageParams.Get(gridTexture, 30.f),
                    TextParams.Get("Button 1", 5, ImColor.intToColor(255, 128, 128))
            );

            HudUtils.ImageButton(
                    NodeAnchor.BOTTOM_FILL,
                    PixelOffset.Get(40, -400, -40, -100),
                    ButtonBehavior.Get(2.5f),
                    ImageParams.Get(gridTexture, 10.f),
                    TextParams.Get("Button 2", 2, ImColor.intToColor(128, 255, 128))
            );

            HudUtils.ImageButton(
                    NodeAnchor.Get(0.5f, 0, 1, 0.3f),
                    PixelOffset.Get(40, 40, -40, -20),
                    ButtonBehavior.Get(2.5f),
                    ImageParams.Get(gridTexture, 60.f, ImColor.intToColor(255, 255, 128)),
                    TextParams.Get("Button 3", 3.5f, ImColor.intToColor(128, 128, 255))
            );

            HudUtils.ProgressBar(
                    NodeAnchor.FILL_LEFT,
                    PixelOffset.Get(5, 10, 100, -10),
                    ImageParams.Get(grassTexture, 20, ImColor.intToColor(255,0,0)),
                    ImageParams.Get(gridTexture, 20, ImColor.intToColor(0, 255, 0)),
                    ((float)Math.sin(GLFW.glfwGetTime()) + 1) / 2,
                    true
            );

        }
        HudUtils.EndContainer();


         */

    }
}
