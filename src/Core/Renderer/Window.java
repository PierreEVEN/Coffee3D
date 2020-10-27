package Core.Renderer;

import Core.Assets.AssetManager;
import Core.IEngineModule;
import Core.IO.LogOutput.Log;
import Core.IO.Settings.EngineSettings;
import Core.Resources.ResourceManager;
import Core.Types.TypeHelper;
import Core.UI.HUD.HudUtils;
import Core.UI.ImGuiImpl.ImGuiImplementation;
import Core.UI.PropertyHelper.FieldWriter;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Window {

    private Vector2i _bfrSize;
    private long _glfwWindowHandle;
    private double _deltaTime;
    private double _lastFrameTime;
    private boolean _bDisplayCursor;
    private IEngineModule _engineModule;
    private int _drawMode;
    private Vector4f _backgroundColor;

    /**
     * Singleton used to reference the primary window
     */
    private static Window _primaryWindow;
    public static Window GetPrimaryWindow() {
        if (_primaryWindow == null) _primaryWindow = new Window();
        return _primaryWindow;
    }

    private Window() {
        _bfrSize = new Vector2i(-1, -1);
        _drawMode = GL_FILL;
        _backgroundColor = new Vector4f(0,0,0,0);
    }

    /**
     * Initialize opengl context, and start render loop
     * (cleanup resource after execution)
     * @param engineModule source module
     */
    public void run(IEngineModule engineModule) {
        _engineModule = engineModule;

        FieldWriter.RegisterPrimitiveWriters();

        Log.Display("initialize glfw");
        _glfwWindowHandle = RenderUtils.InitializeGlfw(_bfrSize, "Coffee3D");
        Log.Display("initialize openGL");
        RenderUtils.InitializeOpenGL();
        showCursor(true);
        Log.Display("pre-initialize imGui");
        ImGuiImplementation.Get().preInit(_glfwWindowHandle);

        Log.Display("load resources");
        AssetManager.LoadAssetLibrary(EngineSettings.DEFAULT_ASSET_PATH);
        _engineModule.LoadResources();

        Log.Display("initialize imGui");
        ImGuiImplementation.Get().init(_glfwWindowHandle);

        Log.Display("build level");
        _engineModule.PreInitialize();

        RenderUtils.CheckGLErrors();

        glfwSetFramebufferSizeCallback(_glfwWindowHandle, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                _bfrSize.x = width;
                _bfrSize.y = height;
                glViewport(0, 0, width, height);

                drawFrame();
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

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(_glfwWindowHandle, title);
    }

    public void setBackgroundColor(Vector4f color) {
        _backgroundColor = color;
    }

    /**
     * Poll glfw events,
     * then draw window content
     */
    private void renderLoop() {
        while (!glfwWindowShouldClose(_glfwWindowHandle)) {
            // Poll inputs
            glfwPollEvents();

            // Draw frame
            drawFrame();
        }
    }


    private void drawFrame() {

        TypeHelper.nextFrame();

        // Update delta time
        updateDeltaTime();

        // Clear background buffer
        glClearColor(0,0,0,0);

        glPolygonMode( GL_FRONT_AND_BACK, _drawMode);
        _engineModule.DrawScene();
        glPolygonMode( GL_FRONT_AND_BACK, GL_FILL);

        initUI();
        HudUtils.ResetCounters();
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(Window.GetPrimaryWindow().getPixelWidth(), Window.GetPrimaryWindow().getPixelHeight());
        if (ImGui.begin("HUD Window", ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoInputs | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBackground)) {
            _engineModule.DrawHUD();
        }
        ImGui.end();

        _engineModule.DrawUI();
        SubWindow.DrawWindows();
        ImGui.render();
        ImGuiImplementation.Get().render();

        glfwSwapBuffers(_glfwWindowHandle);
        RenderUtils.CheckGLErrors();
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
    public int getPixelWidth() { return _bfrSize.x; }

    /**
     * Frame buffer height
     * @return pixels
     */
    public int getPixelHeight() { return _bfrSize.y; }

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
        _drawMode = drawMode;
    }

    public void showCursor(boolean bShow) {
        _bDisplayCursor = bShow;
        if (bShow) {
            glfwSetInputMode(_glfwWindowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
        else {
            glfwSetInputMode(_glfwWindowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
    }
}
