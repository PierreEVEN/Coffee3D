package coffee3D.core.assets;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.types.TextureResource;
import coffee3D.core.types.Color;
import coffee3D.editor.ui.propertyHelper.SerializableData;

import java.io.*;

/**
 * Asset base class
 */
public abstract class Asset extends SerializableData {
    private static final long serialVersionUID = -5394125677306125615L;

    /**
     * Asset name (should be unique)
     */
    private String _assetName;

    /**
     * Path of the loaded source file
     */
    private File _sourceFilePath;

    /**
     * Path of the file where this asset is serialized
     */
    private transient File _assetPath;

    /**
     * Default constructor
     * @param assetName      asset name
     * @param sourceFilePath data file path
     * @param assetPath      serialized file path
     */
    protected Asset(String assetName, File sourceFilePath, File assetPath) {
        _sourceFilePath = sourceFilePath;
        _assetName = assetName;
        _assetPath = assetPath;
        AssetManager.RegisterAsset(this);
        load();
    }

    /**
     * Load asset into memory
     */
    public abstract void load();

    /**
     * Reload resource
     */
    public abstract void reload();

    /**
     * Use item into given scene
     * @param context draw context
     */
    public abstract void use(Scene context);

    /**
     * get asset name
     * @return asset name
     */
    public String getName() { return _assetName; }

    /**
     * Update asset name
     * @param newName new name
     */
    public void updateName(String newName) {
        AssetManager.UnRegisterAsset(this);
        String oldName = getName();
        _assetName = newName;
        AssetManager.RegisterAsset(this);
        AssetManager.OnAssetRenamed(oldName, getName());
    }

    /**
     * get asset file path
     * @return relative path
     */
    public File getSourcePath() { return _sourceFilePath; }

    /**
     * Update asset source path
     * @param source source file
     */
    public void setSourcePath(File source) {
        _sourceFilePath = source;
        reload();
    }

    @Override
    public String toString() { return String.format("[%s:%s]", getClass().getSimpleName(), getName()); }


    /*****************************************************************
    *                                                                *
    *                         SERIALIZATION                          *
    *                                                                *
    ******************************************************************/

    /**
     * Save asset
     */
    @Override
    public void save() {
        super.save();
        serializeAsset();
    }

    /**
     * Serialize asset to it's asset path
     */
    private void serializeAsset() {
        try {
            FileOutputStream fos = new FileOutputStream(_assetPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.Warning("failed to serialise asset " + getName() + " : " + e.getMessage());
        }
    }

    /**
     * Deserialize asset from desired path
     * @param filePath asset path
     * @return deserialized asset
     */
    public static Asset deserializeAsset(File filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Asset asset = (Asset) ois.readObject();
            if (asset != null) return asset;
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.Warning("failed to deserialize asset " + filePath.getName() + " : " + e.getMessage());
        }
        return null;
    }

    /******************************************************************
    *                                                                 *
    *                      EDITOR FEATURES                            *
    *                                                                 *
    *******************************************************************/

    private static final Color _defaultAssetColor = new Color(.5f, .5f, .5f, .5f);

    public String[] getAssetExtensions() { return null; }

    public Color getAssetColor() { return _defaultAssetColor; }

    public void setSavePath(File path) { _assetPath = path; }

    public File getSavePath() { return _assetPath; }

    public TextureResource getThumbnailImage() { return null; }

    public void drawDetailedContent() {}
}
