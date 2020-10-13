package Core.Renderer;

import Core.IO.Inputs.GlfwInputHandler;
import Core.IO.LogOutput.Log;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryStack.stackPush;


public class RenderUtils {

    public static void InitializeOpenGL() {
        createCapabilities();

        glEnable(GL_MULTISAMPLE);
    }

    public static long InitializeGlfw(int width, int height, String title) {
        long _windowContext = -1;

        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize Glfw
        if ( !glfwInit() ) Log.Fail("Failed to initialize glfw");

        // Set glfw parameters
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 16);

        // Create glfw windows
        _windowContext = glfwCreateWindow(width, height, title, 0, 0);
        if ( _windowContext == 0 ) Log.Fail("Failed to create the GLFW window");
        glfwMakeContextCurrent(_windowContext);

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
        glfwSwapInterval(0);
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
