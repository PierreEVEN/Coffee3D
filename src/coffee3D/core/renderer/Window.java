package coffee3D.core.renderer;

import coffee3D.core.assets.AssetManager;
import coffee3D.core.IEngineModule;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.types.TypeHelper;
import coffee3D.core.ui.hud.HudUtils;
import coffee3D.core.ui.imgui.ImGuiImplementation;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.propertyHelper.FieldWriter;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

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
        AssetManager.LoadAssetLibrary(EngineSettings.ENGINE_ASSET_PATH);
        AssetManager.LoadAssetLibrary(EngineSettings.GAME_ASSET_PATH);
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

    /**
     * Poll glfw events,
     * then draw window content
     */
    private void renderLoop() {
        while (!glfwWindowShouldClose(_glfwWindowHandle)) {
            // Poll inputs
            glfwPollEvents();

            if (_engineModule.GetController() == null) Log.Fail("Internal error : Controller is null");
            _engineModule.GetController().update();

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



    private static final double[] _cursorPosX = {0}, _cursorPosY = {0};

    public double getCursorPosX() {
        glfwGetCursorPos(getGlfwWindowHandle(), _cursorPosX, _cursorPosY);
        return _cursorPosX[0];
    }
    public double getCursorPosY() {
        glfwGetCursorPos(getGlfwWindowHandle(), _cursorPosX, _cursorPosY);
        return _cursorPosY[0];
    }
}
