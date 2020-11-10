package coffee3D.core.assets;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.factories.TextureFactory;
import coffee3D.core.resources.types.TextureResource;
import coffee3D.core.types.Color;
import coffee3D.core.types.TypeHelper;
import coffee3D.editor.ui.propertyHelper.SerializableData;
import org.joml.Vector2i;

import java.io.*;

import static org.lwjgl.opengl.GL46.*;

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
        if (autoLoad()) load();
    }

    /**
     * Used to manually load resource
     * @return auto load
     */
    public boolean autoLoad() { return true; }

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
            Log.Error("failed to deserialize asset " + filePath.getName() + " : " + e.getMessage());
        }
        return null;
    }

    /******************************************************************
    *                                                                 *
    *                      EDITOR FEATURES                            *
    *                                                                 *
    *******************************************************************/

    private static final Color _defaultAssetColor = new Color(.5f, .5f, .5f, .5f);
    private transient TextureResource thumbnailTexture;
    public static final Vector2i THUMBNAIL_RESOLUTION = new Vector2i(64,64);

    public final TextureResource getThumbnail() {
        if (thumbnailTexture == null) {
            int[] thumbnailData = null;
            if (thumbnailData == null) {
                Vector2i textureSize = new Vector2i();
                int texture  = getThumbnailSourceTexture(textureSize);
                int[] textureRawData;
                if (texture >= 0) {
                    textureRawData = new int[textureSize.x * textureSize.y];
                    glBindTexture(GL_TEXTURE_2D, texture);
                    glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureRawData);
                    glBindTexture(GL_TEXTURE_2D, 0);
                }
                else {
                    texture = getThumbnailSourceBuffer(textureSize);
                    if (texture >= 0) {
                        textureRawData = new int[textureSize.x * textureSize.y];
                        glBindFramebuffer(GL_FRAMEBUFFER, texture);
                        glReadPixels(0, 0, textureSize.x, textureSize.y, GL_RGBA, GL_UNSIGNED_BYTE, textureRawData);
                        glBindFramebuffer(GL_FRAMEBUFFER, 0);
                    }
                    else return null;
                }
                thumbnailData = new int[THUMBNAIL_RESOLUTION.x * THUMBNAIL_RESOLUTION.y];
                TextureResource.resizeTextureData(textureRawData, thumbnailData, textureSize.x, textureSize.y, THUMBNAIL_RESOLUTION.x, THUMBNAIL_RESOLUTION.y);
            }
            thumbnailTexture = TextureFactory.T2dFromData("thumbnail_" + getName() + "_" + TypeHelper.MakeGlobalUid(), thumbnailData, THUMBNAIL_RESOLUTION.x, THUMBNAIL_RESOLUTION.y, false);
        }
        return thumbnailTexture;
    }

    public int getThumbnailSourceTexture(Vector2i textureSize) { return -1; }
    public int getThumbnailSourceBuffer(Vector2i textureSize) { return -1; }

    public String[] getAssetExtensions() { return null; }

    public Color getAssetColor() { return _defaultAssetColor; }

    public void setSavePath(File path) { _assetPath = path; }

    public File getSavePath() { return _assetPath; }

    public void drawDetailedContent() {}
}
