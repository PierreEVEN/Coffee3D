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

public class MaterialImporter extends SubWindow {

    private File _selectedShader;
    private ImString _fileName;
    private ArrayList<AssetReference<Texture2D>> _textures;

    public MaterialImporter(String windowName) {
        super(windowName);
        _fileName = new ImString();
        _textures = new ArrayList<>();
    }

    @Override
    protected void draw() {
        ImGui.text("source file : ");
        ImGui.sameLine();
        if (ImGui.button(_selectedShader == null ? "pick file" : _selectedShader.getPath())) {
            new FileBrowser("Select texture file", new String[] {"vert", "frag"}, _selectedShader, (file) -> {
                _selectedShader = file;
                if (_fileName.get().equals("")) {
                    _fileName.set(_selectedShader.getName().replaceFirst("[.][^.]+$", ""));
                }
            });
        }

        boolean bIsValidName = AssetManager.CanCreateAssetWithName(_fileName.get());

        if (!bIsValidName) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.f, .2f, .2f, .5f);
            ImGui.pushStyleColor(ImGuiCol.Text, 1.f, .4f, .4f, .8f);
        }

        ImGui.text("asset name : ");
        ImGui.sameLine();
        ImGui.inputText("##file name", _fileName);

        if (!bIsValidName) {
            ImGui.sameLine();
            ImGui.text("invalid file name");
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }


        if (_selectedShader != null) {
            String fileCleanName = _selectedShader.getPath().replaceFirst("[.][^.]+$", "");
            File vertexFile = new File(fileCleanName + ".vert");
            File fragmentFile = new File(fileCleanName + ".frag");
            if (!vertexFile.exists()) {
                ImGui.pushStyleColor(ImGuiCol.Text, 1.f, .4f, .4f, .8f);
                ImGui.text("failed to find vertex shader file named " + fileCleanName + ".vert");
                ImGui.popStyleColor();
                return;
            }
            if (!fragmentFile.exists()) {
                ImGui.pushStyleColor(ImGuiCol.Text, 1.f, .4f, .4f, .8f);
                ImGui.text("failed to find fragment shader file named " + fileCleanName + ".frag");
                ImGui.popStyleColor();
                return;
            }
        }


        ImGui.separator();
        ImGui.text("textures");
        ImGui.sameLine();
        if (ImGui.button("add texture")) {
            AssetReference ref = new AssetReference<Texture2D>(Texture2D.class, null);
            new AssetPicker("Choose texture", ref);
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
        if (bIsValidName && _selectedShader != null) {
            if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
                String[] textureNames = new String[_textures.size()];
                for (int i = 0; i < _textures.size(); ++i) {
                    textureNames[i] = _textures.get(i) == null ? "" : _textures.get(i).get().getName();
                }
                new Material(_fileName.get(), _selectedShader.getPath(), textureNames);
                close();
            }
        }
    }
}
