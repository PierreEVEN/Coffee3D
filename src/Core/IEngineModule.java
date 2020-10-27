package Core;

import Core.Controller.IGameController;

public interface IEngineModule {
    void LoadResources();
    IGameController GetController();
    void PreInitialize();
    void DrawScene();
    void DrawUI();
    void DrawHUD();
}
