package Core.Assets;

import Core.IO.LogOutput.Log;

import java.io.File;
import java.util.*;

/**
 * Handle assets by names
 */
public class AssetManager {
    private static final List<Asset> assetsByClass = new ArrayList<>();
    private static final HashMap<String, Asset> _assets = new HashMap<>();

    public static Collection<Asset> GetAssets() { return _assets.values(); }

    public static <T> List<Asset> GetAssetByClass(Class desiredClass) {
        assetsByClass.clear();
        for (Asset asset : GetAssets()) {
            T assetRef = (T)asset;
            if (assetRef != null && asset.getClass().isAssignableFrom(desiredClass)) assetsByClass.add((Asset) assetRef);
        }
        return assetsByClass;
    }

    /**
     * register asset
     * @param asset new asset
     */
    public static void RegisterAsset(Asset asset) {
        if (asset.getSavePath() == null) return;
        if (_assets.containsKey(asset.getName())) {
            Log.Fail("Asset named " + asset.getName() + " already exist : " + asset.getSourcePath());
            return;
        }
        _assets.put(asset.getName(), asset);
    }

    public static void UnRegisterAsset(Asset asset) {
        if (_assets.containsKey(asset.getName())) {
            _assets.remove(asset.getName());
        }
    }


    /**
     * Find asset into database by name
     * @param assetName asset name
     * @param <T>       asset type
     * @return found asset (or null)
     */
    public static <T> T FindAsset(String assetName) {
        Asset asset = _assets.get(assetName);
        if (asset != null && (T)asset != null) {
            return (T)asset;
        }
        return null;
    }

    public static boolean IsAssetNameFree(String name) {
        if (name.equals("")) return false;
        if (AssetManager.FindAsset(name) != null) return false;
        if (!name.matches("^[a-zA-Z0-9]*$")) return false;
        return true;
    }

    public static void LoadAssetLibrary(File path) {
        for (File asset : ScanAssets(path)) {
            Asset newAsset = Asset.deserializeAsset(asset);
            if (newAsset != null) {
                newAsset.setSavePath(asset.getPath());
                AssetManager.RegisterAsset(newAsset);
                newAsset.load();
            }
            else {
                Log.Warning("failed to load asset : " + asset);
            }
        }
    }

    public static ArrayList<File> ScanAssets(File path) {
        final ArrayList<File> scannedAssets = new ArrayList<>();
        for (File subFile : path.listFiles()) {
            if (subFile.isDirectory()) {
                scannedAssets.addAll(ScanAssets(subFile));
            }
            else {
                Optional<String> extension = Optional.ofNullable(subFile.getName())
                        .filter(f -> f.contains("."))
                        .map(f -> f.substring(subFile.getName().lastIndexOf(".") + 1));
                if (extension.isEmpty()) continue;
                if (extension.get().equals("asset")) {
                    scannedAssets.add(subFile);
                }
            }
        }
        return scannedAssets;
    }


    private static final transient List<IAssetRenamed> _renameAssetEvent = new ArrayList<>();
    public static void bindOnRenameAsset(IAssetRenamed event) {
        _renameAssetEvent.add(event);
    }

    public static void RenameAsset(String oldName, String newName) {
        for (IAssetRenamed event : _renameAssetEvent) {
            event.rename(oldName, newName);
        }
    }
}
