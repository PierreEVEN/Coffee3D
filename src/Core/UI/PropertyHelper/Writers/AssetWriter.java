package Core.UI.PropertyHelper.Writers;

import Core.Assets.Asset;
import Core.Assets.AssetReference;
import Core.UI.PropertyHelper.FieldWriter;

import java.lang.reflect.Field;

public class AssetWriter extends FieldWriter {
    public AssetWriter() {
        super(Asset.class);
    }

    @Override
    protected boolean draw(Field field, Object object) throws IllegalAccessException {
        AssetReference ref = (AssetReference)field.get(object);
        AssetButton.Draw(field.getName(), ref);
        return false;
    }
}
