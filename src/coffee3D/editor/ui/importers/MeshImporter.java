package coffee3D.editor.ui.importers;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.assets.types.Material;
import coffee3D.core.assets.types.MaterialInterface;
import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.io.log.Log;
import coffee3D.editor.ui.propertyHelper.writers.AssetButton;
import coffee3D.editor.ui.propertyHelper.AssetPicker;
import imgui.ImGui;
import org.lwjgl.assimp.AIScene;

import java.io.File;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;

public class MeshImporter extends AssetImporter {

    private ArrayList<AssetReference<MaterialInterface>> _materials;

    private AIScene _previewData;

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
            AssetReference ref = new AssetReference<Material>(MaterialInterface.class);
            new AssetPicker("Choose material", ref, null);
            _materials.add(ref);
        }
        ImGui.indent();


        int materialIndex = 0;
        AssetReference<MaterialInterface> removedElem = null;
        for (AssetReference<MaterialInterface> material : _materials) {
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

        if (_previewData == null) return;
        else {
            ImGui.text("mesh infos : ");
            ImGui.text("section count : " + _previewData.mNumMeshes());
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
            StaticMesh mesh = new StaticMesh(getTargetFileName(), getSelectedSource(), new File(getTargetFilePath()), materialNames);
            mesh.save();
            close();
        }
    }

    @Override
    public void onSourceChanged() {
        super.onSourceChanged();

        try {
            _previewData = null;
            _previewData = aiImportFile(getSelectedSource().getPath(), aiProcess_Triangulate);
            _materials = new ArrayList<>();
            for (int i = 0; i < _previewData.mNumMeshes(); ++i) {
                _materials.add(new AssetReference<>(MaterialInterface.class));
            }
        }
        catch (Exception e){
            _previewData = null;
            Log.Warning("invalid source file : " + e.getMessage());
        }


    }
}
