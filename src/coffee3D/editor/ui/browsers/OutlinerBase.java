package coffee3D.editor.ui.browsers;

import coffee3D.core.animation.SkeletalMesh;
import coffee3D.core.renderer.scene.IScene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class OutlinerBase extends SubWindow {

    private final IScene _scene;
    private final ImString _componentFilter = new ImString("", 256);
    private int _currentNodeIndex = 0;

    public OutlinerBase(IScene scene, String windowName) {
        super(windowName);
        _scene = scene;
    }

    public <T extends IScene> T getScene() { return (T)_scene; }

    public abstract boolean isComponentSelected(SceneComponent component);

    @Override
    protected void draw() {
        _currentNodeIndex = 0;

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 3);
        ImGui.text("search");
        ImGui.sameLine();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailWidth());
        ImGui.inputText("##searchBox", _componentFilter);
        ImGui.popStyleVar();
        ImGui.separator();
        if (ImGui.beginChild("SceneOutlinerList")) {
            for (SceneComponent comp : _scene.getComponents()) {
                drawNode(comp);
            }
        }

        ImGui.endChild();
    }


    private void drawNode(SceneComponent comp) {
        _currentNodeIndex++;

        String componentName = comp.getComponentName();

        if (!_componentFilter.get().equals("") && !componentName.contains(_componentFilter.get())) return;

        int flags = ImGuiTreeNodeFlags.OpenOnDoubleClick;
        if (comp.getChildren() == null || comp.getChildren().size() == 0) flags |= ImGuiTreeNodeFlags.Leaf;
        if (isComponentSelected(comp)) flags |= ImGuiTreeNodeFlags.Selected;

        ImGui.image(comp.getComponentIcon().getTextureHandle(), 16, 16, 0 ,1 ,1 ,0);
        ImGui.sameLine();
        boolean bExpand = ImGui.treeNodeEx(componentName + "##" + _currentNodeIndex, flags);

        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.None)) {
            byte[] data = LevelEditorViewport.SerializeHierarchy(comp);
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
                SceneComponent deserializedComponent = LevelEditorViewport.DeserializeHierarchy(data);
                if (deserializedComponent != null) {
                    deserializedComponent.attachToComponent(comp);
                }
            }
        }

        if (ImGui.isItemClicked(GLFW.GLFW_MOUSE_BUTTON_1)) onComponentSelected(comp);
        if (ImGui.isItemClicked(GLFW.GLFW_MOUSE_BUTTON_2)) ImGui.openPopup("COMPONENT_OUTLINER_POPUP");
        if (ImGui.beginPopup("COMPONENT_OUTLINER_POPUP")) {
            drawComponentPopup(comp);
            ImGui.endPopup();
        }


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
    public void keyCallback(int keycode, int scancode, int action, int mods) {
        if (!isMouseInsideWindow()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (keycode) {
                case GLFW.GLFW_KEY_ESCAPE : onComponentSelected(null); break;
            }
        }
    }

    @Override
    public void charCallback(int chr) {}
    @Override
    public void mouseButtonCallback(int button, int action, int mods) {}
    @Override
    public void scrollCallback(double xOffset, double yOffset) {}
    @Override
    public void cursorPosCallback(double x, double y) {}

    public abstract void drawComponentPopup(SceneComponent component);
    public abstract void onComponentSelected(SceneComponent component);
}
