package Core.Assets;

import Core.IO.Log;

import java.util.HashMap;

public class AssetManager {
    private static HashMap<String, Asset> _assets = new HashMap<>();
    public static HashMap<String, Asset> GetAssets() { return _assets; }

    public static void RegisterAsset(Asset asset) {
        if (_assets.containsKey(asset.getName())) {
            Log.Fail("Asset named " + asset.getName() + " already exist");
            return;
        }
        _assets.put(asset.getName(), asset);
    }

    public static void ClearAssets() {
        _assets.clear();
    }

    public static Asset GetAsset(String assetName) {
        Asset asset = _assets.get(assetName);
        if (asset != null) {
            return asset;
        }
        Log.Warning("failed to find asset '" + assetName + "'");
        return null;
    }



}
