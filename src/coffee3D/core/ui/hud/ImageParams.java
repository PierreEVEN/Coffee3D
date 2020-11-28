package coffee3D.core.ui.hud;

import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.Texture2D;
import imgui.ImColor;

import java.util.ArrayList;

public class ImageParams {
    private static int _paramCount;

    protected static void ResetParamCount() {
        _paramCount = 0;
    }

    private static ImageParams getInstance() {
        if (_paramCount >= _instance.size()) _instance.add(new ImageParams());
        ImageParams instance = _instance.get(_paramCount);
        _paramCount++;
        return instance;
    }

    private ImageParams() {}
    private static final ArrayList<ImageParams> _instance = new ArrayList<>();

    private static Texture2D whiteTexture;
    private static int getDefaultTextureId() {
        if (whiteTexture == null) whiteTexture = AssetManager.FindAsset("whiteTexture");
        if (whiteTexture == null) return -1;
        return whiteTexture.getTextureID();
    }

    public static ImageParams Get(int textureId, float rounding) {
        ImageParams instance = getInstance();
        instance._textureId = textureId < 0 ? getDefaultTextureId() : textureId;
        instance._rounding = rounding;
        instance._color = ImColor.intToColor(255, 255, 255);
        return instance;
    }

    public static ImageParams Get(int textureId, float rounding, int color) {
        ImageParams instance = getInstance();
        instance._textureId = textureId < 0 ? getDefaultTextureId() : textureId;
        instance._rounding = rounding;
        instance._color = color;
        return instance;
    }

    public static ImageParams Get(int color) {
        ImageParams instance = getInstance();
        instance._textureId = -1;
        instance._rounding = 0;
        instance._color = color;
        return instance;
    }

    public int _textureId;
    public float _rounding;
    public int _color;
}
