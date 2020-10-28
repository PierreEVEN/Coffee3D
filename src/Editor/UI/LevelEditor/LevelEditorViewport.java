package Editor.UI.LevelEditor;

import Core.Assets.Asset;
import Core.Assets.AssetManager;
import Core.Assets.StaticMesh;
import Core.IO.Inputs.GlfwInputHandler;
import Core.IO.Inputs.IInputListener;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.SceneComponent;
import Core.Renderer.Window;
import Editor.UI.LevelEditor.Tools.ComponentInspector;
import Editor.UI.LevelEditor.Tools.LevelProperties;
import Editor.UI.LevelEditor.Tools.SceneOutliner;
import Editor.UI.SceneViewport;
import Editor.UI.Tools.StatWindow;
import imgui.ImGui;
import org.joml.Quaterniond;
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
                        _sceneContext.saveToFile("truc.map");
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
