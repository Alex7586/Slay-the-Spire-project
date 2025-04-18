public class Utility {
    public static <T> T nvl(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }
}
