package Core.UI;

import Core.IO.Inputs.GlfwInputHandler;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseCursor;
import imgui.gl3.ImGuiImplGl3;

import java.io.*;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiImplementation {

    private static ImGuiImplementation _instance;
    public static ImGuiImplementation Get() {
        if (_instance == null) _instance = new ImGuiImplementation();
        return _instance;
    }

    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private static final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];


    public void render() {
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    public void init(long glfwWindowHandle) {
// IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ImGui provides 3 different color schemas for styling. We will use the classic one here.
        // Try others with ImGui.styleColors*() methods.
        ImGui.styleColorsClassic();

        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename(null); // We don't want to save .ini file
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Navigation with keyboard
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

        // ------------------------------------------------------------
        // Fonts configuration

        // -------------------
        // Fonts merge example

        final ImFontAtlas fontAtlas = io.getFonts();

        // First of all we add a default font, which is 'ProggyClean.ttf, 13px'
        fontAtlas.addFontDefault();

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

        // We can add new fonts directly from file
        fontAtlas.addFontFromFileTTF("resources/fonts/roboto/Roboto-Medium.ttf", 13, fontConfig);

        fontConfig.destroy(); // After all fonts were added we don't need this config more

        // IMPORTANT!!!
        // Method initializes renderer itself.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGl3.init();

        GlfwInputHandler.AddListener(new ImGuiInputListener(glfwWindowHandle));
    }

}
