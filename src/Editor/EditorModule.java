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
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class EditorModule implements IEngineModule {

    private Scene _rootScene;

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
    }

    @Override
    public void DrawUI() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(Window.GetPrimaryWindow().getPixelWidth(), Window.GetPrimaryWindow().getPixelHeight());
        if (ImGui.begin("Master Window",  ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoInputs | ImGuiWindowFlags.NoBringToFrontOnFocus))
        {
            ImGui.dockSpace(ImGui.getID("Master dockSpace"), 0.f, 0.f, ImGuiDockNodeFlags.PassthruCentralNode);
        }
        ImGui.end();

        if (ImGui.begin("Viewport"))
        {
            if (_rootScene instanceof RenderScene) {
                RenderScene renderScene = (RenderScene)_rootScene;
                ImGui.image(renderScene.getFramebuffer().getColorBuffer(), renderScene.getFramebuffer().getWidth(), renderScene.getFramebuffer().getHeight(), 0, 1, 1, 0);
            }
        }
        ImGui.end();
    }
}
