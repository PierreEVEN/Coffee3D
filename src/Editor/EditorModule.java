package Editor;

import Core.Assets.AssetManager;
import Core.Assets.Material;
import Core.Assets.StaticMesh;
import Core.Assets.Texture2D;
import Core.IEngineModule;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.Renderer.Window;
import Core.UI.SubWindows.DemoWindow;
import Editor.UI.Browsers.ContentBrowser;
import Editor.UI.Browsers.FileBrowser;
import Editor.UI.LevelEditor.LevelEditorViewport;
import Editor.UI.SceneViewport;
import Editor.UI.Browsers.ResourcesViewer;
import Editor.UI.Tools.StyleEditor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class EditorModule implements IEngineModule {

    private Scene _rootScene;

    @Override
    public void LoadResources() {
        new Texture2D("gridTexture", "resources/textures/defaultGrid.png");
        new Texture2D("grass", "resources/textures/grassSeamless.png");
        new Material("testMat", "resources/shaders/shader", new String[] {"gridTexture"});
        new Material("matWeird", "resources/shaders/shader", new String[] {"grass"});
        new StaticMesh("test", "resources/models/Tram.fbx", new String[] { "testMat" });
        new StaticMesh("cube", "resources/models/cube.fbx", new String[] { "matWeird" });
    }

    @Override
    public void PreInitialize() {
        new FileBrowser("test", new String[] {"png"});

        _rootScene = new RenderScene(800, 600);

        SceneComponent root = new SceneComponent(new Vector3f(0,0,0), new Quaternionf().identity(), new Vector3f(1,1,1));
        root.attachToScene(_rootScene);
        Random rnd = new Random();
        for (int i = 0; i < 1; ++i) {
            float range = 200;
            StaticMeshComponent parent = new StaticMeshComponent(
                    AssetManager.FindAsset("test"),
                    new Vector3f(rnd.nextFloat() * range - range / 2 , rnd.nextFloat() * range - range / 2, rnd.nextFloat() * range - range / 2),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 0, 1), 10),
                    new Vector3f(1, 1, 1)
            );
            parent.attachToComponent(root);

            new StaticMeshComponent(
                    AssetManager.FindAsset("cube"),
                    new Vector3f(0, 0, 2),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 0, 0), 25),
                    new Vector3f(1.5f, 0.8f, 1)
            ).attachToComponent(parent);

            StaticMeshComponent subChild = new StaticMeshComponent(
                    AssetManager.FindAsset("cube"),
                    new Vector3f(0, 4, 1),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 2, 0).normalize(), 8789),
                    new Vector3f(1.5f, 0.8f, 1.4f)
            );
            subChild.attachToComponent(parent);

            new StaticMeshComponent(
                    AssetManager.FindAsset("cube"),
                    new Vector3f(3, 2, 1),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 2, 0).normalize(), 489),
                    new Vector3f(1.1f, 0.8f, 0.02f)
            ).attachToComponent(subChild);
        }

        String vendor = glGetString(GL_VENDOR);
        String renderer = glGetString(GL_RENDERER);

        GLFW.glfwSetWindowTitle(Window.GetPrimaryWindow().getGlfwWindowHandle(), "Coffee3D Editor - " + vendor + " " + renderer);

    }

    @Override
    public void DrawScene() {

        Material foundMat = AssetManager.FindAsset("testMat");
        if (foundMat != null) {
            foundMat.use(_rootScene);
            foundMat.getShader().setMatrixParameter("view", _rootScene.getCamera().getViewMatrix());
            foundMat.getShader().setMatrixParameter("projection", _rootScene.getProjection());
        }
        foundMat = AssetManager.FindAsset("matWeird");
        if (foundMat != null) {
            foundMat.use(_rootScene);
            foundMat.getShader().setMatrixParameter("view", _rootScene.getCamera().getViewMatrix());
            foundMat.getShader().setMatrixParameter("projection", _rootScene.getProjection());
        }

        _rootScene.renderScene();
    }

    @Override
    public void DrawUI() {
        ImGui.setNextWindowPos(0, 20);
        ImGui.setNextWindowSize(Window.GetPrimaryWindow().getPixelWidth(), Window.GetPrimaryWindow().getPixelHeight() - 20);
        if (ImGui.begin("Master Window", ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoInputs | ImGuiWindowFlags.NoBringToFrontOnFocus)) {
            ImGui.dockSpace(ImGui.getID("Master dockSpace"), 0.f, 0.f, ImGuiDockNodeFlags.PassthruCentralNode);
        }
        ImGui.end();

        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Files")) {
                if (ImGui.menuItem("save all")) { }
                if (ImGui.menuItem("quit")) {
                    Window.GetPrimaryWindow().close();
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Window")) {
                if (ImGui.menuItem("Viewport 1")) new LevelEditorViewport((RenderScene) _rootScene, "Scene viewport");
                if (ImGui.menuItem("Content browser")) new ContentBrowser("Content browser");
                if (ImGui.menuItem("Resource viewer")) new ResourcesViewer("resource viewer");
                if (ImGui.menuItem("Style editor")) new StyleEditor("Style editor");
                if (ImGui.menuItem("Demo window")) new DemoWindow("Demo window");
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Import")) {
                if (ImGui.menuItem("Static mesh")) Log.Display("not implemented yet");
                if (ImGui.menuItem("Material")) Log.Display("not implemented yet");
                if (ImGui.menuItem("Texture2D")) Log.Display("not implemented yet");
                ImGui.endMenu();
            }

            String fpsText = "ms / fps : " + ((float)Window.GetPrimaryWindow().getDeltaTime() + " / " + (int)(1 / (float)Window.GetPrimaryWindow().getDeltaTime()));
            ImVec2 fpsTextSize = new ImVec2();
            ImGui.calcTextSize(fpsTextSize , fpsText);
            ImGui.dummy(ImGui.getContentRegionAvailX() - fpsTextSize.x - 10, 0.f);
            ImGui.text(fpsText);
        }
        ImGui.endMainMenuBar();
    }
}
