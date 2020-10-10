package Core.Renderer.Scene.Gamemode;

import Core.Renderer.Scene.Scene;

public abstract class IGamemodeBase {

    private Scene _scene;

    abstract IGameController getController();

    protected IGamemodeBase(Scene scene) {
        _scene = scene;
    }

    public void update(Scene context) {
        getController().update(context);
    }

    protected Scene getScene() { return _scene; }

}
