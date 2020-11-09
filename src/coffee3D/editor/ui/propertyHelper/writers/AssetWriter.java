package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.io.log.Log;
import coffee3D.editor.ui.propertyHelper.FieldWriter;

public class AssetWriter extends FieldWriter {
    public AssetWriter() {
        super(AssetReference.class);
    }

    @Override
    protected Object draw(String field, Object object) {

        AssetReference ref = (AssetReference)object;
        if (AssetButton.Draw(field, ref)) {
            Log.Warning("edited");
            return ref;
        }
        return null;
    }
}
