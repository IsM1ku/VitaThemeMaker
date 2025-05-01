package src;

public class Utils {
    public static String sanitizeFolderName(String name) {
        return name.replaceAll("[\\/:*?\"<>|]", "_");
    }
}
