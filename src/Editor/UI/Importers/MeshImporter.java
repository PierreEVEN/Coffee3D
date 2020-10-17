package Editor.UI.Importers;

import Core.Assets.*;
import Core.UI.PropertyHelper.Writers.AssetButton;
import Core.UI.SubWindows.SubWindow;
import Core.UI.Tools.AssetPicker;
import Editor.UI.Browsers.FileBrowser;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;

import java.io.File;
import java.util.ArrayList;

public class MeshImporter extends SubWindow {

    private File _selectedObject;
    private ImString _fileName;
    private ArrayList<AssetReference<Material>> _materials;

    public MeshImporter(String windowName) {
        super(windowName);
        _fileName = new ImString();
        _materials = new ArrayList<>();
    }

    @Override
    protected void draw() {
        ImGui.text("source file : ");
        ImGui.sameLine();
        if (ImGui.button(_selectedObject == null ? "pick file" : _selectedObject.getPath())) {
            new FileBrowser("Select object file", new String[] {"obj", "fbx"}, _selectedObject, (file) -> {
                _selectedObject = file;
                if (_fileName.get().equals("")) {
                    _fileName.set(_selectedObject.getName().replaceFirst("[.][^.]+$", ""));
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

        ImGui.separator();
        ImGui.text("materials");
        ImGui.sameLine();
        if (ImGui.button("add material")) {
            AssetReference ref = new AssetReference<Material>(Material.class, null);
            new AssetPicker("Choose material", ref);
            _materials.add(ref);
        }
        ImGui.indent();


        int materialIndex = 0;
        AssetReference<Material> removedElem = null;
        for (AssetReference<Material> material : _materials) {
            materialIndex++;
            if (ImGui.button(" - ##" + materialIndex)) {
                removedElem = material;
            }
            ImGui.sameLine();
            AssetButton.Draw("index " + (materialIndex - 1), material);
        }

        if (removedElem != null) {
            _materials.remove(removedElem);
        }

        ImGui.unindent();
        ImGui.separator();

        ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
        ImGui.sameLine();
        if (bIsValidName && _selectedObject != null) {
            if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
                String[] materialNames = new String[_materials.size()];
                for (int i = 0; i < _materials.size(); ++i) {
                    materialNames[i] = _materials.get(i) == null ? "" : _materials.get(i).get().getName();
                }
                new StaticMesh(_fileName.get(), _selectedObject.getPath(), materialNames);
                close();
            }
        }
    }
}
