package Core.Assets;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector4f;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Asset base class
 */
public abstract class Asset implements Serializable {
    private static final long serialVersionUID = -5394125677306125615L;

    private final String _name;
    private final String _sourcePath;
    private final String _assetPath;
    private final Vector4f assetColor;

    protected Asset(String name, String sourcePath, String assetPath) {
        _sourcePath = sourcePath;
        _name = name;
        _assetPath = assetPath;
        AssetManager.RegisterAsset(this);
        load();
        assetColor = new Vector4f(.5f, .5f, .5f, .5f);
        Log.Display("import " + name + " (" + getClass().getSimpleName() + ") from " + sourcePath + " to " + assetPath);
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
    public String getFilepath() { return _sourcePath; }

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

    public void drawDetailedContent() {}

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

    public static void serializeAsset(Asset sourceAsset) {
        try {
            FileOutputStream fos = new FileOutputStream(sourceAsset._assetPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sourceAsset);
            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.Warning("failed to serialise scene : " + e.getMessage());
        }
    }

    public static Asset deserializeAsset() {
        return null;
    }

    public void save() {
        serializeAsset(this);
    }
}
