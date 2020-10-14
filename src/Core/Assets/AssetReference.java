package Core.Assets;

import java.io.Serializable;
import java.lang.reflect.Type;

public class AssetReference <T> implements Serializable {
    private transient T _asset;
    private String _assetName;
    private final Class _assetClass;

    public AssetReference(Class assetClass, T asset) {
        set(asset);
        _assetClass = assetClass;
    }

    public Class getGenericClass() {
        return _assetClass;
    }

    public Type getType() {
        return this.getClass().getGenericSuperclass();
    }

    public T get() {
        if (_assetName == null) return null;
        if (_asset == null) _asset = AssetManager.FindAsset(_assetName);
        return _asset;
    }

    public void set(T asset) {
        _asset = asset;
        _assetName = asset == null ? null : ((Asset)asset).getName();
    }
}
