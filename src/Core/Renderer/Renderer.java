/*
package Core.Renderer;

import Core.IO.Log;
import Core.Renderer.Scene.FrameScene;
import Core.Resources.ResourceManager;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {

    private static Renderer _renderer;



    public static Renderer Get() {
        if (_renderer == null) _renderer = new Renderer();
        return _renderer;
    }

    private long _glfwContext;
    private FrameScene _frameScene;
    private long _nvgContext;

    private Renderer() {

        Log.Display("Create renderer");

        _glfwContext = RenderUtils.InitializeGlfw(800, 600, "Coffee3D Engine");
        RenderUtils.InitializeOpenGL(new Vector4f(.5f, .5f, .8f, 1.f));
        _nvgContext = RenderUtils.InitializeNanoVG();

    }

    public void ShutDown() {
        ResourceManager.ClearResources();
        RenderUtils.ShutdownNanoVG();
        RenderUtils.ShutDownOpenGL();
        RenderUtils.ShutDownGlfw();
    }

    public void run() {

        Log.Display("Start render loop");

        // cycle trough render loop until the game is stopped
        cycleRenderLoop();

        ShutDown();

        Log.Display("Renderer complete");
    }

    public void stop() {
        glfwSetWindowShouldClose(_glfwContext, true)    ;
    }

    private void cycleRenderLoop()
    {
        while (!glfwWindowShouldClose(_glfwContext)) {
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            _frameScene.RenderScene();

            glfwSwapBuffers(_glfwContext); // swap the color buffers
        }
    }

    private void initOpenGL() {

        Log.Display("Initialize OpenGL");

        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize Glfw
        if ( !glfwInit() ) Log.Fail("Failed to initialize glfw");

        // Set glfw parameters
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create glfw windows
        _glfwContext = glfwCreateWindow(800, 600, "Coffee3D", NULL, NULL);
        if ( _glfwContext == NULL ) Log.Fail("Failed to create the GLFW window");

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(_glfwContext, pWidth, pHeight);

            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    _glfwContext,
                    (videoMode.width() - pWidth.get(0)) / 2,
                    (videoMode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(_glfwContext);
        glfwSwapInterval(1);

        glfwShowWindow(_glfwContext);

        createCapabilities();

        setupCallbacks();

        // set clear color
        glClearColor(.5f, .5f, .8f, 0.0f);

        _frameScene = new FrameScene();

        _nvgContext = nvgCreate(0);
        if (_nvgContext == NULL) {
           Log.Error("Could not init nanovg.");
        }

    }

    private void setupCallbacks() {

        // keyboard callback
        glfwSetKeyCallback(_glfwContext, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // resize viewport
        glfwSetFramebufferSizeCallback(_glfwContext, (window, width, height) -> {
            glViewport(0,0,width,height);
        });
    }

    private void shutdownOpenGl() {

        _frameScene.close();
        _frameScene = null;

        Log.Display("Shutting down Glfw");


        glfwFreeCallbacks(_glfwContext);
        glfwDestroyWindow(_glfwContext);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

}
*/