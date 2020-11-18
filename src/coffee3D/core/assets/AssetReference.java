package coffee3D.core.assets;

import java.io.Serializable;

public class AssetReference <T> implements Serializable {
    private static final long serialVersionUID = -1947307016044018124L;
    private transient T _asset;
    private String _assetName;
    private final Class _assetClass;
    private transient boolean isBound = false;

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
        isBound = true;
    }

    public Class getGenericClass() {
        return _assetClass;
    }

    public Class getType() {
        return _assetClass;
    }

    public T get() {
        if (_assetName == null) return null;
        if (_asset == null) {
            _asset = AssetManager.FindAsset(_assetName);
            if (!isBound) bindEvent();
        }
        return _asset;
    }

    public String getName() { return _assetName; }

    public void set(T asset) {
        if ((asset != null && (!_assetClass.isAssignableFrom(asset.getClass()))) || _asset == asset) return;
        _asset = asset;
        _assetName = asset == null ? null : ((Asset)asset).getName();
        bindEvent();
    }
}
