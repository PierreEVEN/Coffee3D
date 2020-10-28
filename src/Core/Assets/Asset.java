package Core.Assets;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import Core.Types.Color;
import Core.UI.PropertyHelper.SerializableData;
import Editor.UI.Browsers.FileBrowser;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;

import java.io.*;

/**
 * Asset base class
 */
public abstract class Asset extends SerializableData {
    private static final long serialVersionUID = -5394125677306125615L;

    private String _name;
    private File _sourcePath;
    private transient File _assetPath;
    private static final Color _defaultColor = new Color(.5f, .5f, .5f, .5f);
    private transient ImString newAssetName;

    protected Asset(String name, File sourcePath, File assetPath) {
        _sourcePath = sourcePath;
        _name = name;
        _assetPath = assetPath;
        AssetManager.RegisterAsset(this);
        load();
        if (assetPath != null) Log.Display("import " + name + " (" + getClass().getSimpleName() + ") from " + sourcePath + " to " + assetPath);
    }

    public String[] getAssetExtensions() { return null; }

    public Color getAssetColor() {
        return _defaultColor;
    }

    public void setSavePath(String path) {
        _assetPath = new File(path);
    }

    public File getSavePath() {
        return _assetPath;
    }

    /**
     * Load asset into memory
     */
    public abstract void load();

    public abstract void reload();

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
    public File getSourcePath() { return _sourcePath; }

    /**
     * Draw asset editor thumbnail
     */
    public final void drawThumbnail() {
        ImGui.beginGroup();
        ImGui.pushStyleColor(ImGuiCol.Button, getAssetColor().getVector().x, getAssetColor().getVector().y, getAssetColor().getVector().z, getAssetColor().getVector().w);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 3, 3);
        drawThumbnailImage();
        ImGui.popStyleColor();
        ImGui.popStyleVar();
        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.None)) {
            ImGui.setDragDropPayload("DDOP_ASSET", getName().getBytes());
            drawThumbnail();
            ImGui.endDragDropSource();
        }
        ImGui.textWrapped((getName() + (isDirty() ? "*" : "")));
        ImGui.endGroup();
    }

    public void drawDetailedContent() {
        if (ImGui.button("save##" + getName() + (isDirty() ? "*" : ""))) {
            save();
        }
        ImGui.sameLine();
        if (ImGui.button("reload resource")) {
            reload();
        }
        ImGui.text("source file : ");
        ImGui.sameLine();
        if (ImGui.button(_sourcePath.exists() ? _sourcePath.getPath() : "none")) {
            new FileBrowser("find asset", getAssetExtensions(), _sourcePath, file -> {
                _sourcePath = file;
                reload();
            });
        }
        if (newAssetName == null) newAssetName = new ImString(_name);
        ImGui.inputText("asset name", newAssetName);
        if (!newAssetName.get().equals(getName())) {
            if (!AssetManager.IsAssetNameFree(newAssetName.get())) {
                ImGui.pushStyleColor(ImGuiCol.Text, ImColor.floatToColor(1, .5f, .5f));
                ImGui.sameLine();
                ImGui.text("invalid name");
                ImGui.popStyleColor();
            }
            else {
                AssetManager.UnRegisterAsset(this);
                String oldName = _name;
                _name = newAssetName.get();
                AssetManager.RegisterAsset(this);
                AssetManager.RenameAsset(oldName, _name);
            }
        }
    }

    protected void drawThumbnailImage() {
        if (ImGui.button(("#" + getName() + (isDirty() ? "*" : "")), 64, 64)) {
            if (_assetEditFunction != null) _assetEditFunction.applyAsset(this);
        }
    }

    @Override
    public String toString() { return getName(); }

    protected static IAssetEdited _assetEditFunction;

    public static void SetAssetEditWidget(IAssetEdited assetEditFunction) {
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

    public static Asset deserializeAsset(File filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Asset asset = (Asset) ois.readObject();
            if (asset != null) return asset;
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.Warning("failed to deserialize scene : " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save() {
        super.save();
        serializeAsset(this);
    }
}
