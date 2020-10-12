package Core.Renderer.Scene.Gamemode;

import Core.IO.Inputs.GlfwInputHandler;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;

public class DefaultGamemode extends IGamemodeBase {

    private IGameController _defaultController;

    public DefaultGamemode(Scene scene) {
        super(scene);
        _defaultController = new DefaultController(scene);
        GlfwInputHandler.AddListener(_defaultController);
    }

    @Override
    IGameController getController() {
        return _defaultController;
    }
}
