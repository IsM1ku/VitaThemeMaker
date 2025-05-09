
package src;

import javax.swing.*;
import java.awt.*;

public class WaveSelector {
    public static JComboBox<WaveType> createWaveBox() {
        JComboBox<WaveType> box = new JComboBox<>();
        for (int i = 0; i <= 30; i++) {
            ImageIcon icon = new ImageIcon("resources/waves/wave_" + String.format("%02d", i) + ".png");
            box.addItem(new WaveType(i, icon));
        }

        box.setRenderer(new ListCellRenderer<>() {
            private final JLabel label = new JLabel();

            @Override
            public Component getListCellRendererComponent(JList<? extends WaveType> list, WaveType value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                label.setIcon(value.icon);
                label.setText("Wave " + value.id);
                label.setOpaque(true);
                label.setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
                return label;
            }
        });

        return box;
    }
}

class WaveType {
    public int id;
    public ImageIcon icon;

    public WaveType(int id, ImageIcon icon) {
        this.id = id;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Wave " + id;
    }
}
