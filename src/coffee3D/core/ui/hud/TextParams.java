package coffee3D.core.ui.hud;

import coffee3D.core.assets.types.Font;
import imgui.ImFont;
import imgui.ImGui;

public class TextParams {
    private TextParams() {}
    private static final TextParams _instance = new TextParams();

    public static TextParams Get(String text, float size, int color) {
        _instance._text = text;
        _instance._size = size;
        _instance._color = color;
        _instance._alignment = TextAlignment.CENTERED;
        if (_instance._font == null) _instance._font = ImGui.getFont();
        return _instance;
    }

    public static TextParams Get(String text, float size, int color, TextAlignment alignment) {
        _instance._text = text;
        _instance._size = size;
        _instance._color = color;
        _instance._alignment = alignment;
        if (_instance._font == null) _instance._font = ImGui.getFont();
        return _instance;
    }

    public static TextParams Get(String text, float size, int color, Font font, TextAlignment alignment) {
        _instance._text = text;
        _instance._size = size;
        _instance._color = color;
        _instance._font = font.getFont();
        _instance._alignment = alignment;
        if (_instance._font == null) _instance._font = ImGui.getFont();
        return _instance;
    }

    public TextAlignment _alignment;
    public String _text;
    public float _size;
    public int _color;
    public ImFont _font;
}
