
package src;

import javax.swing.*;
import java.io.File;


public class ImageEntry {
    public File imageFile;
    public int waveType;  // 0-30
    public JComboBox<WaveType> waveSelector;

    public ImageEntry(File imageFile, int waveType, JComboBox<WaveType> waveSelector) {
        this.imageFile = imageFile;
        this.waveType = waveType;
        this.waveSelector = waveSelector;
    }

    @Override
    public String toString() {
        return imageFile.getName();
    }
}
