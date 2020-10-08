package Core.Resources;

import Core.Renderer.Scene.Scene;

/**
 * NIY
 */
public class StaticMesh extends GraphicResource {
    private MeshResource[] _sections;
    private MaterialResource[] _materials;

    public StaticMesh(String resourceName, MeshResource[] sections, MaterialResource[] materials) {
        super(resourceName);
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

    @Override
    public void load() {
        for(MeshResource section : _sections) {
            section.load();
        }
    }

    @Override
    public void unload() {
        for(MeshResource section : _sections) {
            section.unload();
        }
        _sections = null;
    }

    @Override
    public void use(Scene context) {
        for(MeshResource section : _sections) {
            section.use(context);
        }
    }
}
