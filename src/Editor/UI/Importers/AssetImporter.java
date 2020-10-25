package Editor.UI.Importers;

import Core.Assets.AssetManager;
import Core.IO.Settings.EngineSettings;
import Core.UI.SubWindows.SubWindow;
import Editor.UI.Browsers.FileBrowser;
import Editor.UI.Browsers.FolderPicker;
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
        _selectedTarget = new File(EngineSettings.DEFAULT_ASSET_PATH);
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
        return AssetManager.CanCreateAssetWithName(_targetFileName.get());
    }

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
