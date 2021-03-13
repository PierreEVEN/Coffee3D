package coffee3D.core.resources.factories;

import coffee3D.core.assets.types.Font;
import coffee3D.core.io.log.Log;
import coffee3D.core.resources.types.FontResource;
import imgui.ImGui;

import java.io.File;
import java.util.ArrayList;

public class FontFactory {

    private final static ArrayList<FontResource> _delayedFonts = new ArrayList<>();

    public static FontResource FromFile(String resourceName, File filePath, float fontSize) {
        FontResource font = new FontResource(resourceName, filePath, fontSize);
        _delayedFonts.add(font);
        return font;
    }

    public static void FlushDelayedFonts() {
        for (FontResource font : _delayedFonts) {
            font.load();
        }
        _delayedFonts.clear();
    }
}
