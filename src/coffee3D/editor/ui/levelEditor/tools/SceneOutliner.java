package coffee3D.editor.ui.levelEditor.tools;

import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.inputs.IInputListener;
import coffee3D.core.renderer.scene.Components.ComponentManager;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.levelEditor.LevelEditorViewport;
import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class SceneOutliner extends SubWindow {
    private final Scene _parentScene;
    private final LevelEditorViewport _parentViewport;
    private int _currentNodeIndex = 0;
    private final ArrayList<SceneComponent> _drawedComponents = new ArrayList<>();
    private final ImString searchString = new ImString("", 256);

    public SceneOutliner(LevelEditorViewport parentViewport, String windowName) {
        super(windowName);
        _parentScene = parentViewport.getScene();
        _parentViewport = parentViewport;
    }

    private void drawNode(SceneComponent comp) {
        _currentNodeIndex++;

        String componentName = comp.getComponentName();

        if (!searchString.get().equals("") && !componentName.contains(searchString.get())) return;

        _drawedComponents.add(comp);

        int flags = ImGuiTreeNodeFlags.OpenOnDoubleClick;
        if (comp.getChildren() == null || comp.getChildren().size() == 0) flags |= ImGuiTreeNodeFlags.Leaf;
        if (comp == _parentViewport.getEditedComponent()) flags |= ImGuiTreeNodeFlags.Selected;

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

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 3);
        ImGui.text("search");
        ImGui.sameLine();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailWidth() - 150);
        ImGui.inputText("##searchBox", searchString);
        ImGui.sameLine();
        if (ImGui.button("add component", ImGui.getContentRegionAvailX(), 25)) {
            ImGui.openPopup("POPUP_ADD_COMPONENT");
        }
        if (ImGui.beginPopup("POPUP_ADD_COMPONENT")) {
            drawAddComponentPopup();
            ImGui.endPopup();
        }
        ImGui.popStyleVar();
        ImGui.separator();
        if (ImGui.beginChild("SceneOutlinerList")) {
            for (SceneComponent comp : _parentScene.getComponents()) {
                drawNode(comp);
            }
        }
        ImGui.endChild();
    }

    private void drawAddComponentPopup() {
        for (Class<?> cl : ComponentManager.GetComponents()) {
            if (ImGui.menuItem(cl.getSimpleName())) {
                ComponentManager.CreateComponent(cl, (RenderScene) _parentScene,
                        new Vector3f(
                                ((RenderScene)_parentScene).getCamera().getForwardVector())
                                .mul(2)
                                .add(((RenderScene)_parentScene).getCamera().getWorldPosition()));
            }
        }
    }

    @Override
    public void keyCallback(int keycode, int scancode, int action, int mods) {
        if (!isMouseInsideWindow()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (keycode) {
                case GLFW.GLFW_KEY_ESCAPE : _parentViewport.editComponent(null); break;
                case GLFW.GLFW_KEY_UP : {
                    int lastComp = _drawedComponents.indexOf(_parentViewport.getEditedComponent());
                    if (lastComp > 0) _parentViewport.editComponent(_drawedComponents.get(lastComp - 1));
                } break;
                case GLFW.GLFW_KEY_DOWN : {
                    int lastComp = _drawedComponents.indexOf(_parentViewport.getEditedComponent());
                    if (lastComp + 1 < _drawedComponents.size()) _parentViewport.editComponent(_drawedComponents.get(lastComp + 1));
                } break;
                case GLFW.GLFW_KEY_C : {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        _parentViewport.copySelected();
                    }
                } break;
                case GLFW.GLFW_KEY_V : {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        _parentViewport.pastSelected((mods & GLFW.GLFW_MOD_SHIFT) != 0);
                    }
                } break;
                case GLFW.GLFW_KEY_DELETE : {
                    _parentViewport.deleteSelected();
                } break;
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
