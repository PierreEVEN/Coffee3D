package Editor.UI.LevelEditor.Tools;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import Core.UI.PropertyHelper.StructureReader;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;

public class LevelProperties extends SubWindow {

    private final Scene _editedScene;

    public LevelProperties(Scene editedScene, String windowName) {
        super(windowName);
        _editedScene = editedScene;
    }

    @Override
    protected void draw() {
        StructureReader.WriteObj(_editedScene, "scene");
    }
}
