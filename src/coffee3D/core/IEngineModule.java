package coffee3D.core;

import coffee3D.core.controller.IGameController;

public abstract class IEngineModule {

    private static IEngineModule _moduleInstance = null;
    public static IEngineModule Get() { return _moduleInstance; }

    public IEngineModule() {
        _moduleInstance = this;
    }

    public abstract void LoadResources();
    public abstract IGameController GetController();
    public abstract void PreInitialize();
    public abstract void DrawScene();
    public abstract void DrawUI();
    public abstract void DrawHUD();
}
