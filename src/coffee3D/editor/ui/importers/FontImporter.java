package coffee3D.editor.ui.importers;

import coffee3D.core.assets.types.Font;
import coffee3D.core.assets.types.Texture2D;
import coffee3D.core.io.log.Log;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.resources.factories.FontFactory;
import coffee3D.core.resources.factories.TextureFactory;
import coffee3D.core.resources.types.FontResource;
import coffee3D.core.resources.types.Texture2DResource;
import imgui.ImGui;

import java.io.File;

public class FontImporter extends AssetImporter {

    private FontResource _preview;
    private boolean _failed;
    private final int[] _size = new int[] { 15 };

    public FontImporter(String windowName) {
        super(windowName);
        _extensionFilter = new String[] {"ttf", "TTF"};
        _failed = false;
    }

    @Override
    protected void draw() {
        super.draw();

        ImGui.text("font size");
        ImGui.sameLine();
        ImGui.sliderInt("##fontSize", _size, 2, 50);

        if (!canImport()) return;


        if (_failed && _preview == null) {
            ImGui.text("invalid asset");
            return;
        }
        if (!_failed && _preview != null) {
            _preview.use(null);
            _preview.pop();
        }

        ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
        ImGui.sameLine();
        if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
            Font font = new Font(getTargetFileName(), getSelectedSource(), new File(getTargetFilePath()), _size[0]);
            font.save();
            close();
        }

    }

    @Override
    public void onSourceChanged() {
        super.onSourceChanged();

        _failed = false;
        try {
            if (_preview != null) _preview.unload();
            _preview = FontFactory.FromFile("previewResource_" + getSelectedSource().getName(), getSelectedSource(), _size[0]);
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
