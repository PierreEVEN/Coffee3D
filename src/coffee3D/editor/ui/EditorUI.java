package coffee3D.editor.ui;

import coffee3D.core.audio.AudioListener;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.Window;
import coffee3D.core.ui.hud.HudUtils;
import coffee3D.editor.EditorModule;
import coffee3D.editor.ui.browsers.ContentBrowser;
import coffee3D.editor.ui.browsers.EngineSettingsViewer;
import coffee3D.editor.ui.browsers.ResourcesViewer;
import coffee3D.editor.ui.importers.*;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import coffee3D.editor.ui.tools.StatWindow;
import coffee3D.editor.ui.tools.StyleEditor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.opengl.GL11.*;

public class EditorUI {

    public static void DrawMenuBar(RenderScene context) {

        ImGui.setNextWindowPos(-4, 35);
        ImGui.setNextWindowSize(Window.GetPrimaryWindow().getPixelWidth() + 8, Window.GetPrimaryWindow().getPixelHeight() - 33);
        if (ImGui.begin("Master Window", ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoInputs | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoBackground)) {
            ImGui.dockSpace(ImGui.getID("Master dockSpace"), 0.f, 0.f, ImGuiDockNodeFlags.PassthruCentralNode);
        }
        ImGui.end();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4, 15);
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Files")) {
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
                if (ImGui.menuItem("Stat window")) new StatWindow(context, "Stat window");
                ImGui.separator();
                if (ImGui.menuItem("Style editor")) new StyleEditor("Style editor");
                if (ImGui.menuItem("Engine settings")) new EngineSettingsViewer("Engine settings");

                ImGui.endMenu();
            }

            String fpsText = "ms / fps : " + (int)(Window.GetPrimaryWindow().getDeltaTime()*1000) + " / " + (int)(1 / (float)Window.GetPrimaryWindow().getDeltaTime());
            ImVec2 fpsTextSize = new ImVec2();
            ImGui.calcTextSize(fpsTextSize , fpsText);
            ImGui.dummy(ImGui.getContentRegionAvailX() - fpsTextSize.x - 10, 0.f);
            ImGui.text(fpsText);
        }
        ImGui.endMainMenuBar();
        ImGui.popStyleVar();
    }
}
