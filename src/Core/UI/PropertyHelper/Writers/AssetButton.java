package Core.UI.PropertyHelper.Writers;

import Core.Assets.Asset;
import Core.Assets.AssetManager;
import Core.Assets.AssetReference;
import Core.IO.LogOutput.Log;
import Core.UI.Tools.AssetPicker;
import imgui.ImGui;

import java.lang.reflect.Field;

public class AssetButton {

    public static <T> boolean Draw(String fieldName, AssetReference<T> assetRef) {
        Asset foundAsset = null;


        if (assetRef != null) {
            foundAsset = (Asset) assetRef.get();
        }
        ImGui.beginGroup();
        if (ImGui.button(foundAsset == null ? "none" : foundAsset.getName(), ImGui.getContentRegionAvailX(), 0.f)) {
            new AssetPicker("Pick asset for " + fieldName, assetRef, null);
        }
        ImGui.endGroup();

        if (ImGui.beginDragDropTarget())
        {
            byte[] data = ImGui.acceptDragDropPayload ("DDOP_ASSET");
            if (data != null)
            {
                String assetName = new String(data);
                Asset droppedAsset = AssetManager.FindAsset(assetName);
                if (droppedAsset != null) {
                    assetRef.set((T)droppedAsset);
                    return true;
                }
            }
        }
        return false;
    }

}
