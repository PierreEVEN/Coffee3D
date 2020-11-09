package coffee3D.editor.ui.assets;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.types.Font;
import coffee3D.core.assets.types.Material;
import coffee3D.core.io.log.Log;
import coffee3D.core.resources.types.TextureResource;
import coffee3D.core.types.TypeHelper;
import coffee3D.editor.ui.tools.AssetWindow;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class EditorAssetUtils {


    public static void DrawAssetThumbnail(Asset asset) {
        if (asset == null) return;

        ImGui.beginGroup();
        DrawAssetButton(asset);
        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.None)) {
            ImGui.setDragDropPayload("DDOP_ASSET", asset.getName().getBytes());
            DrawAssetButton(asset);
            ImGui.endDragDropSource();
        }
        ImGui.textWrapped((asset.getName() + (asset.isDirty() ? "*" : "")));
        ImGui.endGroup();
    }

    public static void DrawAssetButton(Asset asset) {
        if (asset == null) return;

        Vector4f assetColor = asset.getAssetColor().getVector();
        ImGui.pushStyleColor(ImGuiCol.Button, assetColor.x, assetColor.y, assetColor.z, assetColor.w);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 4);

        TextureResource thumbnail = asset.getThumbnail();

        if (thumbnail == null) {
            if (asset instanceof Font) asset.use(null);
            if (ImGui.button(("#" + asset.getName() + (asset.isDirty() ? "*" : "")), 64, 64)) {
                new AssetWindow(asset, asset.getName());
            }
            if (asset instanceof Font) ((Font)asset).pop();
        } else {
            if (ImGui.imageButton(thumbnail.getTextureHandle(), 64, 64, 0, 1, 1, 0)) {
                new AssetWindow(asset, asset.getName());
            }
        }
        ImGui.popStyleColor();
        ImGui.popStyleVar();
    }

}
