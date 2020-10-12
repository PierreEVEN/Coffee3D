package Core.Renderer;

import Core.IO.Inputs.GlfwInputHandler;
import Core.IO.Log;
import Core.IEngineModule;
import Core.Renderer.Scene.Scene;
import Core.Resources.ResourceManager;
import Core.UI.ImGuiImplementation;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;
import org.joml.Vector4f;
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
    private Scene _windowScene;
    private double _deltaTime;
    private double _lastFrameTime;
    private boolean _bDisplayCursor;

    private IEngineModule _renderModule;

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
        _windowTitle = "Coffee3D Engine";
    }

    /**
     * Initialize opengl context, and start render loop
     * (cleanup resource after execution)
     * @param renderModule
     */
    public void run(IEngineModule renderModule) {
        _renderModule = renderModule;

        _glfwWindowHandle = RenderUtils.InitializeGlfw(_bfrWidth, _bfrHeight, _windowTitle);
        RenderUtils.InitializeOpenGL(new Vector4f(.5f, .7f, .9f, 1.f));
        RenderUtils.InitializeImgui(_glfwWindowHandle);

        _renderModule.LoadResources();
        _renderModule.BuildLevel();

        _windowScene = new Scene();

        showCursor(true);

        glfwSetFramebufferSizeCallback(_glfwWindowHandle, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                _bfrWidth = width;
                _bfrHeight = height;
                glViewport(0, 0, width, height);
            }
        });

        GlfwInputHandler.Initialize(_glfwWindowHandle);

        renderLoop();

        ResourceManager.ClearResources();
        RenderUtils.ShutdownImgui();
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

            glEnable(GL_STENCIL_TEST);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_FRONT);
            glFrontFace(GL_CW);

            if (lastTime > 1) {
                lastTime = 0;
                Log.Display("delta : " + Window.GetPrimaryWindow().getDeltaTime());
            }
            lastTime += Window.GetPrimaryWindow().getDeltaTime();


            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT); // clear the framebuffer

            glBindBuffer(GL_FRAMEBUFFER, _windowScene.getFramebuffer().getBufferId());
            _windowScene.renderScene();

            glBindBuffer(GL_FRAMEBUFFER, 0);

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


            int dockspaceID = 0;
            if (ImGui.begin("Master Window"/*, nullptr, ImGuiWindowFlags_MenuBar*/))
            {
                ImGui.textUnformatted("DockSpace below");

                // Declare Central dockspace
                dockspaceID = ImGui.getID("HUB_DockSpace");
                ImGui.dockSpace(dockspaceID, 0.f, 0.f, ImGuiDockNodeFlags.None | ImGuiDockNodeFlags.PassthruCentralNode/*|ImGuiDockNodeFlags_NoResize*/);
            }
            ImGui.end();

            ImGui.setNextWindowDockID(dockspaceID , ImGuiCond.FirstUseEver);
            if (ImGui.begin("Dockable Window"))
            {
                ImGui.image(_windowScene.getFramebuffer().getColorBuffer(), _windowScene.getFramebuffer().getWidth(), _windowScene.getFramebuffer().getHeight());

                ImGui.textUnformatted("Test");
            }
            ImGui.end();



            ImGui.showDemoWindow();

            ImGui.render();
            ImGuiImplementation.Get().render();

            _renderModule.DrawUI(0);

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
