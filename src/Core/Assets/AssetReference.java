package Core.Assets;

import Core.IO.LogOutput.Log;

import java.io.Serializable;
import java.lang.reflect.Type;

public class AssetReference <T> implements Serializable {
    private static final long serialVersionUID = -1947307016044018124L;
    private transient T _asset;
    private String _assetName;
    private final Class _assetClass;

    public AssetReference(Class assetClass, T asset) {
        _assetClass = assetClass;
        set(asset);
        bindEvent();
    }

    public AssetReference(Class assetClass) {
        _assetClass = assetClass;
        set(null);
        bindEvent();
    }

    public AssetReference(Class assetClass, String asset) {
        _assetClass = assetClass;
        set(AssetManager.FindAsset(asset));
        bindEvent();
    }

    private void bindEvent() {
        AssetManager.bindOnRenameAsset((oldName, newName) -> {
            if (oldName.equals(_assetName)) {
                _assetName = newName;
                _asset = AssetManager.FindAsset(_assetName);
            }
        });
    }

    public Class getGenericClass() {
        return _assetClass;
    }

    public Class getType() {
        return _assetClass;
    }

    public T get() {
        if (_assetName == null) return null;
        if (_asset == null) _asset = AssetManager.FindAsset(_assetName);
        return _asset;
    }

    public String getName() { return _assetName; }

    public void set(T asset) {
        if (asset != null && (!_assetClass.isAssignableFrom(asset.getClass()))) return;
        _asset = asset;
        _assetName = asset == null ? null : ((Asset)asset).getName();
    }
}
