package coffee3D.editor.ui.assets;

import coffee3D.core.assets.Asset;
import coffee3D.core.resources.types.TextureResource;
import coffee3D.editor.ui.tools.AssetWindow;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector4f;

public class EditorAssetUtils {


    public static void DrawAssetThumbnail(Asset asset) {
        ImGui.beginGroup();
        Vector4f assetColor = asset.getAssetColor().getVector();
        ImGui.pushStyleColor(ImGuiCol.Button, assetColor.x, assetColor.y, assetColor.z, assetColor.w);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 3, 3);
        DrawAssetButton(asset);
        ImGui.popStyleColor();
        ImGui.popStyleVar();
        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.None)) {
            ImGui.setDragDropPayload("DDOP_ASSET", asset.getName().getBytes());
            DrawAssetButton(asset);
            ImGui.endDragDropSource();
        }
        ImGui.textWrapped((asset.getName() + (asset.isDirty() ? "*" : "")));
        ImGui.endGroup();
    }

    private static void DrawAssetButton(Asset asset) {
        TextureResource thumbnail = asset.getThumbnailImage();

        if (thumbnail == null) {
            if (ImGui.button(("#" + asset.getName() + (asset.isDirty() ? "*" : "")), 64, 64)) {
                new AssetWindow(asset, asset.getName());
            }
        }
        else {
            if (ImGui.imageButton(thumbnail.getTextureHandle(), 64, 64, 0, 1, 1, 0)) {
                new AssetWindow(asset, asset.getName());
            }
        }
    }

}
