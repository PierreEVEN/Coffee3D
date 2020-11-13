package coffee3D.editor;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.audio.AudioListener;
import coffee3D.core.controller.IGameController;
import coffee3D.core.IEngineModule;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.RenderSceneSettings;

import coffee3D.editor.controller.EditorController;

import coffee3D.editor.ui.browsers.ContentBrowser;
import coffee3D.editor.ui.EditorUI;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import coffee3D.editor.ui.propertyHelper.FieldWriter;
import coffee3D.editor.ui.tools.Console;

public class EditorModule extends IEngineModule {

    private RenderScene _rootScene;
    private EditorController _controller;

    @Override
    public void LoadResources() {}

    @Override
    public String GetDefaultFontName() {
        return "Roboto-Medium";
    }

    @Override
    public void PreInitialize() {

        FieldWriter.RegisterPrimitiveWriters();

        /* Create default scene and default controller */
        _rootScene = new RenderScene(RenderSceneSettings.DEFAULT_WINDOWED);
        _rootScene.load(AssetManager.FindAsset(EngineSettings.Get().defaultMapName));
        _controller = new EditorController(_rootScene);
        //AudioListener.Get().bindListener(_rootScene.getCamera());

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
    public void DrawUI() {
        EditorUI.DrawMenuBar(_rootScene);
    }

    @Override
    public void DrawHUD() { }

    public static void SaveAll() {
        for (Asset as : AssetManager.GetAssets()) {
            as.save();
        }
    }
}
