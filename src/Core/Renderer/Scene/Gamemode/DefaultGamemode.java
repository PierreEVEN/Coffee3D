package Core.Renderer.Scene.Gamemode;

import Core.Renderer.Scene.Scene;

public class DefaultGamemode extends IGamemodeBase {

    private IGameController _defaultController;

    public DefaultGamemode(Scene scene) {
        super(scene);
        _defaultController = new DefaultController(scene);
        //_defaultController.enable();
    }

    @Override
    IGameController getController() {
        return _defaultController;
    }
}
