package Editor.UI.LevelEditor;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.SceneComponent;
import Editor.UI.LevelEditor.Tools.ComponentInspector;
import Editor.UI.LevelEditor.Tools.SceneOutliner;
import Editor.UI.SceneViewport;
import imgui.ImGui;

public class LevelEditorViewport extends SceneViewport {

    private ComponentInspector _inspector;

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
                if (ImGui.menuItem("scene outliner")) new SceneOutliner(this, "Scene outliner");
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }

        super.draw();
    }

    public void editComponent(SceneComponent comp) {
        if (_inspector == null) _inspector = new ComponentInspector("component inspector");
        _inspector.setComponent(comp);
    }

    public SceneComponent getEditedComponent() { return _inspector == null ? null : _inspector.getComponent(); }
}
