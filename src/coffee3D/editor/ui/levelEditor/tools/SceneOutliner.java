package coffee3D.editor.ui.levelEditor.tools;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Components.ComponentManager;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.editor.ui.browsers.OutlinerBase;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class SceneOutliner extends OutlinerBase {
    private final LevelEditorViewport _parentViewport;

    public SceneOutliner(LevelEditorViewport parentViewport, String windowName) {
        super(parentViewport.getScene(), windowName);
        _parentViewport = parentViewport;
    }

    private void drawAddComponentPopup() {
        for (Class<?> cl : ComponentManager.GetComponents()) {
            if (ImGui.menuItem(cl.getSimpleName())) {
                ComponentManager.CreateComponent(cl, getScene(),
                        new Vector3f(
                                this.<RenderScene>getScene().getCamera().getForwardVector())
                                .mul(2)
                                .add(this.<RenderScene>getScene().getCamera().getWorldPosition()));
            }
        }
    }

    @Override
    protected void draw() {
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 3);
        if (ImGui.button("add component",ImGui.getContentRegionAvailX(), 25)) {
            ImGui.openPopup("POPUP_ADD_COMPONENT");
        }
        if (ImGui.beginPopup("POPUP_ADD_COMPONENT")) {
            drawAddComponentPopup();
            ImGui.endPopup();
        }
        ImGui.popStyleVar();
        super.draw();
    }

    @Override
    public boolean isComponentSelected(SceneComponent component) {
        return _parentViewport.getEditedComponent() == component;
    }

    @Override
    public void drawComponentPopup(SceneComponent component) {
    }

    @Override
    public void onComponentSelected(SceneComponent component) {
        _parentViewport.editComponent(component);
    }

}
