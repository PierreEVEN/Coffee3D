package Core;

public interface IRenderModule {
    void LoadResources();

    void BuildLevel();

    void DrawUI(long nvg);
}
