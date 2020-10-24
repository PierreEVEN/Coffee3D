package Core.Assets;

import Core.IO.LogOutput.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Handle assets by names
 */
public class AssetManager {
    private static final HashMap<String, Asset> _assets = new HashMap<>();

    public static Collection<Asset> GetAssets() { return _assets.values(); }

    public static <T> List<T> GetAssetByClass(Class desiredClass) {
        List<T> ret = new ArrayList<>();
        for (Asset asset : GetAssets()) {
            T assetRef = (T)asset;
            if (assetRef != null && asset.getClass().isAssignableFrom(desiredClass)) ret.add(assetRef);
        }
        return ret;
    }

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
        if (asset != null && (T)asset != null) {
            return (T)asset;
        }
        return null;
    }

    public static boolean CanCreateAssetWithName(String name) {
        if (name.equals("")) return false;
        if (AssetManager.FindAsset(name) != null) return false;
        if (!name.matches("^[a-zA-Z0-9]*$")) return false;
        return true;
    }


}
