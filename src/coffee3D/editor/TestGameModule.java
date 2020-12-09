package coffee3D.editor;

import coffee3D.core.IEngineModule;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.Texture2D;
import coffee3D.core.controller.IGameController;
import coffee3D.core.controller.TopViewController;
import coffee3D.core.io.log.Log;
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


    float[] sliderValue = new float[1];

    @Override
    public void DrawHUD() {

        //StatHelper.DrawStats(_rootScene);

/*
        int gridTexture = AssetManager.<Texture2D>FindAsset("whiteTexture").getTextureID();
        int grassTexture = AssetManager.<Texture2D>FindAsset("whiteTexture").getTextureID();


 */
        if (HudUtils.BeginContainer(NodeAnchor.Get(.05f, .1f, .95f, .95f), PixelOffset.DEFAULT)) {

            if (HudUtils.ClickableArea(
                    NodeAnchor.TOP_LEFT,
                    PixelOffset.Get(100, 20, 500, 400),
                    ButtonBehavior.Get(2.5f),
                    () -> {

                        if (HudUtils.BeginContainer(NodeAnchor.FILL, PixelOffset.Get(0,0, -50, -50))) {

                        }
                        HudUtils.EndContainer();

                        ImGui.text("test");
                        ImGui.button("COUCOUUUUU", ImGui.getContentRegionAvailX() - 50, ImGui.getContentRegionAvailY() - 50);
                    }
            )) {
                Log.Display("yes");
            }

            HudUtils.SliderFloat(NodeAnchor.TOP_CENTER, PixelOffset.Get(-200, 50, 200, 150), Color.RED, Color.BLUE, sliderValue, -50, 50, 4, 40, 50);
            HudUtils.bDrawDebugBoxes.set(true);
        }
        HudUtils.EndContainer();







    }
}
