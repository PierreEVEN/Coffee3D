package coffee3D.core.ui.hud;

import imgui.ImFont;
import imgui.ImGui;

public class TextParams {
    private TextParams() {}
    private static final TextParams _instance = new TextParams();

    public static TextParams Get(String text, float size, int color) {
        _instance._text = text;
        _instance._size = size;
        _instance._color = color;
        if (_instance._font == null) _instance._font = ImGui.getFont();
        return _instance;
    }

    public static TextParams Get(String text, float size, int color, ImFont font) {
        _instance._text = text;
        _instance._size = size;
        _instance._color = color;
        _instance._font = font;
        if (_instance._font == null) _instance._font = ImGui.getFont();
        return _instance;
    }

    public String _text;
    public float _size;
    public int _color;
    public ImFont _font;
}
