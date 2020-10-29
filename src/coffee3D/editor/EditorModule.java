package coffee3D.editor;

import coffee3D.core.controller.IGameController;
import coffee3D.core.IEngineModule;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.Window;
import coffee3D.core.ui.imgui.ImGuiImplementation;

import coffee3D.editor.controller.EditorController;

import coffee3D.editor.ui.browsers.ContentBrowser;
import coffee3D.editor.ui.EditorUI;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import coffee3D.editor.ui.tools.Console;

import imgui.*;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class EditorModule implements IEngineModule {

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
        _rootScene.loadFromFile("engineContent/truc.map");
        _controller = new EditorController(_rootScene);

        /* load default widgets */
        new LevelEditorViewport((RenderScene) _rootScene, "viewport");
        new ContentBrowser("Content browser");
        new Console("Console");

        /* Set window title */
        String vendor = glGetString(GL_VENDOR);
        String renderer = glGetString(GL_RENDERER);
        GLFW.glfwSetWindowTitle(Window.GetPrimaryWindow().getGlfwWindowHandle(), "Coffee3D Editor - " + vendor + " " + renderer);
    }

    @Override
    public IGameController GetController() { return _controller; }

    @Override
    public void DrawScene() { _rootScene.renderScene(); }

    @Override
    public void DrawUI() { EditorUI.DrawMenuBar(_rootScene); }

    @Override
    public void DrawHUD() { }
}
