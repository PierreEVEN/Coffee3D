package Editor;

import Core.Assets.*;
import Core.IEngineModule;
import Core.IO.LogOutput.Log;
import Core.Renderer.RenderUtils;
import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.Renderer.Window;
import Core.UI.HUD.*;
import Core.UI.ImGuiImpl.ImGuiImplementation;
import Core.UI.SubWindows.DemoWindow;
import Editor.UI.Browsers.ContentBrowser;
import Editor.UI.Importers.MaterialImporter;
import Editor.UI.Importers.MeshImporter;
import Editor.UI.Importers.TextureImporter;
import Editor.UI.LevelEditor.LevelEditorViewport;
import Editor.UI.Browsers.ResourcesViewer;
import Editor.UI.Tools.AssetWindow;
import Editor.UI.Tools.StyleEditor;
import imgui.*;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class EditorModule implements IEngineModule {

    private Scene _rootScene;

    @Override
    public void LoadResources() {

        Window.GetPrimaryWindow().setBackgroundColor(new Vector4f(0,0,0,0));

        ImGuiImplementation.Get().addFont("resources/fonts/roboto/Roboto-Medium.ttf", 60);
        ImGuiIO io = ImGui.getIO();
        io.setFontGlobalScale(0.4f);

        new Texture2D("plaster", "resources/textures/plaster.png");
        new Texture2D("grass", "resources/textures/grassSeamless.png");
        new Texture2D("mud", "resources/textures/mud.png");
        new Texture2D("grid", "resources/textures/defaultGrid.png");
        new Material("default", "resources/shaders/default", new String[] {"grid"});
        new Material("Concrete1", "resources/shaders/default", new String[] {"grassSeamless"});
        new Material("Concrete2", "resources/shaders/default", new String[] {"mud"});
        new Material("concrete", "resources/shaders/default", new String[] {"plaster"});
        new Material("glass", "resources/shaders/default", new String[] {"grassSeamless"});
        new Material("pillars", "resources/shaders/default", new String[] {"plaster"});
        new StaticMesh("building", "resources/models/Building.fbx", new String[] { "Concrete2", "concrete", "glass", "pillars" });
        new StaticMesh("cube", "resources/models/cube.fbx", new String[] { "default" });
        new StaticMesh("test", "resources/models/test.fbx", new String[] { "default" });
        RenderUtils.CheckGLErrors();

        Asset.SetAssetEditWidget(new IEditAsset() {
            @Override
            public void applyAsset(Asset asset) {
                new AssetWindow(asset, asset.getName());
            }
        });
    }

    @Override
    public void PreInitialize() {
        _rootScene = new RenderScene(800, 600);

        new LevelEditorViewport((RenderScene) _rootScene, "viewport");
        new ContentBrowser("Content browser");

        new StaticMeshComponent(
                AssetManager.FindAsset("building"),
                new Vector3f(0, 0, 0),
                new Quaternionf().identity().rotationXYZ((float)Math.toRadians(-90), 0, 0),
                new Vector3f(1, 1, 1)
        ).attachToScene(_rootScene);

        SceneComponent parent = new StaticMeshComponent(
                AssetManager.FindAsset("cube"),
                new Vector3f(0, -13, 0),
                new Quaternionf().identity().rotateXYZ(0, 0, 0),
                new Vector3f(100, 0.5f, 100)
        );
        parent.attachToScene(_rootScene);

        String vendor = glGetString(GL_VENDOR);
        String renderer = glGetString(GL_RENDERER);
        GLFW.glfwSetWindowTitle(Window.GetPrimaryWindow().getGlfwWindowHandle(), "Coffee3D Editor - " + vendor + " " + renderer);

    }

    @Override
    public void DrawScene() {
        _rootScene.renderScene();
    }

    @Override
    public void DrawUI() {

        ImGui.setNextWindowPos(0, 25);
        ImGui.setNextWindowSize(Window.GetPrimaryWindow().getPixelWidth(), Window.GetPrimaryWindow().getPixelHeight() - 25);
        if (ImGui.begin("Master Window", ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoInputs | ImGuiWindowFlags.NoBringToFrontOnFocus)) {
            ImGui.dockSpace(ImGui.getID("Master dockSpace"), 0.f, 0.f, ImGuiDockNodeFlags.PassthruCentralNode);
        }
        ImGui.end();

        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Files")) {
                if (ImGui.menuItem("save all")) { Log.Display("No implementer yet");}
                if (ImGui.menuItem("quit")) {
                    Window.GetPrimaryWindow().close();
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Window")) {
                if (ImGui.menuItem("Viewport")) new LevelEditorViewport((RenderScene) _rootScene, "Scene viewport");
                if (ImGui.menuItem("Content browser")) new ContentBrowser("Content browser");
                ImGui.separator();
                if (ImGui.menuItem("Resource viewer")) new ResourcesViewer("resource viewer");
                ImGui.separator();
                if (ImGui.menuItem("Style editor")) new StyleEditor("Style editor");
                if (ImGui.menuItem("Demo window")) new DemoWindow("Demo window");
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("View")) {
                if (ImGui.beginMenu("Draw mode")) {
                    if (ImGui.menuItem("Shape")) Window.GetPrimaryWindow().setDrawMode(GL_FILL);
                    if (ImGui.menuItem("Wireframe")) Window.GetPrimaryWindow().setDrawMode(GL_LINE);
                    if (ImGui.menuItem("Points")) Window.GetPrimaryWindow().setDrawMode(GL_POINT);
                    ImGui.endMenu();
                }
                ImGui.checkbox("draw debug boxes", HudUtils.bDrawDebugBoxes);
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Import")) {
                if (ImGui.menuItem("Static mesh")) new MeshImporter("Mesh importer");
                if (ImGui.menuItem("Material")) new MaterialImporter("Material importer");
                if (ImGui.menuItem("Texture2D")) new TextureImporter("Texture importer");
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

    @Override
    public void DrawHUD() {
/*
        int gridTexture = AssetManager.<Texture2D>FindAsset("grid").getTextureID();
        int grassTexture = AssetManager.<Texture2D>FindAsset("grass").getTextureID();

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
