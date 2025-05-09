
package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ColorFieldPanel extends JPanel {
    private final JButton colorButton;
    private Color selectedColor;

    public ColorFieldPanel(Color initial, String label, Runnable onChange) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        selectedColor = initial;
        colorButton = new JButton();
        colorButton.setBackground(initial);
        colorButton.setOpaque(true);
        colorButton.setPreferredSize(new Dimension(40, 20));

        colorButton.addActionListener((ActionEvent e) -> {
            Color c = JColorChooser.showDialog(this, "Pick " + label, selectedColor);
            if (c != null) {
                selectedColor = c;
                colorButton.setBackground(c);
                onChange.run();
            }
        });

        add(new JLabel(label), BorderLayout.WEST);
        add(colorButton, BorderLayout.EAST);
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public static String toARGBHex(Color c, boolean alphaFirst) {
        if (alphaFirst) {
            return String.format("%02x%02x%02x%02x", c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
        } else {
            return String.format("%02x%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        }
    }

    public static String toRGBHex(Color c) {
        return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
