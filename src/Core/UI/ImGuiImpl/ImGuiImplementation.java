package Core.UI.ImGuiImpl;

import Core.IO.Inputs.GlfwInputHandler;
import Core.IO.LogOutput.Log;
import imgui.*;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseCursor;
import imgui.gl3.ImGuiImplGl3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

final class FontSet {
    int fontSize;
    String fontPath;
}

public class ImGuiImplementation {

    private ArrayList<FontSet> fonts;

    private static ImGuiImplementation _instance;
    public static ImGuiImplementation Get() {
        if (_instance == null) _instance = new ImGuiImplementation();
        return _instance;
    }

    public void addFont(String fontPath, int size) {
        if (fonts == null) fonts = new ArrayList<>();
        FontSet newFont = new FontSet();
        newFont.fontPath = fontPath;
        newFont.fontSize = size;
        fonts.add(newFont);
    }

    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private static final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

    public void render() {
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    public void preInit(long glfwWindowHandle) {
// IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ImGui provides 3 different color schemas for styling. We will use the classic one here.
        // Try others with ImGui.styleColors*() methods.
        ImGui.styleColorsDark();

        ImGuiStyle style = ImGui.getStyle();
        style.setWindowRounding(0);
        style.setScrollbarRounding(0);
        style.setTabRounding(0);
        style.setWindowBorderSize(1);
        style.setPopupBorderSize(1);
        style.setWindowTitleAlign(0.5f, style.getWindowTitleAlignY());
        style.setFramePadding(6.f, 6.f);
        style.setWindowPadding(4.f, 4.f);
        style.setGrabMinSize(16.f);
        style.setScrollbarSize(20.f);
        style.setIndentSpacing(30.f);

        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Navigation with keyboard
        io.setConfigFlags(ImGuiConfigFlags.NavEnableGamepad); // Navigation with keyboard
        io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
        io.setBackendPlatformName("imgui_java_impl_glfw"); // For clarity reasons
        io.setBackendRendererName("imgui_java_impl_lwjgl"); // For clarity reasons
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        final int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
        keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
        keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
        keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
        keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
        keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
        keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
        keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
        keyMap[ImGuiKey.End] = GLFW_KEY_END;
        keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
        keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
        keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
        keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
        keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
        keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
        keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
        keyMap[ImGuiKey.A] = GLFW_KEY_A;
        keyMap[ImGuiKey.C] = GLFW_KEY_C;
        keyMap[ImGuiKey.V] = GLFW_KEY_V;
        keyMap[ImGuiKey.X] = GLFW_KEY_X;
        keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
        keyMap[ImGuiKey.Z] = GLFW_KEY_Z;
        io.setKeyMap(keyMap);

        // Mouse cursors mapping
        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
        mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    }
    
    public void init(long glfwWindowHandle) {

        final ImGuiIO io = ImGui.getIO();

        // ------------------------------------------------------------
        // Fonts configuration

        // -------------------
        // Fonts merge example

        final ImFontAtlas fontAtlas = io.getFonts();

        // First of all we add a default font, which is 'ProggyClean.ttf, 13px'

        final ImFontConfig fontConfig = new ImFontConfig(); // Keep in mind that creation of the ImFontConfig will allocate native memory
        fontConfig.setMergeMode(true); // All fonts added while this mode is turned on will be merged with the previously added font
        fontConfig.setPixelSnapH(true);
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic()); // Additional glyphs could be added like this or in addFontFrom*() methods

        // We merge font loaded from resources with the default one. Thus we will get an absent cyrillic glyphs
        //fontAtlas.addFontFromMemoryTTF(loadFromResources("basis33.ttf"), 16, fontConfig);

        // Disable merged mode and add all other fonts normally
        fontConfig.setMergeMode(false);
        fontConfig.setPixelSnapH(false);

        // ------------------------------
        // Fonts from file/memory example

        fontConfig.setRasterizerMultiply(1.2f); // This will make fonts a bit more readable

        if (fonts != null) {

            String defaultFont = null;

            for(FontSet font : fonts) {
                fontAtlas.addFontFromFileTTF(font.fontPath, font.fontSize, fontConfig);
            }
        }

        fontConfig.destroy(); // After all fonts were added we don't need this config more

        // IMPORTANT!!!
        // Method initializes renderer itself.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGl3.init();
        GlfwInputHandler.AddListener(new ImGuiInputListener(glfwWindowHandle));
    }

    public void shutDown() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }
}
