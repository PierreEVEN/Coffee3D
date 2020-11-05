package coffee3D.editor.ui.tools;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.MaterialInterface;
import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.renderer.scene.*;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import coffee3D.core.types.Color;
import coffee3D.core.types.TypeHelper;
import coffee3D.editor.ui.propertyHelper.StructureReader;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.browsers.FileBrowser;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class AssetWindow extends SubWindow {

    private final Asset _editedAsset;
    private final ImString newAssetName;
    private RenderScene _thumbnailScene;
    private StaticMeshComponent _background;

    public AssetWindow(Asset asset, String windowName) {
        super(windowName);
        _editedAsset = asset;
        newAssetName = new ImString(_editedAsset.getName());
        if (newAssetName.getBufferSize() < 100) newAssetName.resize(100);

    }

    @Override
    protected void draw() {

        if (ImGui.button("save##" + _editedAsset.getName() + (_editedAsset.isDirty() ? "*" : ""))) {
            _editedAsset.save();
        }
        ImGui.sameLine();
        if (ImGui.button("reload resource")) {
            _editedAsset.reload();
        }
        ImGui.text("source file : ");
        ImGui.sameLine();
        if (_editedAsset.getSourcePath() != null) {
            if (ImGui.button(_editedAsset.getSourcePath().exists() ? _editedAsset.getSourcePath().getPath() : "none")) {
                new FileBrowser("find asset", _editedAsset.getAssetExtensions(), _editedAsset.getSavePath(), file -> {
                    _editedAsset.setSourcePath(file);
                });
            }
        }
        ImGui.inputText("asset name", newAssetName);
        if (!newAssetName.get().equals(_editedAsset.getName())) {
            if (!AssetManager.IsAssetNameFree(newAssetName.get())) {
                ImGui.pushStyleColor(ImGuiCol.Text, ImColor.floatToColor(1, .5f, .5f));
                ImGui.sameLine();
                ImGui.text("invalid name");
                ImGui.popStyleColor();
            }
            else {
                _editedAsset.updateName(newAssetName.get());
            }
        }



        _editedAsset.drawDetailedContent();
        StructureReader.WriteObj(_editedAsset, _editedAsset.getName());

        if (_editedAsset instanceof StaticMesh || _editedAsset instanceof MaterialInterface) {

            if (_thumbnailScene == null)
            {
                _thumbnailScene = new RenderScene(false);
                ((RenderSceneProperties)_thumbnailScene.getProperties())._backgroundColor = new Color(0,0,0,0);
                _background = new StaticMeshComponent(
                        null,
                        new Vector3f(0,0,0),
                        new Quaternionf().identity(),
                        new Vector3f(1,1,1));
                _background.attachToScene(_thumbnailScene);
            }
            int sizeX = Math.min((int) ImGui.getContentRegionAvailX(), (int) ImGui.getContentRegionAvailY());

            if (_editedAsset instanceof StaticMesh) {
                _background.setStaticMesh((StaticMesh) _editedAsset);
            }
            else {
                _background.setStaticMesh(AssetManager.FindAsset("default_sphere"));
                _background.setMaterial((MaterialInterface)_editedAsset, 0);
            }

            _thumbnailScene.getColorFrameBuffer().resizeFramebuffer(sizeX, sizeX);

            _thumbnailScene.getCamera().setYawInput((float) (GLFW.glfwGetTime() * 20));
            _thumbnailScene.getCamera().setPitchInput(30);
            if (_background != null) _thumbnailScene.getCamera().setRelativePosition(
                    TypeHelper.getVector3(_thumbnailScene.getCamera().getForwardVector())
                            .mul(_background.getBound().radius * -3)
                            .add(_background.getBound().position));

            _thumbnailScene.renderScene();
            ImGui.image(_thumbnailScene.getColorFrameBuffer().getColorTexture(), sizeX, sizeX, 0, 1, 1, 0);
        }

    }
}
