package Core.Renderer;

import Core.IO.Log;
import Core.IRenderModule;
import Core.Renderer.Scene.Scene;
import Core.Resources.ResourceManager;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Window {

    private int _bfrWidth;
    private int _bfrHeight;
    private long _glfwWindowHandle;
    private String _windowTitle;
    private long _nanoVgHandle;
    private Scene _windowScene;
    private double _deltaTime;
    private double _lastFrameTime;
    private boolean _bDisplayCursor;

    private IRenderModule _renderModule;

    /**
     * Singleton used to reference the primary window
     */
    private static Window _primaryWindow;
    public static Window GetPrimaryWindow() {
        if (_primaryWindow == null) _primaryWindow = new Window();
        return _primaryWindow;
    }

    private Window() {
        _bfrWidth = 800;
        _bfrHeight = 600;
        _glfwWindowHandle = -1;
        _nanoVgHandle = -1;
        _windowTitle = "Coffee3D Engine";
    }

    /**
     * Initialize opengl context, and start render loop
     * (cleanup resource after execution)
     * @param renderModule
     */
    public void run(IRenderModule renderModule) {
        _renderModule = renderModule;

        _glfwWindowHandle = RenderUtils.InitializeGlfw(_bfrWidth, _bfrHeight, _windowTitle);
        RenderUtils.InitializeOpenGL(new Vector4f(.1f, .1f, .15f, 1.f));
        _nanoVgHandle = RenderUtils.InitializeNanoVG();

        _renderModule.LoadResources();
        _renderModule.BuildLevel();

        _windowScene = new Scene();

        showCursor(false);

        glfwSetFramebufferSizeCallback(_glfwWindowHandle, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                _bfrWidth = width;
                _bfrHeight = height;
                glViewport(0, 0, width, height);
            }
        });
        renderLoop();

        ResourceManager.ClearResources();
        RenderUtils.ShutdownNanoVG();
        RenderUtils.ShutDownOpenGL();
        RenderUtils.ShutDownGlfw();
    }

    private double lastTime = 0;

    /**
     * Poll glfw events,
     * then draw window content
     */
    private void renderLoop() {
        while (!glfwWindowShouldClose(_glfwWindowHandle)) {
            _deltaTime = GLFW.glfwGetTime() - _lastFrameTime;
            _lastFrameTime = GLFW.glfwGetTime();



            if (lastTime > 1) {
                lastTime = 0;
                Log.Display("delta : " + Window.GetPrimaryWindow().getDeltaTime());
            }
            lastTime += Window.GetPrimaryWindow().getDeltaTime();


            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            _windowScene.renderScene();

            _renderModule.DrawUI(_nanoVgHandle);

            glfwSwapBuffers(_glfwWindowHandle); // swap the color buffers

            System.gc();
        }
    }

    /**
     * Frame buffer width
     * @return pixels
     */
    public int getPixelWidth() { return _bfrWidth; }

    /**
     * Frame buffer height
     * @return pixels
     */
    public int getPixelHeight() { return _bfrHeight; }

    /**
     * close window
     */
    public void close() {
        glfwWindowShouldClose(_glfwWindowHandle);
    }

    public long getGlfwWindowHandle() { return _glfwWindowHandle; }

    public double getDeltaTime() { return _deltaTime; }

    public void switchCursor() {
        showCursor(!_bDisplayCursor);
    }

    public boolean captureMouse() { return !_bDisplayCursor; }

    public void setDrawMode(int drawMode) {
        glPolygonMode( GL_FRONT_AND_BACK, drawMode );
    }

    public void showCursor(boolean bshow) {
        _bDisplayCursor = bshow;
        if (bshow) {
            glfwSetInputMode(_glfwWindowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
        else {
            glfwSetInputMode(_glfwWindowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
    }
}
