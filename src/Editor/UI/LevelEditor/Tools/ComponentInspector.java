package Editor.UI.LevelEditor.Tools;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.SceneComponent;
import Core.UI.SubWindows.SubWindow;
import Core.UI.PropertyHelper.StructureReader;
import imgui.ImGui;

import java.lang.reflect.Field;

public class ComponentInspector extends SubWindow {
    SceneComponent _editedComponent;

    public ComponentInspector(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        drawComponentProperties();
    }

    public void setComponent(SceneComponent inComponent) {
        _editedComponent = inComponent;
    }

    public SceneComponent getComponent() { return _editedComponent; }

    private void drawComponentProperties() {
        if (_editedComponent == null) return;
        StructureReader.WriteObj(_editedComponent, "SceneComponent");
    }

    private void drawClassProperties(Class inClass) {

    }
}
