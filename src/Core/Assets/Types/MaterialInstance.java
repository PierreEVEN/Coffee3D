package Core.Assets.Types;

import Core.Assets.AssetReference;
import Core.Renderer.Scene.Scene;
import Core.Resources.MaterialResource;
import Core.Types.Color;

import java.io.File;

public class MaterialInstance extends MaterialInterface {
    private static final long serialVersionUID = 4204731019018982797L;

    protected AssetReference<MaterialInterface> _parentMaterial;

    private static final Color _matInstAssetColor = new Color(2f, .7f, 2f, 1);

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
        _parentMaterial.get().getResource().use(context);
        bindColor(getColor());
        bindTextures(getTextures());
    }

    @Override
    public void reload() {
        _parentMaterial.get().reload();
    }

    @Override
    public MaterialResource getResource() {
        return _parentMaterial.get().getResource();
    }
}
