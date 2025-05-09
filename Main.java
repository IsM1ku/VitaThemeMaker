package src;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting PS Vita Theme Maker...");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        SwingUtilities.invokeLater(() -> {
            try {
                new ThemeBuilder().createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
