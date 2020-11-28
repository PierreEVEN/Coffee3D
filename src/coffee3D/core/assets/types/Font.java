package coffee3D.core.assets.types;

import coffee3D.core.assets.Asset;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.resources.factories.FontFactory;
import coffee3D.core.resources.types.FontResource;
import coffee3D.core.types.Color;
import imgui.ImFont;

import java.io.File;

public class Font extends Asset {

    private static final long serialVersionUID = 6637107681200457232L;

    private static final Color _fontAssetColor = new Color(51/255f, 51/255f, 51/255f, 1);
    private static final String[] fontExtensionsExtensions = new String[] {"ttf", "TTF"};

    private transient FontResource _font;
    protected float _fontSize;

    public Font(String assetName, File sourceFilePath, File assetPath, float fontSize) {
        super(assetName, sourceFilePath, assetPath);
        _fontSize = fontSize;
        load();
    }

    @Override
    public boolean autoLoad() { return false; }

    @Override
    public void load() {
        _font = FontFactory.FromFile(getName(), getSourcePath(), _fontSize);
    }

    @Override
    public void reload() {
        ResourceManager.UnRegisterResource(_font);
        FontResource newFont = null;

        try {
            newFont = FontFactory.FromFile(getName(), getSourcePath(), _fontSize);
        }
        catch (Exception e) {
            Log.Warning("failed to load font : " + e.getMessage());
        }

        if (newFont != null) _font = newFont;
        else ResourceManager.RegisterResource(_font);
    }

    @Override
    public void use(Scene context) {
        _font.use(context);
    }

    public void use() {
        use(null);
    }

    public void pop() {
        _font.pop();
    }

    public void setDefault() {
        _font.setDefault();
    }

    public ImFont getFont() { return _font.getFont(); }

    @Override
    public String[] getAssetExtensions() {
        return fontExtensionsExtensions;
    }

    @Override
    public Color getAssetColor() {
        return _fontAssetColor;
    }
}
