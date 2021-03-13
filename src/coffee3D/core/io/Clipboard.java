package coffee3D.core.io;

public class Clipboard {

    private static byte[] _clipboard;

    public static void Write(byte[] data) {
        _clipboard = data;
    }

    public static byte[] Get() { return _clipboard; }

}
