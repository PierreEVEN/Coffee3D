package coffee3D.editor.ui.levelEditor;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.inputs.IInputListener;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.renderer.Window;
import coffee3D.editor.ui.levelEditor.tools.ComponentInspector;
import coffee3D.editor.ui.levelEditor.tools.LevelProperties;
import coffee3D.editor.ui.levelEditor.tools.SceneOutliner;
import coffee3D.editor.ui.SceneViewport;
import coffee3D.editor.ui.tools.StatWindow;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class LevelEditorViewport extends SceneViewport implements IInputListener {

    private ComponentInspector _inspector;

    public LevelEditorViewport(RenderScene scene, String windowName) {
        super(scene, windowName);
        bHasMenuBar = true;
        new LevelProperties(getScene(), "Level properties");
        new SceneOutliner(this, "Scene outliner");
        GlfwInputHandler.AddListener(this);
    }

    @Override
    protected void draw() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("edit")) {
                if (ImGui.menuItem("save")) getScene().saveToFile("truc.map");
                if (ImGui.menuItem("load")) getScene().loadFromFile("truc.map");
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("window")) {

                if (ImGui.menuItem("stats")) new StatWindow(_sceneContext, "statistics");
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }

        super.draw();
        if (ImGui.beginDragDropTarget())
        {
            byte[] data = ImGui.acceptDragDropPayload ("DDOP_ASSET");
            if (data != null)
            {
                String assetName = new String(data);
                Asset droppedAsset = AssetManager.FindAsset(assetName);
                if (droppedAsset != null && droppedAsset instanceof StaticMesh) {
                    StaticMeshComponent sm = new StaticMeshComponent(
                            (StaticMesh)droppedAsset,
                            new Vector3f(getScene().getCamera().getRelativePosition()),
                            new Quaternionf().identity(),
                            new Vector3f(1,1,1));
                    sm.attachToScene(getScene());
                    sm.setComponentName("sm_" + droppedAsset.getName());

                }
            }
        }
    }

    public void editComponent(SceneComponent comp) {
        if (_inspector == null) _inspector = new ComponentInspector("component inspector");
        _inspector.setComponent(comp);
    }

    public SceneComponent getEditedComponent() { return _inspector == null ? null : _inspector.getComponent(); }

    @Override
    public void keyCallback(int keycode, int scancode, int action, int mods) {
        if (!isMouseInsideWindow()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (keycode) {
                case GLFW.GLFW_KEY_ESCAPE -> Window.GetPrimaryWindow().switchCursor();
                case GLFW.GLFW_KEY_S -> {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        _sceneContext.saveToFile("engineContent/truc.map");
                    }
                }
            }
        }
    }

    @Override
    public void charCallback(int chr) {}

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE && Window.GetPrimaryWindow().captureMouse()) Window.GetPrimaryWindow().showCursor(true);
        if (!isMouseInsideWindow()) return;
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS) Window.GetPrimaryWindow().showCursor(false);
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
            if (getEditedComponent() != null) {
                getEditedComponent().setOutlined(false);
            }
            editComponent(getScene().getLastHitComponent());
        }
    }

    @Override
    public void scrollCallback(double xOffset, double yOffset) {}

    @Override
    public void cursorPosCallback(double x, double y) {}
}
