package Core.Resources;

import Core.Renderer.Scene.Scene;

public class StaticMesh {
    private MeshResource[] _sections;
    private MaterialResource[] _materials;

    public StaticMesh(MeshResource[] sections, MaterialResource[] materials) {
        _sections = sections;
        _materials = materials;
    }

    public void draw(Scene context) {
        //Draw attached sections with corresponding material
        for(int i = 0; i < _sections.length; ++i) {
            if (_materials.length >= i) _materials[i].use(context);
            _sections[i].use(context);
        }
    }
}
