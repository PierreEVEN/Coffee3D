package Editor.UI.Importers;

import Core.Assets.*;
import Core.Assets.Types.MaterialInstance;
import Core.Assets.Types.MaterialInterface;
import Core.IO.Settings.EngineSettings;
import Core.UI.PropertyHelper.Writers.AssetButton;
import Core.UI.SubWindows.SubWindow;
import Editor.UI.Browsers.FolderPicker;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;

import java.io.File;

public class MaterialInstanceImporter extends SubWindow {

    private AssetReference<MaterialInterface> _parent;
    private ImString assetName;
    private File outputPath;

    public MaterialInstanceImporter(String windowName) {
        super(windowName);
        _parent = new AssetReference<>(MaterialInterface.class);
        assetName = new ImString("");
        outputPath = EngineSettings.ENGINE_ASSET_PATH;
    }

    public boolean canImport() {
        if (assetName == null) return false;
        return _parent != null && AssetManager.IsAssetNameFree(assetName.get()) && outputPath != null;
    }

    @Override
    protected void draw() {

        ImGui.text("parent material : ");
        ImGui.sameLine();
        AssetButton.Draw("parent material", _parent);
        if (assetName.get().equals("") && _parent.get() != null) {
            assetName = new ImString(_parent.getName() + "_inst");
        }


        ImGui.text("target path : ");
        ImGui.sameLine();
        if (ImGui.button(outputPath.getPath())) {
            new FolderPicker("Select folder", outputPath, assetName.get(), (file) -> {
                outputPath = file;
            });
        }

        boolean bIsNameValid = AssetManager.IsAssetNameFree(assetName.get());
        if (!bIsNameValid) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.f, .2f, .2f, .5f);
            ImGui.pushStyleColor(ImGuiCol.Text, 1.f, .4f, .4f, .8f);
        }

        ImGui.text("asset name : ");
        ImGui.sameLine();
        ImGui.inputText("##file name", assetName);

        if (!bIsNameValid) {
            ImGui.sameLine();
            ImGui.text("invalid file name");
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }

        if (!canImport()) return;

        ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
        ImGui.sameLine();
        if (canImport()) {
            if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
                MaterialInstance mat = new MaterialInstance(assetName.get(), _parent, new File(outputPath + "/" + assetName.get() + ".asset"));
                mat.save();
                close();
            }
        }
    }
}
