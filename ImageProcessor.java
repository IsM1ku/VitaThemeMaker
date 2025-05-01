
package src;

import java.io.*;

public class ImageProcessor {
    public static void convertToPng8(File input, File output) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            "magick",
            input.getAbsolutePath(),
            "-resize", "960x512!",
            "-colors", "256",
            "PNG8:" + output.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor();
    }

    public static void convertToPng8Resized(File input, File output, int width, int height) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            "magick",
            input.getAbsolutePath(),
            "-resize", width + "x" + height + "!",
            "-colors", "256",
            "PNG8:" + output.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("[ImageMagick] " + line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 || !output.exists() || output.length() == 0) {
            throw new IOException("ImageMagick failed to convert/resize image.");
        }
    }

    public static void createThumbnail(File input, File output) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            "magick",
            input.getAbsolutePath(),
            "-resize", "360x192!",
            "-colors", "256",
            "PNG8:" + output.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("[ImageMagick] " + line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 || !output.exists() || output.length() == 0) {
            throw new IOException("ImageMagick failed to create thumbnail.");
        }
    }
}
