package Core.UI.PropertyHelper.Writers;

import Core.Assets.Asset;
import Core.Assets.AssetReference;
import Core.IO.LogOutput.Log;
import Core.UI.PropertyHelper.FieldWriter;
import Core.UI.Tools.AssetPicker;
import imgui.ImGui;

import java.lang.reflect.Field;

public class AssetWriter extends FieldWriter {
    public AssetWriter(Class cl) {
        super(cl);
    }

    @Override
    protected void draw(Field field, Object object) throws IllegalAccessException {

        Asset foundAsset = null;

        AssetReference ref = (AssetReference)field.get(object);

        if (ref != null) {
            foundAsset = (Asset) ref.get();
        };

        ImGui.text(field.getName());
        ImGui.sameLine();
        if (ImGui.button(foundAsset == null ? "none" : foundAsset.getName(), ImGui.getContentRegionAvailX(), 0.f)) {
            new AssetPicker("Pick asset for " + field.getName(), ref);
        }
    }
}
