package coffee3D.core.renderer.scene;

import coffee3D.core.io.settings.EngineSettings;

public class RenderSceneSettings {
    private final boolean _isFullScreen;
    private boolean _enableShadows;
    private final boolean _pickCursorEveryFrames;
    private boolean _enablePostProcess;
    private final boolean _enableStencil;
    private final boolean _isStatic;

    public static final RenderSceneSettings DEFAULT_FULL_SCREEN = new RenderSceneSettings(false, true);
    public static final RenderSceneSettings DEFAULT_WINDOWED = new RenderSceneSettings(false, false);
    public static final RenderSceneSettings DEFAULT_THUMBNAIL = new RenderSceneSettings(true, false, true, false, true, true);

    public void setShadows(boolean enable) {
        if (_isStatic) return;
        _enableShadows = enable;
    }

    public void set_enablePostProcess(boolean enable) {
        if (_isStatic) return;
        _enablePostProcess = enable;
    }


    public RenderSceneSettings(boolean isStatic, boolean fullScreen)
    {
        _isFullScreen = fullScreen;
        _enableShadows = true;
        _pickCursorEveryFrames = true;
        _enablePostProcess = true;
        _enableStencil = true;
        _isStatic = isStatic;
    }

    public RenderSceneSettings(boolean isStatic, boolean fullScreen, boolean shadows, boolean pickEveryFrames, boolean postProcess, boolean stencil)
    {
        _isFullScreen = fullScreen;
        _enableShadows = shadows;
        _pickCursorEveryFrames = pickEveryFrames;
        _enablePostProcess = postProcess;
        _enableStencil = stencil;
        _isStatic = isStatic;
    }


    public boolean isFullScreen() {
        return _isFullScreen;
    }

    public boolean enableShadows() { return _enableShadows && EngineSettings.Get().enableShadows; }

    public boolean enablePostProcess() { return _enablePostProcess && EngineSettings.Get().enablePostProcessing; }

    public boolean enableStencil() { return _enableStencil && EngineSettings.Get().enableStencilTest; }

    public boolean isPickCursorEveryFrames() { return hasPickBuffer() && _pickCursorEveryFrames && EngineSettings.Get().enablePicking; }

    public boolean hasFullScreenColorBuffer() {
        return _isFullScreen && hasColorBuffer();
    }

    public boolean hasPostProcessBuffer() { return !_isFullScreen && !(!_enablePostProcess && _isStatic); }

    public boolean hasStencilBuffer() { return !(!_enableStencil && _isStatic); }

    public boolean hasShadowBuffer() {
        return !(!_enableShadows && _isStatic);
    }

    public boolean hasPickBuffer() { return true; }

    public boolean hasColorBuffer() { return !_isFullScreen || (enablePostProcess()); }
}
