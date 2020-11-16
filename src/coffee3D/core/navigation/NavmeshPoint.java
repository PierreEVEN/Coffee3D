package coffee3D.core.navigation;

import org.joml.Vector2i;

import java.io.Serializable;

public class NavmeshPoint implements Serializable {
    private static final long serialVersionUID = 6011416635037746495L;
    public boolean isNavigable = false;
    public final Vector2i location = new Vector2i();
}
