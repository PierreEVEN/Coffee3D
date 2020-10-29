package coffee3D.editor.ui.importers;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.assets.types.Material;
import coffee3D.core.assets.types.Texture2D;
import coffee3D.editor.ui.propertyHelper.writers.AssetButton;
import coffee3D.editor.ui.propertyHelper.AssetPicker;
import imgui.ImGui;

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

                AssetReference<Texture2D>[] textures = new AssetReference[textureNames.length];

                for (int i = 0; i < textureNames.length; ++i) {
                    textures[i] = new AssetReference<>(Texture2D.class, textureNames[i]);
                }


                Material mat = new Material(getTargetFileName(), getSelectedSource(), new File(getTargetFilePath()), textures);
                mat.save();
                close();
            }
        }
    }
}
