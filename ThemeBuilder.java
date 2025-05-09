package src;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;


public class ThemeBuilder {
    private JFrame frame;
    private JTextField themeNameField, authorField, versionField;
    private DefaultListModel<ImageEntry> imageListModel;
    private JPanel imagesPanel;
    private ColorFieldPanel barColorPanel, indicatorColorPanel;
    private ColorFieldPanel dateColorPanel, notifyFontColorPanel, notifyBgColorPanel, notifyBorderColorPanel;
    private JComboBox<String> dateLayoutBox;
	private File bgmWavFile = null;
    private File outputDir;
    private File startBgFile = null;
    private File previewThumbFile = null;
    private File previewPageFile = null;
    private File previewLockscreenFile = null;

    public void createAndShowGUI() {
        frame = new JFrame("PS Vita Theme Maker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imageListModel = new DefaultListModel<>();
        imagesPanel = new JPanel();
        imagesPanel.setLayout(new BoxLayout(imagesPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(imagesPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        imageListModel.addListDataListener(new javax.swing.event.ListDataListener() {
            private void refreshPanel() {
                imagesPanel.removeAll();
                for (int i = 0; i < imageListModel.size(); i++) {
                    ImageEntry entry = imageListModel.get(i);
                    JPanel entryPanel = new JPanel(new BorderLayout());
                    entryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    JLabel label = new JLabel(entry.imageFile.getName());
                    entryPanel.add(label, BorderLayout.CENTER);
                    entryPanel.add(entry.waveSelector, BorderLayout.EAST);
                    imagesPanel.add(entryPanel);
                }
                imagesPanel.revalidate();
                imagesPanel.repaint();
            }

            public void intervalAdded(javax.swing.event.ListDataEvent e) { refreshPanel(); }
            public void intervalRemoved(javax.swing.event.ListDataEvent e) { refreshPanel(); }
            public void contentsChanged(javax.swing.event.ListDataEvent e) { refreshPanel(); }
        });

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        themeNameField = new JTextField("My Theme");
        authorField = new JTextField("Author");
        versionField = new JTextField("01.00");

        dateLayoutBox = new JComboBox<>(new String[]{"LeftBottom", "LeftTop", "RightBottom"});

        barColorPanel = new ColorFieldPanel(Color.BLACK, "Bar Color", () -> {});
        indicatorColorPanel = new ColorFieldPanel(Color.WHITE, "Indicator Color", () -> {});
        dateColorPanel = new ColorFieldPanel(Color.WHITE, "Date Font Color", () -> {});
        notifyFontColorPanel = new ColorFieldPanel(Color.WHITE, "Notify Font Color", () -> {});
        notifyBgColorPanel = new ColorFieldPanel(Color.WHITE, "Notify BG Color", () -> {});
        notifyBorderColorPanel = new ColorFieldPanel(new Color(0xCC, 0xCC, 0xCC), "Notify Border Color", () -> {});

        form.add(new JLabel("Theme Name:")); form.add(themeNameField);
        form.add(new JLabel("Author:")); form.add(authorField);
        form.add(new JLabel("Version:")); form.add(versionField);
        form.add(new JLabel("Date Layout:")); form.add(dateLayoutBox);

        form.add(barColorPanel); form.add(indicatorColorPanel);
        form.add(dateColorPanel); form.add(notifyFontColorPanel);
        form.add(notifyBgColorPanel); form.add(notifyBorderColorPanel);

        JButton addImagesButton = new JButton("Add Images");
        addImagesButton.addActionListener(e -> {
            FileDialog dialog = new FileDialog(frame, "Select PNG Images", FileDialog.LOAD);
            dialog.setMultipleMode(true);
            dialog.setVisible(true);
            for (File f : dialog.getFiles()) {
                if (imageListModel.size() >= 10) break;
                JComboBox<WaveType> waveBox = WaveSelector.createWaveBox();
                waveBox.setSelectedIndex(11);
                imageListModel.addElement(new ImageEntry(f, 11, waveBox));
            }
        });

        JButton removeImageButton = new JButton("Remove Selected");
        removeImageButton.addActionListener(e -> {
            if (!imageListModel.isEmpty()) {
                imageListModel.remove(imageListModel.size() - 1);
            }
        });

        JButton buildButton = new JButton("Build Theme");
        buildButton.addActionListener(e -> buildTheme());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addImagesButton);
        buttonPanel.add(removeImageButton);
        buttonPanel.add(buildButton);

        new DropTarget(imagesPanel, new DropTargetAdapter() {
            @SuppressWarnings("unchecked")
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        if (imageListModel.size() >= 10) break;
                        JComboBox<WaveType> waveBox = WaveSelector.createWaveBox();
                        waveBox.setSelectedIndex(11);
                        imageListModel.addElement(new ImageEntry(file, 11, waveBox));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(form, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        JPanel filePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        filePanel.add(createInteractiveLabel("start_bg.png", "960x512", f -> startBgFile = f));
        filePanel.add(createInteractiveLabel("preview_thumbnail.png", "226x128", f -> previewThumbFile = f));
        filePanel.add(createInteractiveLabel("preview_page.png", "480x272", f -> previewPageFile = f));
        filePanel.add(createInteractiveLabel("preview_lockscreen.png", "480x272", f -> previewLockscreenFile = f));
        filePanel.add(createInteractiveLabel("BGM.wav", "Audio File", f -> bgmWavFile = f));
        frame.add(filePanel, BorderLayout.EAST);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JLabel createInteractiveLabel(String text, String description, java.util.function.Consumer<File> fileConsumer) {
        JLabel label = new JLabel("Drop " + text + " (" + description + ")", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(180, 40));
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FileDialog dialog = new FileDialog(frame, "Select " + text, FileDialog.LOAD);
                dialog.setVisible(true);
                File[] files = dialog.getFiles();
                if (files != null && files.length > 0) {
                    File selected = files[0];
                    fileConsumer.accept(selected);
                    label.setText(selected.getName());
                }
            }
        });

        try {
            new DropTarget(label, new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent evt) {
                    try {
                        evt.acceptDrop(DnDConstants.ACTION_COPY);
                        java.util.List<File> droppedFiles = (java.util.List<File>) evt.getTransferable()
                                .getTransferData(DataFlavor.javaFileListFlavor);
                        if (!droppedFiles.isEmpty()) {
                            File file = droppedFiles.get(0);
                            fileConsumer.accept(file);
                            label.setText(file.getName());
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Failed to load file: " + ex.getMessage());
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return label;
    }

   private void buildTheme() {
    JDialog progressDialog = new JDialog(frame, "Building Theme...", true);
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    panel.add(new JLabel("Please wait..."), BorderLayout.NORTH);
    JProgressBar bar = new JProgressBar();
    bar.setIndeterminate(true);
    panel.add(bar, BorderLayout.CENTER);
    progressDialog.add(panel);
    progressDialog.pack();
    progressDialog.setLocationRelativeTo(frame);

    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        protected Void doInBackground() {
            try {
                String themeName = themeNameField.getText().trim();
                outputDir = new File(System.getProperty("user.dir"), Utils.sanitizeFolderName(themeName));
                outputDir.mkdirs();

                List<String> bgNames = new ArrayList<>();
                List<Integer> waveTypes = new ArrayList<>();

                for (int i = 0; i < imageListModel.size(); i++) {
                    ImageEntry entry = imageListModel.get(i);
                    File input = entry.imageFile;
                    String base = String.format("bg_%02d", i + 1);
                    File output = new File(outputDir, base + ".png");
                    File thumb = new File(outputDir, base + "t.png");
                    ImageProcessor.convertToPng8(input, output);
                    ImageProcessor.createThumbnail(output, thumb);
                    bgNames.add(base + ".png");
                    WaveType wt = (WaveType) entry.waveSelector.getSelectedItem();
                    waveTypes.add(wt.id);
                }

                if (bgmWavFile != null) {
                    File tempWav = File.createTempFile("temp_bgm", ".wav");
                    tempWav.deleteOnExit();
                    Files.copy(bgmWavFile.toPath(), tempWav.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    AudioProcessor.convertWavToAt9(tempWav, new File(outputDir, "BGM.at9"));
                    tempWav.delete();
                }

                processImage(startBgFile, new File(outputDir, "start_bg.png"), 960, 512);
                processImage(previewThumbFile, new File(outputDir, "preview_thumbnail.png"), 226, 128);
                processImage(previewPageFile, new File(outputDir, "preview_page.png"), 480, 272);
                processImage(previewLockscreenFile, new File(outputDir, "preview_lockscreen.png"), 480, 272);

                ThemeXmlWriter.writeThemeXml(outputDir,
                    themeName, authorField.getText(), versionField.getText(),
                    bgNames, waveTypes,
                    ColorFieldPanel.toRGBHex(barColorPanel.getSelectedColor()),
                    ColorFieldPanel.toRGBHex(indicatorColorPanel.getSelectedColor()),
                    ColorFieldPanel.toRGBHex(dateColorPanel.getSelectedColor()),
                    String.valueOf(dateLayoutBox.getSelectedIndex()),
                    ColorFieldPanel.toRGBHex(notifyFontColorPanel.getSelectedColor()),
                    ColorFieldPanel.toARGBHex(notifyBgColorPanel.getSelectedColor(), true),
                    ColorFieldPanel.toARGBHex(notifyBorderColorPanel.getSelectedColor(), true),
                    previewPageFile != null, previewLockscreenFile != null
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Build failed: " + ex.getMessage());
            }
            return null;
        }

        protected void done() {
            progressDialog.dispose();
            JOptionPane.showMessageDialog(frame, "Theme built successfully!");
        }
    };

    worker.execute();
    progressDialog.setVisible(true);
}


    private void processImage(File input, File output, int w, int h) throws Exception {
        if (input != null && input.exists()) {
            ImageProcessor.convertToPng8Resized(input, output, w, h);
        }
    }
}
