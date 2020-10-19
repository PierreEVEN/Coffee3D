package Core.Assets;

import Core.Renderer.Scene.Scene;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector4f;

import java.io.Serializable;

/**
 * Asset base class
 */
public abstract class Asset implements Serializable {
    private static final long serialVersionUID = -5394125677306125615L;

    private final String _name;
    private final String _filePath;
    protected Vector4f assetColor;

    protected Asset(String name, String filePath) {
        _filePath = filePath;
        _name = name;
        AssetManager.RegisterAsset(this);
        load();
        assetColor = new Vector4f(.5f, .5f, .5f, .5f);
    }

    /**
     * Load asset into memory
     */
    public abstract void load();

    /**
     * Draw item into given scene
     * @param context draw context
     */
    public abstract void use(Scene context);

    /**
     * get asset name
     * @return asset name
     */
    public String getName() { return _name; }

    /**
     * get asset file path
     * @return relative path
     */
    public String getFilepath() { return _filePath; }

    private boolean bIsAssetDirty = false;
    /**
     * Draw asset editor thumbnail
     */
    public final void drawThumbnail() {
        ImGui.beginGroup();
        ImGui.pushStyleColor(ImGuiCol.Button, assetColor.x, assetColor.y, assetColor.z, assetColor.w);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 3, 3);
        drawThumbnailImage();
        ImGui.popStyleColor();
        ImGui.popStyleVar();
        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.None)) {
            ImGui.setDragDropPayload("DDOP_ASSET", getName().getBytes());
            drawThumbnail();
            ImGui.endDragDropSource();
        }
        ImGui.textWrapped((getName() + (bIsAssetDirty ? "*" : "")));
        ImGui.endGroup();
    }

    protected void drawThumbnailImage() {
        if (ImGui.button(("#" + getName() + (bIsAssetDirty ? "*" : "")), 64, 64)) {
            if (_assetEditFunction != null) _assetEditFunction.applyAsset(this);
        }
    }

    @Override
    public String toString() { return getName(); }

    protected static IEditAsset _assetEditFunction;

    public static void SetAssetEditWidget(IEditAsset assetEditFunction) {
        _assetEditFunction = assetEditFunction;
    }

}
