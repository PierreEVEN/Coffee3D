package Editor.UI.LevelEditor;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.RenderScene;
import Editor.UI.LevelEditor.Tools.SceneOutliner;
import Editor.UI.SceneViewport;
import imgui.ImGui;

public class LevelEditorViewport extends SceneViewport {

    public LevelEditorViewport(RenderScene scene, String windowName) {
        super(scene, windowName);
        bHasMenuBar = true;
    }

    @Override
    protected void draw() {

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("edit")) {
                if (ImGui.menuItem("save")) Log.Warning("Not implemented yet");
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("window")) {
                if (ImGui.menuItem("scene outliner")) new SceneOutliner(getScene(), "Scene outliner");
                ImGui.endMenu();
            }


            ImGui.endMenuBar();
        }

        super.draw();
    }
}
