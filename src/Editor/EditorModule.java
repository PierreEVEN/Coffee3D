package Editor;

import Core.Assets.AssetManager;
import Core.Assets.Material;
import Core.Assets.StaticMesh;
import Core.Assets.Texture2D;
import Core.IEngineModule;
import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.Renderer.Window;
import Core.UI.DrawScenWindow;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class EditorModule implements IEngineModule {

    private Scene _rootScene;
    private Scene _scene2;

    @Override
    public void LoadResources() {
        new Texture2D("gridTexture", "resources/textures/defaultGrid.png");
        new Texture2D("grass", "resources/textures/grassSeamless.png");
        new Material("testMat", "resources/shaders/shader", new String[] {"gridTexture"});
        new Material("matWeird", "resources/shaders/shader", new String[] {"grass"});
        new StaticMesh("test", "resources/models/test.fbx", new String[] { "testMat" });
        new StaticMesh("cube", "resources/models/cube.fbx", new String[] { "matWeird" });
    }

    @Override
    public void PreInitialize() {
        _rootScene = new RenderScene(800, 600);
        _scene2 = new RenderScene(800, 600);

       new StaticMeshComponent(
                AssetManager.FindAsset("test"),
                new Vector3f().zero(),
                new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 0, 1), 10),
                new Vector3f(1, 1, 1)
        ).attachToScene(_scene2);


        SceneComponent root = new SceneComponent(new Vector3f(0,0,0), new Quaternionf().identity(), new Vector3f(1,1,1));
        root.attachToScene(_rootScene);
        Random rnd = new Random();
        for (int i = 0; i < 200; ++i) {
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

        foundMat = AssetManager.FindAsset("testMat");
        if (foundMat != null) {
            foundMat.use(_scene2);
            foundMat.getShader().setMatrixParameter("view", _scene2.getCamera().getViewMatrix());
            foundMat.getShader().setMatrixParameter("projection", _scene2.getProjection());
        }
        foundMat = AssetManager.FindAsset("matWeird");
        if (foundMat != null) {
            foundMat.use(_scene2);
            foundMat.getShader().setMatrixParameter("view", _scene2.getCamera().getViewMatrix());
            foundMat.getShader().setMatrixParameter("projection", _scene2.getProjection());
        }
        _scene2.renderScene();
    }

    ImBoolean bSHowViewport = new ImBoolean(false);
    ImBoolean bSHowViewport2 = new ImBoolean(false);

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
                if (ImGui.menuItem("quit")) {
                    Window.GetPrimaryWindow().close();
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Window")) {
                ImGui.checkbox("Viewport 1 ", bSHowViewport);
                ImGui.checkbox("Viewport 2 ", bSHowViewport2);
                ImGui.endMenu();
            }
        }
        ImGui.endMainMenuBar();

        if (bSHowViewport.get()) {
            if (ImGui.begin("Viewport 1", bSHowViewport)) {
                if (_rootScene instanceof RenderScene) {
                    RenderScene renderScene = (RenderScene) _rootScene;
                    DrawScenWindow.Draw(renderScene);
                }
            }
            ImGui.end();
        }

        if (bSHowViewport2.get()) {
            if (ImGui.begin("Viewport 2", bSHowViewport2)) {
                if (_rootScene instanceof RenderScene) {
                    RenderScene renderScene2 = (RenderScene) _scene2;
                    DrawScenWindow.Draw(renderScene2);
                }
            }
            ImGui.end();
        }
    }
}
