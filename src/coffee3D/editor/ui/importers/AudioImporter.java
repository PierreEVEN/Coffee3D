package coffee3D.editor.ui.importers;

import coffee3D.core.assets.types.SoundWave;
import coffee3D.core.io.log.Log;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.resources.factories.AudioFactory;
import coffee3D.core.resources.types.WaveResource;
import imgui.ImGui;

import java.io.File;

public class AudioImporter extends AssetImporter {

    private WaveResource _preview;
    private boolean _failed;

    public AudioImporter(String windowName) {
        super(windowName);
        _extensionFilter = new String[] {"wav"};
        _failed = false;
    }

    @Override
    protected void draw() {
        super.draw();
        if (!canImport()) return;


        if (_failed && _preview == null) {
            ImGui.text("invalid asset");
            return;
        }
        if (!_failed && _preview != null) {
            ImGui.text("length : " + _preview.getLength());
        }


        ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
        ImGui.sameLine();
        if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
            SoundWave tex = new SoundWave(getTargetFileName(), getSelectedSource(), new File(getTargetFilePath()));
            tex.save();
            close();
        }

    }

    @Override
    public void onSourceChanged() {
        super.onSourceChanged();

        _failed = false;
        try {
            if (_preview != null) _preview.unload();
            _preview = AudioFactory.FromFile("previewResource_" + getSelectedSource().getName(), getSelectedSource());
            if (_preview != null) ResourceManager.UnRegisterResource(_preview);
            else {
                _failed = true;
                Log.Warning("failed to load preview resource");
            }
        }
        catch (Exception e) {
            _failed = true;
            Log.Warning("failed to load preview resource : " + e.getMessage());
        }
    }

    @Override
    public void close() {
        super.close();
        if (_preview != null) _preview.unload();
    }
}
