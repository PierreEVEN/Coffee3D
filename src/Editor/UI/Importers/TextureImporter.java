package Editor.UI.Importers;

import Core.Assets.Types.Texture2D;
import Core.Factories.TextureFactory;
import Core.IO.LogOutput.Log;
import Core.Resources.ResourceManager;
import Core.Resources.Texture2DResource;
import imgui.ImGui;

import java.io.File;

public class TextureImporter extends AssetImporter {

    private Texture2DResource _preview;
    private boolean _failed;

    public TextureImporter(String windowName) {
        super(windowName);
        _extensionFilter = new String[] {"png"};
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
            ImGui.image(_preview.getTextureHandle(), _preview.getWidth(), _preview.getHeight(), 0, 1, 1, 0);
        }


        ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
        ImGui.sameLine();
        if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
            Texture2D tex = new Texture2D(getTargetFileName(), getSelectedSource(), new File(getTargetFilePath()));
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
            _preview = TextureFactory.T2dFromFile("previewResource_" + getSelectedSource().getName(), getSelectedSource());
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
