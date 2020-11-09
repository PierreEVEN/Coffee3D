package coffee3D.editor.ui.levelEditor.tools;

import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.propertyHelper.StructureReader;

public class ComponentInspector extends SubWindow {
    SceneComponent _editedComponent;

    public ComponentInspector(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        drawComponentProperties();
        if (_editedComponent != null) {
            _editedComponent.setStencilValue(1);
        }
    }

    @Override
    public void close() {
        super.close();
        if (_editedComponent != null) {
            _editedComponent.setStencilValue(0);
        }
    }

    public void setComponent(SceneComponent inComponent) {
        if (_editedComponent != null) {
            _editedComponent.setStencilValue(0);
        }
        _editedComponent = inComponent;
    }

    public SceneComponent getComponent() { return _editedComponent; }

    private void drawComponentProperties() {
        if (_editedComponent == null) return;
        StructureReader.WriteObj(_editedComponent, "SceneComponent");
    }
}
