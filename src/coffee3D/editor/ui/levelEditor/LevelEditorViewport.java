package coffee3D.editor.ui.levelEditor;

import coffee3D.core.IEngineModule;
import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.*;
import coffee3D.core.io.Clipboard;
import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.inputs.IInputListener;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.navigation.NavigableActor;
import coffee3D.core.navigation.NavmeshComponent;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Components.AudioComponent;
import coffee3D.core.renderer.scene.Components.BillboardComponent;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.renderer.Window;
import coffee3D.core.ui.hud.HudUtils;
import coffee3D.editor.components.GizmoComponent;
import coffee3D.editor.components.GizmoMovementDirection;
import coffee3D.editor.controller.EditorController;
import coffee3D.editor.renderer.EditorScene;
import coffee3D.editor.ui.levelEditor.tools.ComponentInspector;
import coffee3D.editor.ui.levelEditor.tools.LevelProperties;
import coffee3D.editor.ui.levelEditor.tools.SceneOutliner;
import coffee3D.editor.ui.SceneViewport;
import coffee3D.editor.ui.propertyHelper.AssetPicker;
import coffee3D.editor.ui.tools.StatWindow;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.lwjgl.opengl.GL11.*;


public class LevelEditorViewport extends SceneViewport {

    private ComponentInspector _inspector;

    public LevelEditorViewport(RenderScene scene, String windowName) {
        super(scene, windowName);
        bHasMenuBar = true;
        new LevelProperties(getScene(), "Level properties");
        new SceneOutliner(this, "Scene outliner");
    }

