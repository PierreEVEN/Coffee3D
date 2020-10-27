package Core.Renderer;

import Core.Assets.Material;
import Core.IO.Inputs.GlfwInputHandler;
import Core.IO.LogOutput.Log;
import Core.IO.Settings.EngineSettings;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;


public class RenderUtils {


    private static Material debugMaterial;
    private static Material pickMaterial;

    public static int RENDER_MODE = GL_SELECT;

    public static Material getDebugMaterial() {
        if (debugMaterial == null) {
            debugMaterial = new Material("DebugMaterial", EngineSettings.DEBUG_MATERIAL_PATH, null);
            if (debugMaterial == null) {
                Log.Fail("failed to load debug material from : " + EngineSettings.DEBUG_MATERIAL_PATH);
            }
        }
        return debugMaterial;
    }

    public static Material getPickMaterial() {
        if (pickMaterial == null) {
            pickMaterial = new Material("PickMaterial", EngineSettings.PICK_MATERIAL_PATH, null);
            if (pickMaterial == null) {
                Log.Fail("failed to load pick material from : " + EngineSettings.DEBUG_MATERIAL_PATH);
            }
        }
        return pickMaterial;
    }

    public static void InitializeOpenGL() {
        createCapabilities();
    }

    public static void CheckGLErrors() {
        int errCode;
        while((errCode = glGetError()) != GL_NO_ERROR)
        {
            Log.Error("GL error : " + Integer.toHexString(errCode));
        }
    }

    public static long InitializeGlfw(Vector2i bfrSize, String title) {
        long _windowContext = -1;

        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize Glfw
        if ( !glfwInit() ) Log.Fail("Failed to initialize glfw");

        // Set glfw parameters
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, EngineSettings.MSAA_SAMPLES);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_DECORATED, EngineSettings.FULLSCREEN_MODE ? GLFW_FALSE : GLFW_TRUE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, EngineSettings.TRANSPARENT_FRAMEBUFFER ? GLFW_TRUE : GLFW_FALSE);

        GLFWVidMode.Buffer windowMode = glfwGetVideoModes(glfwGetPrimaryMonitor());
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < windowMode.capacity(); i++)
        {
            if (windowMode.get(i).width() > maxWidth)
                maxWidth = windowMode.get(i).width();
            if (windowMode.get(i).height() > maxHeight)
                maxHeight = windowMode.get(i).height();
        }
        boolean bFullScreen = false;
        if (bfrSize.x < 0 || bfrSize.y < 0) {
            bfrSize.x = maxWidth;
            bfrSize.y = maxHeight;
            bFullScreen = true;
        }

        // Create glfw windows
        _windowContext = glfwCreateWindow(bfrSize.x, bfrSize.y, title, 0, 0);
        if ( _windowContext == 0 ) Log.Fail("Failed to create the GLFW window");
        glfwMakeContextCurrent(_windowContext);

        if (bFullScreen) {
            glfwSetWindowMonitor(_windowContext, 0, 0, 0, maxWidth, maxHeight, GLFW_DONT_CARE);
            glfwSetWindowPos(_windowContext, 0, 0);
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

        // Enable double buffering
        glfwSwapInterval(EngineSettings.ENABLE_DOUBLE_BUFFERING ? 1 : 0);
        glfwShowWindow(_windowContext);

        // Create input handler
        GlfwInputHandler.Initialize(_windowContext);

        return _windowContext;
    }

    public static void ShutDownOpenGL() {
        GL.destroy();
    }

    public static void ShutDownGlfw(long context) {

        glfwFreeCallbacks(context);
        glfwDestroyWindow(context);
        glfwTerminate();
    }
}
