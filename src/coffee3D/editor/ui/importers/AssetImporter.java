package coffee3D.editor.ui.importers;

import coffee3D.core.assets.AssetManager;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.browsers.FileBrowser;
import coffee3D.editor.ui.browsers.FolderPicker;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;

import java.io.File;

public abstract class AssetImporter extends SubWindow {

    private File _selectedSource;
    private ImString _targetFileName;
    private File _selectedTarget;
    public String[] _extensionFilter = null;

    public AssetImporter(String windowName) {
        super(windowName);
        _targetFileName = new ImString();
        _selectedTarget = EngineSettings.ENGINE_ASSET_PATH;
    }

    public File getSelectedSource() { return _selectedSource; }
    public File getSelectedTarget() { return _selectedTarget; }
    public String getTargetFileName() { return _targetFileName.get(); }
    public String getTargetFilePath() { return _selectedTarget.getPath() + "/" + _targetFileName.get() + ".asset"; }

    public boolean canImport() {
        if (_selectedSource == null) return false;
        if (_targetFileName == null) return false;
        if (_selectedTarget == null) return false;
        if (!isNameValid()) return false;
        return true;
    }

    public final boolean isNameValid() {
        return AssetManager.IsAssetNameFree(_targetFileName.get());
    }

    public void onSourceChanged() {}

    @Override
    protected void draw() {
        ImGui.text("source file : ");
        ImGui.sameLine();
        if (ImGui.button(_selectedSource == null ? "pick file" : _selectedSource.getPath())) {
            new FileBrowser("Select asset file", _extensionFilter, _selectedSource, (file) -> {
                _selectedSource = file;
                if (_targetFileName.get().equals("")) {
                    _targetFileName.set(_selectedSource.getName().replaceFirst("[.][^.]+$", ""));
                }
                onSourceChanged();
            });
        }

        ImGui.text("target path : ");
        ImGui.sameLine();
        if (ImGui.button(_selectedTarget.getPath())) {
            new FolderPicker("Select folder", _selectedTarget, _targetFileName.get(), (file) -> {
                _selectedTarget = file;
            });
        }

        boolean bIsNameValid = isNameValid();
        if (!bIsNameValid) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.f, .2f, .2f, .5f);
            ImGui.pushStyleColor(ImGuiCol.Text, 1.f, .4f, .4f, .8f);
        }

        ImGui.text("asset name : ");
        ImGui.sameLine();
        ImGui.inputText("##file name", _targetFileName);

        if (!bIsNameValid) {
            ImGui.sameLine();
            ImGui.text("invalid file name");
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }
    }
}
