package Editor.UI.Importers;

import Core.Assets.AssetManager;
import Core.Assets.AssetReference;
import Core.Assets.Material;
import Core.Assets.Texture2D;
import Core.IO.LogOutput.Log;
import Core.Resources.MaterialResource;
import Core.UI.PropertyHelper.Writers.AssetButton;
import Core.UI.SubWindows.SubWindow;
import Core.UI.Tools.AssetPicker;
import Editor.UI.Browsers.FileBrowser;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;

import java.io.File;
import java.util.ArrayList;

public class MaterialImporter extends AssetImporter {

    private ArrayList<AssetReference<Texture2D>> _textures;

    public MaterialImporter(String windowName) {
        super(windowName);
        _textures = new ArrayList<>();
        _extensionFilter = new String[] {"vert"};
    }

    @Override
    public boolean canImport() {
        if (!super.canImport()) return false;
        String fileCleanName = getSelectedSource().getPath().replaceFirst("[.][^.]+$", "");
        File vertexFile = new File(fileCleanName + ".vert");
        File fragmentFile = new File(fileCleanName + ".frag");
        if (!vertexFile.exists()) {
            return false;
        }
        if (!fragmentFile.exists()) {
            return false;
        }
        return true;
    }

    @Override
    protected void draw() {
        super.draw();

        if (!super.canImport()) return;


        ImGui.separator();
        ImGui.text("textures");
        ImGui.sameLine();
        if (ImGui.button("add texture")) {
            AssetReference ref = new AssetReference<Texture2D>(Texture2D.class);
            new AssetPicker("Choose texture", ref, null);
            _textures.add(ref);
        }
        ImGui.indent();


        int textureIndex = 0;
        AssetReference<Texture2D> removedElem = null;
        for (AssetReference<Texture2D> texture : _textures) {
            textureIndex++;
            if (ImGui.button(" - ##" + textureIndex)) {
                removedElem = texture;
            }
            ImGui.sameLine();
            AssetButton.Draw("index " + (textureIndex - 1), texture);
        }

        if (removedElem != null) {
            _textures.remove(removedElem);
        }

        ImGui.unindent();
        ImGui.separator();

        ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
        ImGui.sameLine();
        if (canImport()) {
            if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
                String[] textureNames = new String[_textures.size()];
                for (int i = 0; i < _textures.size(); ++i) {
                    textureNames[i] = _textures.get(i) == null ? "" : _textures.get(i).get().getName();
                }
                Material mat = new Material(getTargetFileName(), getSelectedSource().getPath(), getTargetFilePath(), textureNames);
                mat.save();
                close();
            }
        }
    }
}