    @Override
    protected void draw() {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4, 15);
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("edit")) {
                if (ImGui.menuItem("save")) getScene().save();
                if (ImGui.menuItem("load")) new AssetPicker("select level", _sceneContext.getSource(), () -> {
                    _sceneContext.load(_sceneContext.getSource().get());
                });
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("add")) {
                if (ImGui.menuItem("Navmesh")) addNavmesh();
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("debug")) {
                if (ImGui.menuItem(getScene().isFrustumFrozen() ? "unfreeze frustum culling" : "freeze frustum culling"))
                    getScene().freezeFrustum(!getScene().isFrustumFrozen());
                if (ImGui.beginMenu("Draw mode")) {
                    if (ImGui.menuItem("Shape")) Window.GetPrimaryWindow().setDrawMode(GL_FILL);
                    if (ImGui.menuItem("Wireframe")) Window.GetPrimaryWindow().setDrawMode(GL_LINE);
                    if (ImGui.menuItem("Points")) Window.GetPrimaryWindow().setDrawMode(GL_POINT);
                    ImGui.endMenu();
                }
                ImGui.checkbox("SCENE show bounds", RenderUtils.DRAW_DEBUG_BOUNDS);
                ImGui.checkbox("UI draw debug boxes", HudUtils.bDrawDebugBoxes);
                if (ImGui.menuItem(_sceneContext.getSettings().enableShadows() ? "disable shadows" : "enable shadows"))
                    _sceneContext.getSettings().setShadows(!_sceneContext.getSettings().enableShadows());

                if (ImGui.menuItem(_sceneContext.getSettings().enablePostProcess() ? "disable post process" : "enable post process"))
                    _sceneContext.getSettings().set_enablePostProcess(!_sceneContext.getSettings().enablePostProcess());

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
        ImGui.popStyleVar();

        super.draw();
        if (ImGui.beginDragDropTarget())
        {
            byte[] data = ImGui.acceptDragDropPayload ("DDOP_ASSET");
            if (data != null)
            {
                String assetName = new String(data);
                Asset droppedAsset = AssetManager.FindAsset(assetName);

                if (droppedAsset instanceof StaticMesh) {

                    StaticMeshComponent sm = new StaticMeshComponent(
                            (StaticMesh)droppedAsset,
                            getDropPosition(droppedAsset),
                            new Quaternionf().identity(),
                            new Vector3f(1,1,1));
                    sm.attachToScene(getScene());
                    sm.setComponentName("sm_" + droppedAsset.getName());
                    editComponent(sm);
                }

                if (droppedAsset instanceof Texture2D) {
                    BillboardComponent bb = new BillboardComponent(
                            (Texture2D)droppedAsset,
                            getDropPosition(droppedAsset),
                            new Quaternionf().identity(),
                            new Vector3f(1,1,1));
                    bb.attachToScene(getScene());
                    bb.setComponentName("bb_" + droppedAsset.getName());
                    editComponent(bb);
                }

                if (droppedAsset instanceof SoundWave) {
                    AudioComponent bb = new AudioComponent(
                            (SoundWave) droppedAsset,
                            getDropPosition(droppedAsset),
                            new Quaternionf().identity(),
                            new Vector3f(1,1,1));
                    bb.attachToScene(getScene());
                    bb.setComponentName("aud_" + droppedAsset.getName());
                    editComponent(bb);
                }

                if (droppedAsset instanceof MaterialInterface) {
                    if (getScene().getHitResult().component instanceof StaticMeshComponent) {
                        ((StaticMeshComponent)getScene().getHitResult().component).setMaterial(((MaterialInterface)droppedAsset), 0);
                    }
                    editComponent(getScene().getHitResult().component);
                }
            }
        }
    }


    private Vector3f getDropPosition(Asset sourceAsset) {
        if (getScene().getHitResult().component != null) {
            return new Vector3f(getScene().getHitResult().position);
        }
        else {
            if (sourceAsset instanceof StaticMesh) {
                return new Vector3f(
                        getScene().getCamera().getForwardVector())
                        .mul(2 * ((StaticMesh) sourceAsset).getBound().radius)
                        .add(getScene().getCamera().getRelativePosition())
                        .sub(((StaticMesh) sourceAsset).getBound().position);
            }
            else {
                return new Vector3f(
                        getScene().getCamera().getForwardVector()).mul(2).add(getScene().getCamera().getRelativePosition());
            }
        }
    }

    private void addNavmesh() {
        NavmeshComponent navmesh = new NavmeshComponent(new Vector3f(0), new Quaternionf().identity(), new Vector3f(1));
        navmesh.attachToScene(getScene());
        navmesh.setComponentName("navmesh");
    }

    public void editComponent(SceneComponent comp) {
        ((EditorScene)getScene()).getGizmo().setComponent(comp);
        if (_inspector == null) _inspector = new ComponentInspector("component inspector");
        _inspector.setComponent(comp);
    }

    public SceneComponent getEditedComponent() { return _inspector == null ? null : _inspector.getComponent(); }

    public GizmoComponent getGizmo() {
        return ((EditorScene)getScene()).getGizmo();
    }

    @Override
    public void keyCallback(int keycode, int scancode, int action, int mods) {

        boolean wasEditingComponent = false;
        if (action == GLFW.GLFW_PRESS && keycode == GLFW.GLFW_KEY_ESCAPE) {
            if (getEditedComponent() != null) {
                editComponent(null);
                wasEditingComponent = true;
            }
        }

        if (!isMouseInsideWindow()) return;
        if (action == GLFW.GLFW_PRESS) {
            switch (keycode) {
                case GLFW.GLFW_KEY_ESCAPE : {
                    if (!wasEditingComponent) Window.GetPrimaryWindow().switchCursor();
                } break;
                case GLFW.GLFW_KEY_S : {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        _sceneContext.save();
                    }
                } break;
                case GLFW.GLFW_KEY_G : {
                    if (getGizmo().isInTranslation()) getGizmo().endTranslation();
                    else getGizmo().beginTranslation(true);
                } break;
                case GLFW.GLFW_KEY_X : getGizmo().setTranslationAxis(GizmoMovementDirection.moveX); break;
                case GLFW.GLFW_KEY_Y : getGizmo().setTranslationAxis(GizmoMovementDirection.moveY); break;
                case GLFW.GLFW_KEY_W : getGizmo().setTranslationAxis(GizmoMovementDirection.moveZ); break;
                case GLFW.GLFW_KEY_F : {
                    if (getEditedComponent() != null) {
                        _sceneContext.getCamera().setRelativePosition(new Vector3f(_sceneContext.getCamera().getForwardVector()).mul(getEditedComponent().getBound().radius * -3).add(getEditedComponent().getBound().position));
                    }
                }
                case GLFW.GLFW_KEY_C : {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        copySelected();
                    }
                } break;
                case GLFW.GLFW_KEY_V : {
                    if (mods == GLFW.GLFW_MOD_CONTROL) {
                        pastSelected();
                    }
                } break;
                case GLFW.GLFW_KEY_D : {
                    if (mods == GLFW.GLFW_MOD_ALT) {
                        copySelected();
                        SceneComponent lastParent = getEditedComponent().getParent();
                        if (pastSelected() != null) {
                            if (lastParent == null) getEditedComponent().attachToScene(_sceneContext);
                            else getEditedComponent().attachToComponent(lastParent);
                            getGizmo().beginTranslation(true);
                        }
                    }
                } break;
                case GLFW.GLFW_KEY_DELETE : deleteSelected(); break;
                case GLFW.GLFW_KEY_T:
                    NavigableActor act = new NavigableActor(getDropPosition(null), new Quaternionf().identity(), new Vector3f(1));
                    act.attachToScene(getScene());
                    break;
            }
        }
    }

    @Override
    public void charCallback(int chr) {}

    @Override
    public void mouseButtonCallback(int button, int action, int mods) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE && Window.GetPrimaryWindow().captureMouse()) Window.GetPrimaryWindow().showCursor(true);
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {
            if (getGizmo().isInTranslation()) getGizmo().endTranslation();
        }
        if (!isMouseInsideWindow()) return;
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS) {
            if (getGizmo().isInTranslation()) getGizmo().cancelTranslation();
            else Window.GetPrimaryWindow().showCursor(false);
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
            if (getGizmo().isInTranslation()) {
                getGizmo().endTranslation();
            }
            else {
                if (getEditedComponent() != null) {
                    getEditedComponent().setIsSelected(false);
                }
                if (getGizmo().displayTranslation()) getGizmo().beginTranslation(false);
                else if (!Window.GetPrimaryWindow().captureMouse()) editComponent(getScene().getHitResult().component);
            }
        }
    }

    @Override
    public void scrollCallback(double xOffset, double yOffset) {}

    public void copySelected() {
        if (getEditedComponent() != null) Clipboard.Write(LevelEditorViewport.SerializeHierarchy(getEditedComponent()));
    }

    public SceneComponent pastSelected() {
        SceneComponent newComp = DeserializeHierarchy(Clipboard.Get());
        if (newComp != null) {
            if (getEditedComponent() != null && getEditedComponent().getParent() != null) {
                newComp.attachToComponent(getEditedComponent().getParent());
            }
            else {
                newComp.attachToScene(_sceneContext);
            }
            editComponent(newComp);
            getGizmo().beginTranslation(true);
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

    public boolean isMouseInsideWindow() {
        if ((float) Window.GetPrimaryWindow().getCursorPosY() - 35 < getWindowPosY()) return false;
        return super.isMouseInsideWindow();
    }
}
