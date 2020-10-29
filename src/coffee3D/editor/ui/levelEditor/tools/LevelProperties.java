package coffee3D.editor.ui.levelEditor.tools;

import coffee3D.core.renderer.scene.Scene;
import coffee3D.editor.ui.propertyHelper.StructureReader;
import coffee3D.core.ui.subWindows.SubWindow;

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
