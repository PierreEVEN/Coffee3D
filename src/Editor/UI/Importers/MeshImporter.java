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

public class MeshImporter extends AssetImporter {

    private ArrayList<AssetReference<Material>> _materials;

    public MeshImporter(String windowName) {
        super(windowName);
        _materials = new ArrayList<>();
        _extensionFilter = new String[] {"fbx", "obj"};
    }

    @Override
    protected void draw() {
        super.draw();
        if (!canImport()) return;


        ImGui.separator();
        ImGui.text("materials");
        ImGui.sameLine();
        if (ImGui.button("add material")) {
            AssetReference ref = new AssetReference<Material>(Material.class);
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
        if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
            String[] materialNames = new String[_materials.size()];
            for (int i = 0; i < _materials.size(); ++i) {
                materialNames[i] = _materials.get(i) == null ? "" : _materials.get(i).get().getName();
            }
            StaticMesh mesh = new StaticMesh(getTargetFileName(), getSelectedSource().getPath(), getTargetFilePath(), materialNames);
            mesh.save();
            close();
        }
    }
}
