package Editor.UI.LevelEditor;

import Core.Assets.Asset;
import Core.Assets.AssetManager;
import Core.Assets.StaticMesh;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.SceneComponent;
import Editor.UI.LevelEditor.Tools.ComponentInspector;
import Editor.UI.LevelEditor.Tools.LevelProperties;
import Editor.UI.LevelEditor.Tools.SceneOutliner;
import Editor.UI.SceneViewport;
import Editor.UI.Tools.StatWindow;
import imgui.ImGui;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LevelEditorViewport extends SceneViewport {

    private ComponentInspector _inspector;

    public LevelEditorViewport(RenderScene scene, String windowName) {
        super(scene, windowName);
        bHasMenuBar = true;
        new SceneOutliner(this, "Scene outliner");
        new LevelProperties(getScene(), "Level properties");
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
                if (ImGui.menuItem("scene outliner")) new SceneOutliner(this, "Scene outliner");
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
}
