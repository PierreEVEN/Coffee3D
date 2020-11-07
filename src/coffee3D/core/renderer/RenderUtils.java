package coffee3D.core.renderer;

import coffee3D.core.assets.types.Material;
import coffee3D.core.assets.types.MaterialInterface;
import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
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


    private static MaterialInterface debugMaterial;
    private static MaterialInterface postProcessMaterial;
    private static MaterialInterface[] shadowDrawList;
    private static MaterialInterface[] pickDrawList;
    private static MaterialInterface[] outlineDrawList;

    public static RenderMode RENDER_MODE = RenderMode.Color;

    public static MaterialInterface[] getShadowDrawList() {
        if (shadowDrawList == null) {
            shadowDrawList = new Material[] { new Material("ShadowMaterial", EngineSettings.SHADOW_MATERIAL_PATH, null, null) };
            if (shadowDrawList == null) {
                Log.Fail("failed to shadow material from : " + EngineSettings.SHADOW_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return shadowDrawList;
    }

    public static MaterialInterface getPostProcessMaterial() {
        if (postProcessMaterial == null) {
            postProcessMaterial = new Material("PostProcessMaterial", EngineSettings.POST_PROCESS_MATERIAL, null, null);
            if (postProcessMaterial == null) {
                Log.Fail("failed to load post process material from : " + EngineSettings.POST_PROCESS_MATERIAL);
            }
            RenderUtils.CheckGLErrors();
        }
        return postProcessMaterial;
    }

    public static MaterialInterface getDebugMaterial() {
        if (debugMaterial == null) {
            debugMaterial = new Material("DebugMaterial", EngineSettings.DEBUG_MATERIAL_PATH, null, null);
            if (debugMaterial == null) {
                Log.Fail("failed to load debug material from : " + EngineSettings.DEBUG_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return debugMaterial;
    }

    public static MaterialInterface[] getPickMaterialDrawList() {
        if (pickDrawList == null) {
            pickDrawList = new Material[] { new Material("PickMaterial", EngineSettings.PICK_MATERIAL_PATH, null, null) };
            if (pickDrawList[0] == null) {
                Log.Fail("failed to load pick material from : " + EngineSettings.DEBUG_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return pickDrawList;
    }

    public static MaterialInterface[] getOutlineMaterialDrawList() {
        if (outlineDrawList == null) {
            outlineDrawList = new Material[] { new Material("OutlineMaterial", EngineSettings.OUTLINE_MATERIAL_PATH, null, null) };
            if (outlineDrawList[0] == null) {
                Log.Fail("failed to load pick material from : " + EngineSettings.OUTLINE_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return outlineDrawList;
    }

    public static void InitializeOpenGL() {
        createCapabilities();
    }

    public static void CheckGLErrors() {
        int errCode;
        while((errCode = glGetError()) != GL_NO_ERROR)
        {
            String errorCode;
            switch (errCode) {
                case GL_INVALID_ENUM : errorCode = "GL_INVALID_ENUM"; break;
                case GL_INVALID_VALUE : errorCode = "GL_INVALID_VALUE"; break;
                case GL_INVALID_OPERATION : errorCode = "GL_INVALID_OPERATION"; break;
                case GL_STACK_OVERFLOW : errorCode = "GL_STACK_OVERFLOW"; break;
                case GL_STACK_UNDERFLOW : errorCode = "GL_STACK_UNDERFLOW"; break;
                case GL_OUT_OF_MEMORY : errorCode = "GL_OUT_OF_MEMORY"; break;
                default : errorCode = "unknown : " + Integer.toHexString(errCode); break;
            }
            Log.Fail("GL error : " + errorCode);
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
