package Editor.UI.Tools;

import Core.Assets.Asset;
import Core.IO.LogOutput.Log;
import Core.UI.PropertyHelper.StructureReader;
import Core.UI.SubWindows.SubWindow;

import java.lang.reflect.Field;

public class AssetWindow extends SubWindow {

    private Asset _editedAsset;

    public AssetWindow(Asset asset, String windowName) {
        super(windowName);
        _editedAsset = asset;
    }

    @Override
    protected void draw() {
        _editedAsset.drawDetailedContent();
        StructureReader.WriteObj(_editedAsset, _editedAsset.getName());
    }
}
