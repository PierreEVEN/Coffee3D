package Editor.UI.Importers;

import Core.Assets.AssetManager;
import Core.Assets.Texture2D;
import Core.UI.SubWindows.SubWindow;
import Editor.UI.Browsers.FileBrowser;
import Editor.UI.Browsers.IFileValidated;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.lwjgl.egl.IMGContextPriority;

import java.io.File;

public class TextureImporter extends SubWindow {

    private File _selectedFile;

    private ImString _fileName;
    public TextureImporter(String windowName) {
        super(windowName);
        _fileName = new ImString();
    }

    @Override
    protected void draw() {
        if (ImGui.button(_selectedFile == null ? "pick file" : _selectedFile.getPath())) {
            new FileBrowser("Select texture file", new String[] {"png"}, _selectedFile, (file) -> _selectedFile = file);
        }

        boolean bAlreadyExist = false;
        if (AssetManager.FindAsset(_fileName.get()) != null || _fileName.get().equals("")) {
            bAlreadyExist = true;
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.f, .2f, .2f, .5f);
            ImGui.pushStyleColor(ImGuiCol.Text, 1.f, .4f, .4f, .8f);
        }

        ImGui.text("file name : ");
        ImGui.sameLine();
        ImGui.inputText("##file name", _fileName);

        if (bAlreadyExist) {
            ImGui.sameLine();
            ImGui.text("invalid file name");
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }

        ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
        ImGui.sameLine();
        if (!bAlreadyExist && _selectedFile != null) {
            if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
                new Texture2D(_fileName.get(), _selectedFile.getPath());
                close();
            }
        }
    }
}
