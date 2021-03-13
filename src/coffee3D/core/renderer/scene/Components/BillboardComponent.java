package coffee3D.core.renderer.scene.Components;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.assets.types.Texture2D;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.types.SphereBound;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BillboardComponent extends SceneComponent {

    private static final long serialVersionUID = -8089244852841439545L;

    protected AssetReference<Texture2D> _texture;


    public BillboardComponent(Texture2D texture, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        _texture = new AssetReference(Texture2D.class, texture);
    }

    @Override
    protected void draw(Scene context) {
        if (_texture.get() != null) {
            drawBillboard(context, _texture.get(), getRelativeScale().x);
        }
        else {
            super.draw(context);
        }
    }

    @Override
    public SphereBound getBound() {
        super.getBound().radius = getRelativeScale().x;
        return super.getBound();
    }
}
