package Core.Renderer;

import Core.IRenderModule;
import Core.Resources.ResourceManager;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Window {

    private int _bfrWidth;
    private int _bfrHeight;
    private long _glfwWindowHandle;
    private String _windowTitle;
    private long _nanoVgHandle;
    

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
        RenderUtils.InitializeOpenGL(new Vector4f(.5f, .5f, .8f, 1.f));
        _nanoVgHandle = RenderUtils.InitializeNanoVG();

        _renderModule.LoadResources();
        _renderModule.BuildLevel();

        renderLoop();

        ResourceManager.ClearResources();
        RenderUtils.ShutdownNanoVG();
        RenderUtils.ShutDownOpenGL();
        RenderUtils.ShutDownGlfw();
    }

    /**
     * Poll glfw events,
     * then draw window content
     */
    private void renderLoop() {
        while (!glfwWindowShouldClose(_glfwWindowHandle)) {

            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer



            _renderModule.DrawUI(_nanoVgHandle);

            glfwSwapBuffers(_glfwWindowHandle); // swap the color buffers
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
}
