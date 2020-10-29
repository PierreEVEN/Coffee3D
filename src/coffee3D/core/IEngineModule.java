package coffee3D.core;

import coffee3D.core.controller.IGameController;

public interface IEngineModule {
    void LoadResources();
    IGameController GetController();
    void PreInitialize();
    void DrawScene();
    void DrawUI();
    void DrawHUD();
}
