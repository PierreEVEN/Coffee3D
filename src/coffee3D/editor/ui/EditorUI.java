package coffee3D.editor.ui;

import coffee3D.core.IEngineModule;
import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.Window;
import coffee3D.core.ui.hud.HudUtils;
import coffee3D.editor.EditorModule;
import coffee3D.editor.ui.browsers.ContentBrowser;
import coffee3D.editor.ui.browsers.ResourcesViewer;
import coffee3D.editor.ui.importers.MaterialImporter;
import coffee3D.editor.ui.importers.MaterialInstanceImporter;
import coffee3D.editor.ui.importers.MeshImporter;
import coffee3D.editor.ui.importers.TextureImporter;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import coffee3D.editor.ui.tools.StyleEditor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.opengl.GL11.*;

public class EditorUI {

    public static void DrawMenuBar(RenderScene context) {

        ImGui.setNextWindowPos(0, 30);
        ImGui.setNextWindowSize(Window.GetPrimaryWindow().getPixelWidth(), Window.GetPrimaryWindow().getPixelHeight() - 30);
        if (ImGui.begin("Master Window", ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoInputs | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoBackground)) {
            ImGui.dockSpace(ImGui.getID("Master dockSpace"), 0.f, 0.f, ImGuiDockNodeFlags.PassthruCentralNode);
        }
        ImGui.end();

        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Files")) {
                if (ImGui.menuItem("save all")) EditorModule.SaveAll();
                if (ImGui.menuItem("quit")) {
                    Window.GetPrimaryWindow().close();
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Window")) {
                if (ImGui.menuItem("Viewport")) new LevelEditorViewport((RenderScene) context, "Scene viewport");
                if (ImGui.menuItem("Content browser")) new ContentBrowser("Content browser");
                ImGui.separator();
                if (ImGui.menuItem("Resource viewer")) new ResourcesViewer("resource viewer");
                ImGui.separator();
                if (ImGui.menuItem("Style editor")) new StyleEditor("Style editor");
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("View")) {
                if (ImGui.beginMenu("Draw mode")) {
                    if (ImGui.menuItem("Shape")) Window.GetPrimaryWindow().setDrawMode(GL_FILL);
                    if (ImGui.menuItem("Wireframe")) Window.GetPrimaryWindow().setDrawMode(GL_LINE);
                    if (ImGui.menuItem("Points")) Window.GetPrimaryWindow().setDrawMode(GL_POINT);
                    ImGui.endMenu();
                }
                ImGui.checkbox("SCENE show bounds", EngineSettings.DRAW_DEBUG_BOUNDS);
                ImGui.checkbox("UI draw debug boxes", HudUtils.bDrawDebugBoxes);
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Import")) {
                if (ImGui.menuItem("Static mesh")) new MeshImporter("Mesh importer");
                if (ImGui.menuItem("Material")) new MaterialImporter("Material importer");
                if (ImGui.menuItem("Material instance")) new MaterialInstanceImporter("create material instance");
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


}
