package coffee3D.editor.ui.levelEditor.tools;

import coffee3D.core.io.Clipboard;
import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.inputs.IInputListener;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.util.ArrayList;

public class SceneOutliner extends SubWindow implements IInputListener {
    private final Scene _parentScene;
    private final LevelEditorViewport _parentViewport;
    private int _currentNodeIndex = 0;
    private final ArrayList<SceneComponent> _drawedComponents = new ArrayList<>();

    public SceneOutliner(LevelEditorViewport parentViewport, String windowName) {
        super(windowName);
        _parentScene = parentViewport.getScene();
        _parentViewport = parentViewport;
        GlfwInputHandler.AddListener(this);
    }

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
            Log.Warning("failed to copy data : " + e.getMessage());
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
        _currentNodeIndex++;

        String componentName = comp.getComponentName();

        _drawedComponents.add(comp);

        int flags = ImGuiTreeNodeFlags.OpenOnDoubleClick;
        if (comp.getChildren() == null || comp.getChildren().size() == 0) flags |= ImGuiTreeNodeFlags.Leaf;
        if (comp == _parentViewport.getEditedComponent()) flags |= ImGuiTreeNodeFlags.Selected;

        boolean bExpand = ImGui.treeNodeEx(componentName + "##" + _currentNodeIndex, flags);

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
        _currentNodeIndex = 0;
        _drawedComponents.clear();
        for (SceneComponent comp : _parentScene.getComponents())
        {
            drawNode(comp);
        }
    }

    @Override
    public void keyCallback(int keycode, int scancode, int action, int mods) {
        if (!isMouseInsideWindow()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (keycode) {
                case GLFW.GLFW_KEY_ESCAPE -> _parentViewport.editComponent(null);
                case GLFW.GLFW_KEY_UP -> {
                    int lastComp = _drawedComponents.indexOf(_parentViewport.getEditedComponent());
                    if (lastComp > 0) _parentViewport.editComponent(_drawedComponents.get(lastComp - 1));
                }
                case GLFW.GLFW_KEY_DOWN -> {
                    int lastComp = _drawedComponents.indexOf(_parentViewport.getEditedComponent());
                    if (lastComp + 1 < _drawedComponents.size()) _parentViewport.editComponent(_drawedComponents.get(lastComp + 1));
                }
                case GLFW.GLFW_KEY_C -> {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        if (_parentViewport.getEditedComponent() != null) Clipboard.Write(SerializeHierarchy(_parentViewport.getEditedComponent()));
                    }
                }
                case GLFW.GLFW_KEY_V -> {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        SceneComponent newComp = DeserializeHierarchy(Clipboard.Get());
                        if (newComp != null) {
                            if (_parentViewport.getEditedComponent() != null) {
                                newComp.attachToComponent(_parentViewport.getEditedComponent());
                            }
                            else {
                                newComp.attachToScene(_parentScene);
                            }
                        }
                    }
                }
                case GLFW.GLFW_KEY_DELETE -> {
                    if (_parentViewport.getEditedComponent() != null) {
                        _parentViewport.getEditedComponent().detach();
                        _parentViewport.editComponent(null);
                    }
                }
            }
        }
    }

    @Override
    public void charCallback(int chr) {

    }

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {

    }

    @Override
    public void scrollCallback(double xOffset, double yOffset) {

    }

    @Override
    public void cursorPosCallback(double x, double y) {

    }
}
