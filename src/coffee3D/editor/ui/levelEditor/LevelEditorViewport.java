package coffee3D.editor.ui.levelEditor;

import coffee3D.core.IEngineModule;
import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.io.Clipboard;
import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.inputs.IInputListener;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.renderer.Window;
import coffee3D.editor.controller.EditorController;
import coffee3D.editor.ui.levelEditor.tools.ComponentInspector;
import coffee3D.editor.ui.levelEditor.tools.LevelProperties;
import coffee3D.editor.ui.levelEditor.tools.SceneOutliner;
import coffee3D.editor.ui.SceneViewport;
import coffee3D.editor.ui.propertyHelper.AssetPicker;
import coffee3D.editor.ui.tools.StatWindow;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

enum MovementDirection {
    None,
    NoAxis,
    X,
    Y,
    Z
}


public class LevelEditorViewport extends SceneViewport implements IInputListener {

    private ComponentInspector _inspector;
    private MovementDirection _currentMovementDirection;
    private Vector3f _initialPosition;

    public LevelEditorViewport(RenderScene scene, String windowName) {
        super(scene, windowName);
        bHasMenuBar = true;
        new LevelProperties(getScene(), "Level properties");
        new SceneOutliner(this, "Scene outliner");
        GlfwInputHandler.AddListener(this);
        _currentMovementDirection = MovementDirection.None;
    }

