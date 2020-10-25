package Editor.UI.LevelEditor.Tools;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.UI.SubWindows.SubWindow;
import Editor.UI.LevelEditor.LevelEditorViewport;
import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiTreeNodeFlags;

import java.io.*;
import java.util.ArrayList;

public class SceneOutliner extends SubWindow {
    Scene _parentScene;
    LevelEditorViewport _parentViewport;


    public SceneOutliner(LevelEditorViewport parentViewport, String windowName) {
        super(windowName);
        _parentScene = parentViewport.getScene();
        _parentViewport = parentViewport;
    }

    int nodeIndex = 0;


    private static byte[] SerializeHierarchy(SceneComponent component) {
        try {
            ByteArrayOutputStream fos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(component);
            oos.flush();
            byte[] data = fos.toByteArray();
            oos.close();
            fos.close();
            return data;
        }
        catch (Exception e) {
            Log.Warning(e.getMessage());
        }
        return null;
    }

    private static SceneComponent DeserializeHierarchy(byte[] data) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            SceneComponent output = (SceneComponent) ois.readObject();
            ois.close();
            bis.close();
            return output;
        }
        catch (Exception e) {
            Log.Warning(e.getMessage());
        }
        return null;
    }

    private void drawNode(SceneComponent comp) {
        nodeIndex ++;

        String componentName = comp.getComponentName();

        int flags = ImGuiTreeNodeFlags.OpenOnDoubleClick;
        if (comp.getChildren() == null || comp.getChildren().size() == 0) flags |= ImGuiTreeNodeFlags.Leaf;
        if (comp == _parentViewport.getEditedComponent()) flags |= ImGuiTreeNodeFlags.Selected;

        boolean bExpand = ImGui.treeNodeEx(componentName + "##" + nodeIndex, flags);

        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.None)) {
            byte[] data = SerializeHierarchy(comp);
            if (data != null) {
                ImGui.setDragDropPayload("DDOP_SCENE_HIERARCHY", data);
                ImGui.text("drag component");
            }
            ImGui.endDragDropSource();
        }
        if (ImGui.beginDragDropTarget())
        {
            byte[] data = ImGui.acceptDragDropPayload ("DDOP_SCENE_HIERARCHY");
            if (data != null)
            {
                SceneComponent deserializedComponent = DeserializeHierarchy(data);
                if (deserializedComponent != null) {
                    deserializedComponent.attachToComponent(comp);
                }
            }
        }

        if (ImGui.isItemClicked()) _parentViewport.editComponent(comp);
        if (bExpand) {
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
