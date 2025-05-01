
package src;

import java.io.*;

public class AudioProcessor {
    public static void convertWavToAt9(File wavFile, File output) throws Exception {
        if (!wavFile.exists()) {
            throw new IOException("WAV file not found: " + wavFile.getAbsolutePath());
        }

        ProcessBuilder pb = new ProcessBuilder(
            "at9tool",
            "-e",
            "-br", "144",
            "-wholeloop",
            wavFile.getAbsolutePath(),
            output.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("[at9tool] " + line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 || !output.exists() || output.length() == 0) {
            throw new IOException("at9tool failed or output is empty. Exit code: " + exitCode);
        }
    }
}
