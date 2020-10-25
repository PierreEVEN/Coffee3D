package Core.UI.PropertyHelper;

import java.io.Serializable;

public abstract class SerializableData implements Serializable {
    private static final long serialVersionUID = -5178316587349921157L;

    private boolean _bIsDirty = false;

    public boolean isDirty() {
        return _bIsDirty;
    }

    public void edit() {
        _bIsDirty = true;
    }
    
    public void save() {
        _bIsDirty = false;
    }
}
