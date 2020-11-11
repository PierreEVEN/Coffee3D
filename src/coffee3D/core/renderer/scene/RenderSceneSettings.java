package coffee3D.core.renderer.scene;

import coffee3D.core.io.settings.EngineSettings;

public class RenderSceneSettings {
    private final boolean _isFullScreen;
    private boolean _enableShadows;
    private boolean _pickCursorEveryFrames;
    private boolean _enablePostProcess;
    private boolean _enableStencil;
    private final boolean _isStatic;

    public static final RenderSceneSettings DEFAULT_FULL_SCREEN = new RenderSceneSettings(false, true);
    public static final RenderSceneSettings DEFAULT_WINDOWED = new RenderSceneSettings(false, false);
    public static final RenderSceneSettings DEFAULT_THUMBNAIL = new RenderSceneSettings(true, false, true, true, true, true);

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

    public boolean enableShadows() {
        return _enableShadows && EngineSettings.ENABLE_SHADOWS;
    }

    public boolean enablePostProcess() {
        return _enablePostProcess && EngineSettings.ENABLE_POSTPROCESSING;
    }

    public boolean enableStencil() {
        return _enableStencil && EngineSettings.ENABLE_STENCIL_TEST;
    }

    public boolean isPickCursorEveryFrames() {
        return hasPickBuffer() && _pickCursorEveryFrames;
    }

    public boolean hasFullScreenColorBuffer() {
        return _isFullScreen && hasColorBuffer();
    }

    public boolean hasPostProcessBuffer() {
        return !_isFullScreen && EngineSettings.ENABLE_POSTPROCESSING && !(!_enablePostProcess && _isStatic);
    }

    public boolean hasStencilBuffer() {
        return EngineSettings.ENABLE_STENCIL_TEST && !(!_enableStencil && _isStatic);
    }

    public boolean hasShadowBuffer() {
        return EngineSettings.ENABLE_SHADOWS && !(!_enableShadows && _isStatic);
    }

    public boolean hasPickBuffer() {
        return EngineSettings.ENABLE_PICKING;
    }

    public boolean hasColorBuffer() {
        return !_isFullScreen || EngineSettings.ENABLE_POSTPROCESSING;
    }
}
