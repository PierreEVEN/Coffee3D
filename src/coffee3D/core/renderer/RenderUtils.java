package coffee3D.core.renderer;

import coffee3D.core.assets.types.Material;
import coffee3D.core.assets.types.MaterialInterface;
import coffee3D.core.io.inputs.GlfwInputHandler;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
import imgui.type.ImBoolean;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL31C.GL_UNIFORM_BUFFER;
import static org.lwjgl.system.MemoryStack.stackPush;


public class RenderUtils {


    private static MaterialInterface billboardPickMaterial;
    private static MaterialInterface debugMaterial;
    private static MaterialInterface postProcessMaterial;
    private static MaterialInterface[] shadowDrawList;
    private static MaterialInterface[] pickDrawList;
    private static MaterialInterface[] outlineDrawList;

    public static RenderMode RENDER_MODE = RenderMode.Color;

    public static MaterialInterface[] getShadowDrawList() {
        if (shadowDrawList == null) {
            shadowDrawList = new Material[] { new Material("ShadowMaterial", AssetReferences.SHADOW_MATERIAL_PATH, null, null) };
            if (shadowDrawList == null) {
                Log.Fail("failed to shadow material from : " + AssetReferences.SHADOW_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return shadowDrawList;
    }

    public static MaterialInterface getPostProcessMaterial() {
        if (postProcessMaterial == null) {
            postProcessMaterial = new Material("PostProcessMaterial", AssetReferences.POST_PROCESS_MATERIAL, null, null);
            if (postProcessMaterial == null) {
                Log.Fail("failed to load post process material from : " + AssetReferences.POST_PROCESS_MATERIAL);
            }
            RenderUtils.CheckGLErrors();
        }
        return postProcessMaterial;
    }

    public static MaterialInterface getDebugMaterial() {
        if (debugMaterial == null) {
            debugMaterial = new Material("DebugMaterial", AssetReferences.DEBUG_MATERIAL_PATH, null, null);
            if (debugMaterial == null) {
                Log.Fail("failed to load debug material from : " + AssetReferences.DEBUG_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return debugMaterial;
    }

    public static boolean WITH_EDITOR = false;


    public static MaterialInterface getBillboardPickMaterial() {
        if (billboardPickMaterial == null) {
            billboardPickMaterial = new Material("BillboardPickMaterial", AssetReferences.BILLBOARD_PICK_MATERIAL_PATH, null, null);
            if (billboardPickMaterial == null) {
                Log.Fail("failed to load billboard pick material from : " + AssetReferences.BILLBOARD_PICK_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return billboardPickMaterial;
    }

    public static MaterialInterface[] getPickMaterialDrawList() {
        if (pickDrawList == null) {
            pickDrawList = new Material[] { new Material("PickMaterial", AssetReferences.PICK_MATERIAL_PATH, null, null) };
            if (pickDrawList[0] == null) {
                Log.Fail("failed to load pick material from : " + AssetReferences.DEBUG_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return pickDrawList;
    }

    public static MaterialInterface[] getOutlineMaterialDrawList() {
        if (outlineDrawList == null) {
            outlineDrawList = new Material[] { new Material("OutlineMaterial", AssetReferences.OUTLINE_MATERIAL_PATH, null, null) };
            if (outlineDrawList[0] == null) {
                Log.Fail("failed to load pick material from : " + AssetReferences.OUTLINE_MATERIAL_PATH);
            }
            RenderUtils.CheckGLErrors();
        }
        return outlineDrawList;
    }

    public static imgui.type.ImBoolean DRAW_DEBUG_BOUNDS = new ImBoolean(false);

    public static void InitializeOpenGL() {
        createCapabilities();
    }

    private static int LastActivatedTexture = -1;
    public static void ActivateTexture(int index) {
        if (LastActivatedTexture != index) {
            LastActivatedTexture = index;
            glActiveTexture(GL_TEXTURE0 + index);
        }
    }
    private static int LastUniformBuffer = -1;
    public static void BindUniformBuffer(int index) {
        if (LastUniformBuffer != index) {
            LastUniformBuffer = index;
            glBindBuffer(GL_UNIFORM_BUFFER, index);
        }
    }
    private static int LastEbo = -1;
    public static void BindEbo(int ebo) {
        if (LastEbo != ebo) {
            LastEbo = ebo;
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        }
    }
    private static int LastVao = -1;
    public static void BindVao(int vao) {
        if (LastVao != vao) {
            LastVao = vao;
            GL30.glBindVertexArray(vao);
        }
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
                case 0x506 : errorCode = "GL_INVALID_FRAMEBUFFER_OPERATION"; break;
                case 0x0507 : errorCode = "GL_CONTEXT_LOST"; break;
                case 0x8031 : errorCode = "GL_TABLE_TOO_LARGE1"; break;
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
        glfwWindowHint(GLFW_SAMPLES, EngineSettings.Get().msaaSamples);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_DECORATED, EngineSettings.Get().fullscreen ? GLFW_FALSE : GLFW_TRUE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, EngineSettings.Get().transparentFramebuffer ? GLFW_TRUE : GLFW_FALSE);

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
        glfwSwapInterval(EngineSettings.Get().doubleBuffering ? 1 : 0);
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
