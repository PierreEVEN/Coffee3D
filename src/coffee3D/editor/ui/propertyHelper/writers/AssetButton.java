package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.AssetReference;
import coffee3D.editor.ui.assets.EditorAssetUtils;
import coffee3D.editor.ui.propertyHelper.AssetPicker;
import imgui.ImGui;

public class AssetButton {

    public static <T> boolean Draw(String fieldName, AssetReference<T> assetRef) {
        Asset foundAsset = null;


        if (assetRef != null) {
            foundAsset = (Asset) assetRef.get();
        }
        ImGui.beginGroup();

        if (foundAsset != null) {
            EditorAssetUtils.DrawAssetButton(foundAsset);
            ImGui.sameLine();
        }
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
