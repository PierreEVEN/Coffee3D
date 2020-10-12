package Editor;

import Core.Assets.AssetManager;
import Core.Assets.Material;
import Core.Assets.StaticMesh;
import Core.Assets.Texture2D;
import Core.IEngineModule;
import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.SceneComponent;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class EditorModule implements IEngineModule {

    private RenderScene _rootScene;

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
    public void BuildLevel() {
        _rootScene = new RenderScene(800, 600);

        SceneComponent root = new SceneComponent(new Vector3f(0,0,0), new Quaternionf().identity(), new Vector3f(1,1,1));
        root.attachToScene(_rootScene);
        Random rnd = new Random();
        for (int i = 0; i < 100; ++i) {
            float range = 200;
            StaticMeshComponent parent = new StaticMeshComponent(
                    AssetManager.GetAsset("test"),
                    new Vector3f(rnd.nextFloat() * range - range / 2 , rnd.nextFloat() * range - range / 2, rnd.nextFloat() * range - range / 2),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 0, 1), 10),
                    new Vector3f(1, 1, 1)
            );
            parent.attachToComponent(root);

            new StaticMeshComponent(
                    AssetManager.GetAsset("cube"),
                    new Vector3f(0, 0, 2),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 0, 0), 25),
                    new Vector3f(1.5f, 0.8f, 1)
            ).attachToComponent(parent);

            StaticMeshComponent subChild = new StaticMeshComponent(
                    AssetManager.GetAsset("cube"),
                    new Vector3f(0, 4, 1),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 2, 0).normalize(), 8789),
                    new Vector3f(1.5f, 0.8f, 1.4f)
            );
            subChild.attachToComponent(parent);

            new StaticMeshComponent(
                    AssetManager.GetAsset("cube"),
                    new Vector3f(3, 2, 1),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 2, 0).normalize(), 489),
                    new Vector3f(1.1f, 0.8f, 0.02f)
            ).attachToComponent(subChild);
        }
    }

    @Override
    public void DrawScene() {
        _rootScene.renderScene();
    }

    @Override
    public void DrawUI() {
        int dockspaceID = 0;
        if (ImGui.begin("Master Window"/*, nullptr, ImGuiWindowFlags_MenuBar*/))
        {
            ImGui.textUnformatted("DockSpace below");

            // Declare Central dockspace
            dockspaceID = ImGui.getID("HUB_DockSpace");
            ImGui.dockSpace(dockspaceID, 0.f, 0.f, ImGuiDockNodeFlags.None | ImGuiDockNodeFlags.PassthruCentralNode/*|ImGuiDockNodeFlags_NoResize*/);
        }
        ImGui.end();

        ImGui.setNextWindowDockID(dockspaceID , ImGuiCond.FirstUseEver);
        if (ImGui.begin("Dockable Window"))
        {
            ImGui.image(_rootScene.getFramebuffer().getColorBuffer(), _rootScene.getFramebuffer().getWidth(), _rootScene.getFramebuffer().getHeight(), 0, 1, 1, 0);

            ImGui.textUnformatted("Test");
        }
        ImGui.end();

        ImGui.showDemoWindow();

    }
}
