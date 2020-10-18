package Core.UI.HUD;

import imgui.ImColor;

public class ImageParams {
    private ImageParams() {}
    private static final ImageParams _instance = new ImageParams();

    public static ImageParams Get(int textureId, float rounding) {
        _instance._textureId = textureId;
        _instance._rounding = rounding;
        _instance._color = ImColor.intToColor(255, 255, 255);
        return _instance;
    }

    public static ImageParams Get(int textureId, float rounding, int color) {
        _instance._textureId = textureId;
        _instance._rounding = rounding;
        _instance._color = color;
        return _instance;
    }

    public int _textureId;
    public float _rounding;
    public int _color;
}
