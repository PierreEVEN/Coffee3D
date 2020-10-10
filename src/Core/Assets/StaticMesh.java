package Core.Assets;

import Core.Factories.MeshFactory;
import Core.Renderer.Scene.Scene;
import Core.Resources.GraphicResource;
import Core.Resources.MaterialResource;
import Core.Resources.MeshResource;

/**
 * NIY
 */
public class StaticMesh extends Asset {
    private transient MeshResource[] _sections;
    private transient MaterialResource[] _materials;

    public StaticMesh(String name, String filePath) {
        super(name, filePath);
    }

    @Override
    public void load() {
        _sections = MeshFactory.FromFile(getName(), getFilepath());
    }

    public void draw(Scene context) {
        //Draw attached sections with corresponding material
        for(int i = 0; i < _sections.length; ++i) {
            if (_materials != null && _materials.length >= i) _materials[i].use(context);
            _sections[i].use(context);
        }
    }

    @Override
    public void use(Scene context) {
        for(MeshResource section : _sections) {
            section.use(context);
        }
    }
}
