package Editor.UI.LevelEditor.Tools;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.SceneComponent;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

    private void drawComponentProperties() {
        if (_editedComponent == null) return;
        Class componentClass = _editedComponent.getClass();
        drawClassProperties(componentClass);
    }

    private void drawClassProperties(Class inClass) {
        ImGui.text(inClass.getSimpleName());
        ImGui.separator();

        Class parent = inClass.getSuperclass();
        if (parent != null && parent != Object.class) {
            drawClassProperties(parent);
        }

        for (Field field : inClass.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers())) continue;


            ImGui.text(field.getName() + " : " + field.getType().getSimpleName());

            if (field.getType().getName().equals("float")) {
                try {
                    float value = field.getFloat(_editedComponent);
                    ImGui.sameLine();
                    ImGui.text("value = " + value);
                }
                catch (Exception e) {
                    Log.Warning("failed to read property value : " + e.getMessage());
                }
            }
        }
    }
}