    @Override
    protected void draw() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("edit")) {
                if (ImGui.menuItem("save")) getScene().save();
                if (ImGui.menuItem("load")) new AssetPicker("select level", _sceneContext.getSource(), () -> {
                    _sceneContext.load(_sceneContext.getSource().get());
                });
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("debug")) {
                if (ImGui.button(getScene().isFrustumFrozen() ? "unfreeze frustum culling" : "freeze frustum culling")) getScene().freezeFrustum(!getScene().isFrustumFrozen());
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("window")) {

                if (ImGui.menuItem("stats")) new StatWindow(_sceneContext, "statistics");
                ImGui.endMenu();
            }

            ImGui.sameLine();
            ImGui.dummy(ImGui.getContentRegionAvailX() / 2 - 100, 0);
            ImGui.sameLine();
            ImGui.text("current level : " + (_sceneContext.getSource().get() == null ? "none" : _sceneContext.getSource().get().getName()));

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
                            new Vector3f(getScene().getCamera().getForwardVector()).mul(2 * ((StaticMesh) droppedAsset).getBound().radius).add(getScene().getCamera().getRelativePosition()),
                            new Quaternionf().identity(),
                            new Vector3f(1,1,1));
                    sm.attachToScene(getScene());
                    sm.setComponentName("sm_" + droppedAsset.getName());
                }
            }
        }
    }

    public void editComponent(SceneComponent comp) {
        cancelMovement();
        if (_inspector == null) _inspector = new ComponentInspector("component inspector");
        _inspector.setComponent(comp);
    }

    public SceneComponent getEditedComponent() { return _inspector == null ? null : _inspector.getComponent(); }



    @Override
    public void keyCallback(int keycode, int scancode, int action, int mods) {
        if (!isMouseInsideWindow()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (keycode) {
                case GLFW.GLFW_KEY_ESCAPE -> {
                    if (getEditedComponent() != null) editComponent(null);
                    else Window.GetPrimaryWindow().switchCursor();
                }
                case GLFW.GLFW_KEY_S -> {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        _sceneContext.save();
                    }
                }
                case GLFW.GLFW_KEY_G -> {
                    if (_currentMovementDirection == MovementDirection.NoAxis) {
                        _currentMovementDirection = null;
                    }
                    else {
                        _currentMovementDirection = MovementDirection.NoAxis;
                    }
                }
                case GLFW.GLFW_KEY_X -> beginMovement(MovementDirection.X);
                case GLFW.GLFW_KEY_Y -> beginMovement(MovementDirection.Y);
                case GLFW.GLFW_KEY_W -> beginMovement(MovementDirection.Z);
                case GLFW.GLFW_KEY_F -> {
                    if (getEditedComponent() != null) _sceneContext.getCamera().setRelativePosition(new Vector3f(_sceneContext.getCamera().getForwardVector()).mul(getEditedComponent().getBound().radius * -3).add(getEditedComponent().getRelativePosition()));
                }
                case GLFW.GLFW_KEY_C -> {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        copySelected();
                    }
                }
                case GLFW.GLFW_KEY_V -> {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        pastSelected();
                    }
                }
                case GLFW.GLFW_KEY_D -> {
                    if (mods == GLFW.GLFW_MOD_ALT) {
                        copySelected();
                        SceneComponent lastParent = getEditedComponent().getParent();
                        if (pastSelected() != null) {
                            if (lastParent == null) getEditedComponent().attachToScene(_sceneContext);
                            else getEditedComponent().attachToComponent(lastParent);
                            _currentMovementDirection = MovementDirection.NoAxis;
                        }
                    }
                }
                case GLFW.GLFW_KEY_DELETE -> deleteSelected();
            }
        }
    }

    @Override
    public void charCallback(int chr) {}

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE && Window.GetPrimaryWindow().captureMouse()) Window.GetPrimaryWindow().showCursor(true);
        if (!isMouseInsideWindow()) return;
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS) {
            if (_currentMovementDirection != MovementDirection.None) cancelMovement();
            else Window.GetPrimaryWindow().showCursor(false);
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
            if (_currentMovementDirection != MovementDirection.None) {
                endMovement();
            }
            else {
                if (getEditedComponent() != null) {
                    getEditedComponent().setOutlined(false);
                }
                editComponent(getScene().getLastHitComponent());
            }
        }
    }

    @Override
    public void scrollCallback(double xOffset, double yOffset) {}

    @Override
    public void cursorPosCallback(double x, double y) {
        if (getEditedComponent() != null) {
            float delta = (float) IEngineModule.Get().GetController().getCursorDeltaX();
            if (GLFW.glfwGetKey(Window.GetPrimaryWindow().getGlfwWindowHandle(), GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS) delta /= 10;
            switch (_currentMovementDirection) {
                case X -> getEditedComponent().getRelativePosition().x += delta;
                case Y -> getEditedComponent().getRelativePosition().y += delta;
                case Z -> getEditedComponent().getRelativePosition().z += delta;
            }
        }
    }

    private void beginMovement(MovementDirection direction) {
        if (_currentMovementDirection == MovementDirection.None) return;
        cancelMovement();
        if (getEditedComponent() == null) return;
        _initialPosition = new Vector3f(getEditedComponent().getRelativePosition());
        _currentMovementDirection = direction;
        Window.GetPrimaryWindow().showCursor(false);
        ((EditorController)IEngineModule.Get().GetController()).enableCameraMovements(false);
    }

    private void endMovement() {
        if (getEditedComponent() == null) return;
        getEditedComponent().getRelativePosition();
        _initialPosition = null;
        _currentMovementDirection = MovementDirection.None;
        Window.GetPrimaryWindow().showCursor(true);
        ((EditorController)IEngineModule.Get().GetController()).enableCameraMovements(true);
    }

    private void cancelMovement() {
        if (getEditedComponent() == null || _initialPosition == null) return;
        getEditedComponent().setRelativePosition(_initialPosition);
        _initialPosition = null;
        _currentMovementDirection = MovementDirection.None;
        Window.GetPrimaryWindow().showCursor(true);
        ((EditorController)IEngineModule.Get().GetController()).enableCameraMovements(true);
    }

    public void copySelected() {
        if (getEditedComponent() != null) Clipboard.Write(LevelEditorViewport.SerializeHierarchy(getEditedComponent()));
    }

    public SceneComponent pastSelected() {
        SceneComponent newComp = DeserializeHierarchy(Clipboard.Get());
        if (newComp != null) {
            if (getEditedComponent() != null) {
                newComp.attachToComponent(getEditedComponent());
            }
            else {
                newComp.attachToScene(_sceneContext);
            }
            editComponent(newComp);
            return newComp;
        }
        return null;
    }

    public void deleteSelected() {
        if (getEditedComponent() != null) {
            getEditedComponent().detach();
            editComponent(null);
        }
    }

    public static byte[] SerializeHierarchy(SceneComponent component) {
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

    public static SceneComponent DeserializeHierarchy(byte[] data) {
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

}
