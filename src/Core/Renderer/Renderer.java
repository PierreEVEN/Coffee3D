package Core.Renderer;

import Core.IO.Log;
import Core.Renderer.Scene.FrameScene;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {

    private static Renderer _renderer;
    public static Renderer Get() {
        if (_renderer == null) _renderer = new Renderer();
        return _renderer;
    }

    private long _windowContext;
    private boolean _bShutDown;
    private FrameScene _frameScene;

    private Renderer() {

        Log.Display("Create renderer");


        _bShutDown = false;

        initOpenGL();
    }

    public void ShutDown() {

        shutdownOpenGl();
        _renderer = null;
    }

    public void run() {

        Log.Display("Start render loop");

        // cycle trough render loop until the game is stopped
        cycleRenderLoop();

        ShutDown();

        Log.Display("Renderer complete");
    }

    public void stop() {
        _bShutDown = true;
    }

    private void cycleRenderLoop()
    {
        while (!glfwWindowShouldClose(_windowContext)) {
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            _frameScene.RenderScene();

            glfwSwapBuffers(_windowContext); // swap the color buffers
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
        _windowContext = glfwCreateWindow(800, 600, "Coffee Engine", NULL, NULL);
        if ( _windowContext == NULL ) Log.Fail("Failed to create the GLFW window");

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

        GL.createCapabilities();

        setupCallbacks();

        // set clear color
        glClearColor(.5f, .5f, .8f, 0.0f);

        _frameScene = new FrameScene();
    }

    private void setupCallbacks() {

        // keyboard callback
        glfwSetKeyCallback(_windowContext, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // resize viewport
        glfwSetFramebufferSizeCallback(_windowContext, (window, width, height) -> {
            glViewport(0,0,width,height);
        });
    }

    private void shutdownOpenGl() {

        _frameScene.close();
        _frameScene = null;

        Log.Display("Shutting down Glfw");


        glfwFreeCallbacks(_windowContext);
        glfwDestroyWindow(_windowContext);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

}
