package Editor.UI.LevelEditor.Tools;

import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;

public class SceneOutliner extends SubWindow {
    Scene _parentScene;


    public SceneOutliner(Scene parentScene, String windowName) {
        super(windowName);
        _parentScene = parentScene;
    }

    int nodeIndex = 0;

    private void drawNode(SceneComponent comp) {
        nodeIndex ++;

        String componentName = comp.getClass().getSimpleName();

        if (comp instanceof StaticMeshComponent) {
            componentName = "sm_" + ((StaticMeshComponent)comp).getStaticMesh().getName();
        }

        if (ImGui.treeNode(componentName + "##" + nodeIndex)) {
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
