package Core.UI.PropertyHelper.Writers;

import Core.Assets.Asset;
import Core.Assets.AssetReference;
import Core.UI.PropertyHelper.FieldWriter;

import java.lang.reflect.Field;

public class AssetWriter extends FieldWriter {
    public AssetWriter() {
        super(AssetReference.class);
    }

    @Override
    protected Object draw(String field, Object object) throws IllegalAccessException {

        AssetReference ref = (AssetReference)object;
        AssetButton.Draw(field, ref);
        if (ref != object) {
            return ref;
        }
        return null;
    }
}
