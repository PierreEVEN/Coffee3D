package coffee3D.editor;

import coffee3D.core.IEngineModule;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.controller.IGameController;
import coffee3D.core.controller.TopViewController;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.RenderSceneProperties;
import coffee3D.core.renderer.scene.RenderSceneSettings;
import org.lwjgl.glfw.GLFW;

public class TestGameModule extends IEngineModule {
    private RenderScene _rootScene;
    private TopViewController controller;


    @Override
    public void LoadResources() {}

    @Override
    public IGameController GetController() {
        return controller;
    }

    @Override
    public String GetDefaultFontName() {
        return super.GetDefaultFontName();
    }

    @Override
    public void PreInitialize() {

        _rootScene = new RenderScene(RenderSceneSettings.DEFAULT_FULL_SCREEN);
        _rootScene.load(AssetManager.FindAsset(EngineSettings.Get().defaultMapName));
        controller = new TopViewController(_rootScene);
        Window.GetPrimaryWindow().showCursor(true);
        Window.GetPrimaryWindow().setWindowTitle("Sample game");
    }

    @Override
    public void DrawScene() {
        ((RenderSceneProperties)_rootScene.getProperties()).sunOrientation.identity().rotateXYZ((float) (GLFW.glfwGetTime() * .1), (float) Math.toRadians(-20), 0);
        _rootScene.renderScene();
    }

    @Override
    public void DrawUI() {}



    @Override
    public void DrawHUD() {
        //StatHelper.DrawStats(_rootScene);


        /*

        Texture2D tex = AssetManager.FindAsset("whiteTexture");


        ImGui.image(AssetManager.<Texture2D>FindAsset("whiteTexture").getTextureID(), 50, 50);

        int gridTexture = AssetManager.<Texture2D>FindAsset("whiteTexture").getTextureID();
        int grassTexture = AssetManager.<Texture2D>FindAsset("whiteTexture").getTextureID();

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
