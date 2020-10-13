package Editor.UI.LevelEditor.Tools;

import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.UI.SubWindows.SubWindow;
import Editor.UI.LevelEditor.LevelEditorViewport;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;

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

        int flags = ImGuiTreeNodeFlags.OpenOnDoubleClick;
        if (comp.getChildren() == null || comp.getChildren().size() == 0) flags |= ImGuiTreeNodeFlags.Leaf;
        if (comp == _parentViewport.getEditedComponent()) flags |= ImGuiTreeNodeFlags.Selected;

        boolean bExpand = ImGui.treeNodeEx(componentName + "##" + nodeIndex, flags);
        if (ImGui.isItemClicked()) _parentViewport.editComponent(comp);
        if (bExpand) {
            //ImGui.sameLine();
            if (comp.getChildren() != null) {
                for (SceneComponent child : comp.getChildren()) {
                    drawNode(child);
                }
            }
            ImGui.treePop();
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
