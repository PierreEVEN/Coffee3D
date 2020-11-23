package coffee3D.editor.ui.animationEditor;

import coffee3D.core.animation.SkeletalMesh;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import coffee3D.core.renderer.scene.IScene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.editor.ui.browsers.OutlinerBase;
import coffee3D.editor.ui.levelEditor.tools.ComponentInspector;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;

public class AnimationOutliner extends OutlinerBase {

    private ComponentInspector _inspector;
    private String lastParent;

    public AnimationOutliner(SkeletalMesh scene, String windowName) {
        super(scene, windowName);
    }

    @Override
    protected void draw() {
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 3);
        if (ImGui.button("new root", ImGui.getContentRegionAvailX(), 25)) {
            this.<SkeletalMesh>getScene().addBone("root", null);
        }
        ImGui.popStyleVar();

        super.draw();

    }

    @Override
    public boolean isComponentSelected(SceneComponent component) {
        return (_inspector != null && _inspector.getComponent() == component);
    }

    @Override
    public void drawComponentPopup(SceneComponent component) {
        if (component == null) return;

        if (ImGui.menuItem("add child bone")) {
            this.<SkeletalMesh>getScene().addBone("child", component.getComponentName());
        }
    }

    @Override
    public void onComponentSelected(SceneComponent component) {
        if (_inspector == null) _inspector = new ComponentInspector("bone inspector");
        _inspector.setComponent(component);
    }

}
