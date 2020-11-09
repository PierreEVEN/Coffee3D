package coffee3D.core.resources.types;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.GraphicResource;
import imgui.*;

import java.io.File;

public class FontResource extends GraphicResource {

    private File _resourceFilePath;
    private float _size;
    private ImFont _font;
    private static ImFontConfig _fontConfig;

    private static ImFontConfig GetFontConfig() {
        if (_fontConfig == null) _fontConfig = new ImFontConfig(); // Keep in mind that creation of the ImFontConfig will allocate native memory
        final ImGuiIO io = ImGui.getIO();
        final ImFontAtlas fontAtlas = io.getFonts();
        _fontConfig.setMergeMode(true); // All fonts added while this mode is turned on will be merged with the previously added font
        _fontConfig.setPixelSnapH(true);
        _fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic()); // Additional glyphs could be added like this or in addFontFrom*() methods

        // We merge font loaded from resources with the default one. Thus we will get an absent cyrillic glyphs
        //fontAtlas.addFontFromMemoryTTF(loadFromResources("basis33.ttf"), 16, fontConfig);

        // Disable merged mode and add all other fonts normally
        _fontConfig.setMergeMode(false);
        _fontConfig.setPixelSnapH(false);

        // ------------------------------
        // Fonts from file/memory example

        _fontConfig.setRasterizerMultiply(1.2f); // This will make fonts a bit more readable
        return _fontConfig;
    }


    public FontResource(String resourceName, File resourceFilePath, float size) {
        super(resourceName);
        _resourceFilePath = resourceFilePath;
        _size = size;
        _font = null;
    }

    @Override
    public void load() {
        ImGuiIO io = ImGui.getIO();
        final ImFontAtlas fontAtlas = io.getFonts();
        _font = fontAtlas.addFontFromFileTTF(_resourceFilePath.getPath(), _size, GetFontConfig());
    }

    @Override
    public void unload() {

    }

    @Override
    public void use(Scene context) {
        if (_font != null) ImGui.pushFont(_font);
    }

    public void pop() {
        if (_font != null) ImGui.popFont();
    }

    public void setDefault() {
        if (_font != null) ImGui.getIO().setFontDefault(_font);
    }
}
