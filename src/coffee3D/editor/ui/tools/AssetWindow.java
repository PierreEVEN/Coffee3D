package coffee3D.editor.ui.tools;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.io.log.Log;
import coffee3D.editor.ui.propertyHelper.StructureReader;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.browsers.FileBrowser;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;

public class AssetWindow extends SubWindow {

    private final Asset _editedAsset;
    private final ImString newAssetName;

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
    }
}
