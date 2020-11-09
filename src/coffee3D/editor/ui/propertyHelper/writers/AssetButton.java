package coffee3D.editor.ui.propertyHelper.writers;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.AssetReference;
import coffee3D.editor.ui.assets.EditorAssetUtils;
import coffee3D.editor.ui.propertyHelper.AssetPicker;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.List;

public class AssetButton {


    private static List<Asset> _availableAssets;

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
            _availableAssets = AssetManager.GetAssetByClass(assetRef.getType());
            ImGui.openPopup("PickAssetPopup");
        }
        if (ImGui.beginPopup("PickAssetPopup")) {
            drawPopupContent(assetRef);
            ImGui.endPopup();
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


    private static <T> void drawPopupContent(AssetReference<T> assetRef) {
        for (int i = 0; i < _availableAssets.size(); ++i) {
            ImGui.image(_availableAssets.get(i).getThumbnail().getTextureHandle(), 16, 16, 0 ,1 ,1 ,0);
            ImGui.sameLine();
            if (ImGui.menuItem(_availableAssets.get(i).getName())) {
                assetRef.set((T) _availableAssets.get(i));
            }
        }
    }

}
