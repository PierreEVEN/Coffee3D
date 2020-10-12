package Core.Assets;

import Core.IO.LogOutput.Log;

import java.util.HashMap;

/**
 * Handle assets by names
 */
public class AssetManager {
    private static final HashMap<String, Asset> _assets = new HashMap<>();

    /**
     * register asset
     * @param asset new asset
     */
    public static void RegisterAsset(Asset asset) {
        if (_assets.containsKey(asset.getName())) {
            Log.Fail("Asset named " + asset.getName() + " already exist");
            return;
        }
        _assets.put(asset.getName(), asset);
    }

    /**
     * Find asset into database by name
     * @param assetName asset name
     * @param <T>       asset type
     * @return found asset (or null)
     */
    public static <T> T FindAsset(String assetName) {
        Asset asset = _assets.get(assetName);
        if (asset != null) {
            return (T)asset;
        }
        Log.Warning("failed to find asset '" + assetName + "'");
        return null;
    }



}
