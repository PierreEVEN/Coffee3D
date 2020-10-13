package Core.Renderer;

import Core.IEngineModule;
import Core.IO.LogOutput.Log;
import Core.Resources.ResourceManager;
import Core.UI.ImGuiImpl.ImGuiImplementation;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Window {

    private int _bfrWidth;
    private int _bfrHeight;
    private long _glfwWindowHandle;
    private String _windowTitle;
    private double _deltaTime;
    private double _lastFrameTime;
    private boolean _bDisplayCursor;
    private IEngineModule _engineModule;

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
        _windowTitle = "Coffee3D";
    }

    /**
     * Initialize opengl context, and start render loop
     * (cleanup resource after execution)
     * @param engineModule
     */
    public void run(IEngineModule engineModule) {
        _engineModule = engineModule;

        Log.Display("initialize glfw");
        _glfwWindowHandle = RenderUtils.InitializeGlfw(_bfrWidth, _bfrHeight, _windowTitle);
        Log.Display("initialize openGL");
        RenderUtils.InitializeOpenGL();
        Log.Display("initialize imGui");
        ImGuiImplementation.Get().init(_glfwWindowHandle);
        showCursor(true);

        Log.Display("load resources");
        _engineModule.LoadResources();

        Log.Display("build level");
        _engineModule.PreInitialize();

        glfwSetFramebufferSizeCallback(_glfwWindowHandle, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                _bfrWidth = width;
                _bfrHeight = height;
                glViewport(0, 0, width, height);
            }
        });


        Log.Display("Start render loop");
        renderLoop();

        Log.Display("Clear resources");
        ResourceManager.ClearResources();
        Log.Display("Clear imGui resources");
        ImGuiImplementation.Get().shutDown();
        Log.Display("Shutting down opengl");
        RenderUtils.ShutDownOpenGL();
        Log.Display("Shutting down glfw");
        RenderUtils.ShutDownGlfw(_glfwWindowHandle);
        Log.Display("done");
    }

    /**
     * Poll glfw events,
     * then draw window content
     */
    private void renderLoop() {
        while (!glfwWindowShouldClose(_glfwWindowHandle)) {

            // Update delta time
            updateDeltaTime();

            // Poll inputs
            glfwPollEvents();

            // Clear background buffer
            glClearColor(0,0,0,1);
            glClear(GL_COLOR_BUFFER_BIT);

            glViewport(0, 0, getPixelWidth(), getPixelHeight());

            _engineModule.DrawScene();
            initUI();
            _engineModule.DrawUI();
            SubWindow.DrawWindows();
            ImGui.render();
            ImGuiImplementation.Get().render();

            glfwSwapBuffers(_glfwWindowHandle);

            // Flush gc to avoid garbage accumulation.
            System.gc();
        }
    }


    private void initUI() {
        IntBuffer winWidth = BufferUtils.createIntBuffer(1);
        IntBuffer winHeight = BufferUtils.createIntBuffer(1);
        IntBuffer fbWidth = BufferUtils.createIntBuffer(1);
        IntBuffer fbHeight = BufferUtils.createIntBuffer(1);
        DoubleBuffer mousePosX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer mousePosY = BufferUtils.createDoubleBuffer(1);

        glfwGetWindowSize(_glfwWindowHandle, winWidth, winHeight);
        glfwGetFramebufferSize(_glfwWindowHandle, fbWidth, fbHeight);
        glfwGetCursorPos(_glfwWindowHandle, mousePosX, mousePosY);

        final ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(winWidth.get(0), winHeight.get(0));
        io.setDisplayFramebufferScale((float) fbWidth.get(0) / winWidth.get(0), (float) fbHeight.get(0) / winHeight.get(0));
        io.setMousePos((float) mousePosX.get(0), (float) mousePosY.get(0));
        io.setDeltaTime((float) _deltaTime);

        ImGui.newFrame();
    }

    private void updateDeltaTime() {
        _deltaTime = GLFW.glfwGetTime() - _lastFrameTime;
        _lastFrameTime = GLFW.glfwGetTime();
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
        glfwSetWindowShouldClose(_glfwWindowHandle, true);
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
