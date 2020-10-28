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
        if (_editedComponent != null) {
            _editedComponent.setOutlined(true);
        }
    }

    @Override
    public void close() {
        super.close();
        if (_editedComponent != null) {
            _editedComponent.setOutlined(false);
        }
    }

    public void setComponent(SceneComponent inComponent) {
        if (_editedComponent != null) {
            _editedComponent.setOutlined(false);
        }
        _editedComponent = inComponent;
    }

    public SceneComponent getComponent() { return _editedComponent; }

    private void drawComponentProperties() {
        if (_editedComponent == null) return;
        StructureReader.WriteObj(_editedComponent, "SceneComponent");
    }
}
