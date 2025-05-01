
package src;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ThemeBuilder {
    private JFrame frame;
    private DefaultListModel<File> fileListModel;
    private File outputDir;
    private JTextField themeNameField;
    private JTextField authorField;
    private File bgmWavFile = null;
    private File startBgFile = null;
    private File previewThumbFile = null;
    private File previewPageFile = null;
    private File previewLockscreenFile = null;
    private JLabel bgmDropLabel, startBgLabel, previewThumbLabel, previewPageLabel, previewLockLabel;
    private JList<File> fileList;

    public void createAndShowGUI() {
        frame = new JFrame("PS Vita Theme Maker");
        fileListModel = new DefaultListModel<>();

        fileList = new JList<>(fileListModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(350, 200));
        fileList.setDropTarget(getDropTargetForImages());

        themeNameField = new JTextField("My Theme");
        authorField = new JTextField("Author");

        bgmDropLabel = createDropLabel("Drop BGM .wav here or click to pick", f -> {
            if (f.getName().toLowerCase().endsWith(".wav")) {
                bgmWavFile = f;
                bgmDropLabel.setText("BGM: " + f.getName());
            }
        }, "*.wav");

        startBgLabel = createDropLabel("Drop start_bg.png (960×512)", f -> startBgFile = handleSetImage(f, "start_bg.png", startBgLabel), "*.png");
        previewThumbLabel = createDropLabel("Drop preview_thumbnail.png (226×128)", f -> previewThumbFile = handleSetImage(f, "preview_thumbnail.png", previewThumbLabel), "*.png");
        previewPageLabel = createDropLabel("Drop preview_page.png (480×272)", f -> previewPageFile = handleSetImage(f, "preview_page.png", previewPageLabel), "*.png");
        previewLockLabel = createDropLabel("Drop preview_lockscreen.png (480×272)", f -> previewLockscreenFile = handleSetImage(f, "preview_lockscreen.png", previewLockLabel), "*.png");

        JButton pickImagesButton = new JButton("Pick Background Images");
        pickImagesButton.addActionListener(e -> {
            FileDialog dialog = new FileDialog(frame, "Select PNG or JPG images", FileDialog.LOAD);
            dialog.setMultipleMode(true);
            dialog.setVisible(true);
            for (File f : dialog.getFiles()) {
                if (fileListModel.size() >= 10) break;
                String name = f.getName().toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                    fileListModel.addElement(f);
                }
            }
        });

        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> {
            for (File f : fileList.getSelectedValuesList()) {
                fileListModel.removeElement(f);
            }
        });

        JButton convertButton = new JButton("Build Theme");
        convertButton.addActionListener(e -> {
            if (fileListModel.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please add background images.");
                return;
            }

            try {
                String themeName = themeNameField.getText().trim();
                outputDir = new File(System.getProperty("user.dir"), Utils.sanitizeFolderName(themeName));
                outputDir.mkdirs();

                List<String> bgNames = new ArrayList<>();
                for (int i = 0; i < fileListModel.size(); i++) {
                    File input = fileListModel.get(i);
                    String baseName = String.format("bg_%02d", i + 1);
                    File output = new File(outputDir, baseName + ".png");
                    File thumb = new File(outputDir, baseName + "t.png");
                    ImageProcessor.convertToPng8(input, output);
                    ImageProcessor.createThumbnail(output, thumb);
                    bgNames.add(baseName + ".png");
                }

                if (bgmWavFile != null) {
                    File wavCopy = new File(outputDir, "BGM.wav");
                    Files.copy(bgmWavFile.toPath(), wavCopy.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    AudioProcessor.convertWavToAt9(wavCopy, new File(outputDir, "BGM.at9"));
                }

                handlePreviewImage(startBgFile, new File(outputDir, "start_bg.png"), 960, 512);
                handlePreviewImage(previewThumbFile, new File(outputDir, "preview_thumbnail.png"), 226, 128);
                handlePreviewImage(previewPageFile, new File(outputDir, "preview_page.png"), 480, 272);
                handlePreviewImage(previewLockscreenFile, new File(outputDir, "preview_lockscreen.png"), 480, 272);

                ThemeXmlWriter.writeThemeXml(outputDir, themeName, authorField.getText(), bgNames);
                JOptionPane.showMessageDialog(frame, "Theme built successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Theme build failed: " + ex.getMessage());
            }
        });

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(new JLabel("Theme Name:")); formPanel.add(themeNameField);
        formPanel.add(new JLabel("Author:")); formPanel.add(authorField);
        formPanel.add(new JLabel("BGM:")); formPanel.add(bgmDropLabel);
        formPanel.add(new JLabel("Start Background:")); formPanel.add(startBgLabel);
        formPanel.add(new JLabel("Preview Thumbnail:")); formPanel.add(previewThumbLabel);
        formPanel.add(new JLabel("Preview Page:")); formPanel.add(previewPageLabel);
        formPanel.add(new JLabel("Preview Lockscreen:")); formPanel.add(previewLockLabel);
        formPanel.add(new JLabel("Add Backgrounds:")); formPanel.add(pickImagesButton);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(scrollPane, BorderLayout.CENTER);
        imagePanel.add(removeButton, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(convertButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private DropTarget getDropTargetForImages() {
        return new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> dropped = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : dropped) {
                        if (fileListModel.size() >= 10) break;
                        String name = f.getName().toLowerCase();
                        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                            fileListModel.addElement(f);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    private JLabel createDropLabel(String text, java.util.function.Consumer<File> handler, String fileFilter) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        label.setPreferredSize(new Dimension(300, 40));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> files = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) handler.accept(files.get(0));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FileDialog dialog = new FileDialog(frame, "Select File", FileDialog.LOAD);
                dialog.setFile(fileFilter);
                dialog.setVisible(true);
                if (dialog.getFile() != null) {
                    File f = new File(dialog.getDirectory(), dialog.getFile());
                    handler.accept(f);
                }
            }
        });
        return label;
    }

    private File handleSetImage(File f, String label, JLabel labelRef) {
        if (f != null && f.exists()) {
            labelRef.setText(label + ": " + f.getName());
            return f;
        }
        return null;
    }

    private void handlePreviewImage(File input, File output, int w, int h) throws Exception {
        if (input != null && input.exists()) {
            ImageProcessor.convertToPng8Resized(input, output, w, h);
        }
    }
}
