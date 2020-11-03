package coffee3D.editor;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.World;
import coffee3D.core.controller.IGameController;
import coffee3D.core.IEngineModule;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.ui.imgui.ImGuiImplementation;

import coffee3D.editor.controller.EditorController;

import coffee3D.editor.ui.browsers.ContentBrowser;
import coffee3D.editor.ui.EditorUI;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import coffee3D.editor.ui.tools.Console;
import imgui.ImGui;
import imgui.ImGuiIO;

public class EditorModule extends IEngineModule {

    private RenderScene _rootScene;
    private EditorController _controller;

    @Override
    public void LoadResources() {
        /* Load default font */
        ImGuiImplementation.Get().addFont(EngineSettings.ENGINE_ASSET_PATH + "/assets/fonts/roboto/Roboto-Medium.ttf", 60);
        ImGuiIO io = ImGui.getIO();
        io.setFontGlobalScale(0.4f);
    }

    @Override
    public void PreInitialize() {
        /* Create default scene and default controller */
        _rootScene = new RenderScene(false);
        _rootScene.load(AssetManager.FindAsset(EngineSettings.DEFAULT_MAP_NAME));
        _controller = new EditorController(_rootScene);

        /* load default widgets */
        new LevelEditorViewport((RenderScene) _rootScene, "viewport");
        new ContentBrowser("Content browser");
        new Console("Console");
    }

    @Override
    public IGameController GetController() { return _controller; }

    @Override
    public void DrawScene() {
        RenderUtils.CheckGLErrors();
        _rootScene.renderScene();
        RenderUtils.CheckGLErrors();
    }

    @Override
    public void DrawUI() { EditorUI.DrawMenuBar(_rootScene); }

    @Override
    public void DrawHUD() { }

    public static void SaveAll() {
        for (Asset as : AssetManager.GetAssets()) {
            as.save();
        }
    }
}
