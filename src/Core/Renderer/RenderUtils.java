package Core.Renderer;

import Core.IO.Log;
import org.joml.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;

public class RenderUtils {

    public static void InitializeOpenGL(Vector4f clearColor) {

        createCapabilities();

        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);

    }

    public static long InitializeGlfw(int width, int height, String title) {

        long _windowContext = -1;

        Log.Display("Initialize OpenGL");

        GLFWErrorCallback.createPrint(System.err).set();

        Log.Warning("a");
        // Initialize Glfw
        if ( !glfwInit() ) Log.Fail("Failed to initialize glfw");

        // Set glfw parameters
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create glfw windows
        _windowContext = glfwCreateWindow(width, height, title, 0, 0);
        if ( _windowContext == 0 ) {
            Log.Fail("Failed to create the GLFW window");
            return -1;
        }

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(_windowContext, pWidth, pHeight);

            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    _windowContext,
                    (videoMode.width() - pWidth.get(0)) / 2,
                    (videoMode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(_windowContext);
        glfwSwapInterval(1);

        glfwShowWindow(_windowContext);

        return _windowContext;
    }

    public static long InitializeNanoVG() {
        long _vgContext = nvgCreate(0);
        if (_vgContext == 0) {
            Log.Error("Could not init nanovg.");
            return -1;
        }
        return _vgContext;
    }

    public static void ShutdownNanoVG() {

    }

    public static void ShutDownOpenGL() {

    }

    public static void ShutDownGlfw() {

    }
}
