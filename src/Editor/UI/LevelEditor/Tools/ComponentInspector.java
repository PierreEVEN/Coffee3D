package Editor.UI.LevelEditor.Tools;

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
        Class componentClass = _editedComponent.getClass();
        drawClassProperties(componentClass);
    }

    private void drawClassProperties(Class inClass) {
        if (inClass.getSuperclass() != null && inClass.getSuperclass() != Object.class) {
            drawClassProperties(inClass.getSuperclass());
        }

        ImGui.separator();
        ImGui.text(inClass.getSimpleName());
        ImGui.separator();
        ImGui.indent();
        for (Field field : inClass.getDeclaredFields()) {
            StructureReader.debugIndex = 0;
            StructureReader.WriteField(field, _editedComponent);
        }
        ImGui.unindent();
        ImGui.dummy(0,15.f);;
    }
}
