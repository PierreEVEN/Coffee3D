package coffee3D.core.renderer;

import coffee3D.core.Engine;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.IEngineModule;
import coffee3D.core.assets.types.Font;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.io.settings.GameSettings;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.resources.factories.FontFactory;
import coffee3D.core.types.TypeHelper;
import coffee3D.core.ui.hud.HudUtils;
import coffee3D.core.ui.imgui.ImGuiImplementation;
import coffee3D.core.ui.subWindows.SubWindow;
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
    private double _frameDuration;
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

        Log.Display("~ Loading Coffee3D v" + Engine.GetEngineVersion() + " ~");

        GameSettings.ScanSettings();

        _engineModule = engineModule;

        Log.Display("initialize glfw");
        _glfwWindowHandle = RenderUtils.InitializeGlfw(_bfrSize, "Coffee3D");
        Log.Display("initialize openGL");
        RenderUtils.InitializeOpenGL();
        showCursor(true);
        Log.Display("pre-initialize imGui");
        ImGuiImplementation.Get().preInit(_glfwWindowHandle);

        Log.Display("load resources");
        AssetManager.LoadAssetLibrary(EngineSettings.Get().engineAssetsPath);
        AssetManager.LoadAssetLibrary(EngineSettings.Get().gameAssetsPath);
        _engineModule.LoadResources();

        Log.Display("initialize imGui");
        FontFactory.FlushDelayedFonts();
        ImGuiImplementation.Get().init(_glfwWindowHandle);
        Font defaultFont = AssetManager.FindAsset(_engineModule.GetDefaultFontName());
        if (defaultFont != null) defaultFont.setDefault();

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

        //Cleanup memory
        TypeHelper.ClearMemory();
        System.gc();


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
            //Handle inputs
            if (_engineModule.GetController() == null) Log.Fail("Internal error : Controller is null");
            _engineModule.GetController().update();

            glfwPollEvents();

            //AudioListener.Get().tick();

            // Draw frame
            drawFrame();
        }
    }

    private void drawFrame() {
        TypeHelper.nextFrame();

        // Update delta time
        updateDeltaTime();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        _engineModule.DrawScene();

        initUI();

        HudUtils.ResetCounters();
        ImGui.setNextWindowPos(-4, -4);
        ImGui.setNextWindowSize(Window.GetPrimaryWindow().getPixelWidth() + 8, Window.GetPrimaryWindow().getPixelHeight() + 8);
        if (ImGui.begin("HUD Window", ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoInputs | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBackground)) {
            _engineModule.DrawHUD();
        }
        ImGui.end();

        _engineModule.DrawUI();
        SubWindow.DrawWindows();
        ImGui.render();
        ImGuiImplementation.Get().render();

        _frameDuration = GLFW.glfwGetTime() - _lastFrameTime;

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
        if (_deltaTime > 1.0 / 10.0) {
            Log.Warning("Game froze for " + _deltaTime + "s");
            _deltaTime = 1.0 / 10.0;
        }
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

    public double getFrameDuration() { return _frameDuration; }

    public void switchCursor() {
        showCursor(!_bDisplayCursor);
    }

    public boolean captureMouse() { return !_bDisplayCursor; }

    public void setDrawMode(int drawMode) {
        _drawMode = drawMode;
    }

    public int getDrawMode() { return _drawMode; }

    public void showCursor(boolean bShow) {
        showCursor(bShow, false);

    }
    public void showCursor(boolean bShow, boolean bMoveCursor) {
        _bDisplayCursor = bShow;
        if (bShow) {
            double pX = getCursorPosX() % _bfrSize.x;
            double pY = getCursorPosY() % _bfrSize.y;
            glfwSetInputMode(_glfwWindowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            if (bMoveCursor) glfwSetCursorPos(getGlfwWindowHandle(), pX, pY);
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
