package coffee3D.core.navigation;

import org.joml.Vector2i;

import java.io.Serializable;

public class NavmeshPoint implements Serializable {
    private static final long serialVersionUID = 6011416635037746495L;
    public boolean isNavigable = false;

    public final Vector2i location = new Vector2i();
    protected transient boolean _isExplored;
    protected transient int _FCost, _GCost, _HCost; // GCost is the distance to the end
    protected transient boolean _isUsable = false;
    protected transient NavmeshPoint _source;

    public void reset(boolean isUsable, int inGCost) {
        _GCost = inGCost;
        _isUsable = isUsable;
        _isExplored = false;
    }

}
