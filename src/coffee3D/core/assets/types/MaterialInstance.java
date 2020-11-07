package coffee3D.core.assets.types;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.types.MaterialResource;
import coffee3D.core.types.Color;

import java.io.File;

public class MaterialInstance extends MaterialInterface {
    private static final long serialVersionUID = 4204731019018982797L;

    protected AssetReference<MaterialInterface> _parentMaterial;

    private static final Color _matInstAssetColor = new Color(0/255f, 102/255f, 0/255f, 1f);

    @Override
    public Color getAssetColor() { return _matInstAssetColor; }

    @Override
    public void load() {}

    public MaterialInstance(String name, AssetReference<MaterialInterface> parentMaterial, File assetPath) {
        super(name, null, assetPath, parentMaterial.get().cloneTextures());
        _parentMaterial = parentMaterial;
    }

    @Override
    public void use(Scene context) {
        if (_parentMaterial.get() != this) _parentMaterial.get().getResource().use(context);
        bindColor(getColor());
        bindTextures(getTextures());
    }

    @Override
    public void reload() {
        if (_parentMaterial.get() == this) return;
        _parentMaterial.get().reload();
    }

    @Override
    public MaterialResource getResource() {
        if (_parentMaterial.get() == this) return null;
        return _parentMaterial.get().getResource();
    }
}
