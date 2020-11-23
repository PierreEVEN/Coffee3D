package coffee3D.editor.ui.importers;

import coffee3D.core.animation.SkeletalMesh;
import coffee3D.core.assets.types.SoundWave;
import imgui.ImGui;

import java.io.File;

public class AnimationImporter extends AssetImporter {
    public AnimationImporter(String windowName) {
        super(windowName);
        bHasSource = false;
    }

    @Override
    protected void draw() {
        super.draw();
        if (canImport()) {
            if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
                SkeletalMesh skel = new SkeletalMesh(getTargetFileName(), new File(getTargetFilePath()));
                skel.save();
                close();
            }
        }
    }
}
