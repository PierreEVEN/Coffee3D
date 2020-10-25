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

public class TextureImporter extends AssetImporter {

    public TextureImporter(String windowName) {
        super(windowName);
        _extensionFilter = new String[] {"png"};
    }

    @Override
    protected void draw() {
        super.draw();
        if (!canImport()) return;

        ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
        ImGui.sameLine();
        if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
            Texture2D tex = new Texture2D(getTargetFileName(), getSelectedSource().getPath(), new File(getTargetFilePath()));
            tex.save();
            close();
        }
    }
}
