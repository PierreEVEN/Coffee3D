package Editor.UI.LevelEditor.Tools;

import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.UI.SubWindows.SubWindow;
import Editor.UI.LevelEditor.LevelEditorViewport;
import imgui.ImGui;

public class SceneOutliner extends SubWindow {
    Scene _parentScene;
    LevelEditorViewport _parentViewport;


    public SceneOutliner(LevelEditorViewport parentViewport, String windowName) {
        super(windowName);
        _parentScene = parentViewport.getScene();
        _parentViewport = parentViewport;
    }

    int nodeIndex = 0;

    private void drawNode(SceneComponent comp) {
        nodeIndex ++;

        String componentName = comp.getClass().getSimpleName();

        if (comp instanceof StaticMeshComponent) {
            componentName = "sm_" + ((StaticMeshComponent)comp).getStaticMesh().getName();
        }

        if (ImGui.treeNode(componentName + "##" + nodeIndex)) {
            ImGui.sameLine();
            if (ImGui.button("edit##nodeIndex", ImGui.getContentRegionAvailX(), 0)) _parentViewport.editComponent(comp);
            if (comp.getChildren() != null) {
                for (SceneComponent child : comp.getChildren()) {
                    drawNode(child);
                }
            }
            ImGui.treePop();
        }
        else {
            ImGui.sameLine();
            if (ImGui.button("edit##nodeIndex", ImGui.getContentRegionAvailX(), 0)) _parentViewport.editComponent(comp);
        }
    }

    @Override
    protected void draw() {
        nodeIndex = 0;
        for (SceneComponent comp : _parentScene.getComponents())
        {
            drawNode(comp);
        }
    }
}
