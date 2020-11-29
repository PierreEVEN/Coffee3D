package coffee3D.editor;

import coffee3D.core.IEngineModule;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.Texture2D;
import coffee3D.core.controller.IGameController;
import coffee3D.core.controller.TopViewController;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.RenderSceneProperties;
import coffee3D.core.renderer.scene.RenderSceneSettings;
import coffee3D.core.types.Color;
import coffee3D.core.ui.hud.*;
import coffee3D.core.ui.tools.StatHelper;
import imgui.ImColor;
import imgui.ImGui;
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

        StatHelper.DrawStats(_rootScene);
/*

        int gridTexture = AssetManager.<Texture2D>FindAsset("whiteTexture").getTextureID();
        int grassTexture = AssetManager.<Texture2D>FindAsset("whiteTexture").getTextureID();

        if (HudUtils.BeginContainer(NodeAnchor.Get(.05f, .1f, .95f, .95f), PixelOffset.DEFAULT)) {

            HudUtils.ImageButton(
                    NodeAnchor.TOP_LEFT,
                    PixelOffset.Get(100, 20, 500, 400),
                    ButtonBehavior.Get(2.5f),
                    ImageParams.Get(-1, 20, Color.RED.asInt()),
                    TextParams.Get("Button 1", 5, ImColor.intToColor(255, 128, 128))
            );

            if (HudUtils.BorderContainer(
                    NodeAnchor.Get(.5f, 0, .5f, 1),
                    PixelOffset.Get(-400, 0, 400, 0),
                    ImageParams.Get(-1, 60.f, ImColor.intToColor(255, 255, 128))
            )) {
                HudUtils.VerticalBox(NodeAnchor.FILL, PixelOffset.DEFAULT, new IDrawContent[]{
                        () -> ImGui.button("test", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY()),
                        () -> ImGui.button("test2", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY()),
                        () -> ImGui.button("test3", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY())
                });
            }
            HudUtils.EndContainer();

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
