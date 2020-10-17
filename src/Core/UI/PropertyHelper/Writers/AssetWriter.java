package Core.UI.PropertyHelper.Writers;

import Core.Assets.Asset;
import Core.Assets.AssetManager;
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
    protected boolean draw(Field field, Object object) throws IllegalAccessException {
        AssetReference ref = (AssetReference)field.get(object);
        AssetButton.Draw(field.getName(), ref);
        return false;
    }
}
