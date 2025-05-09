package src;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ThemeXmlWriter {
    public static void writeThemeXml(File outDir, String themeName, String author, String version,
                                     List<String> bgNames, List<Integer> waveTypes,
                                     String barColor, String indicatorColor,
                                     String dateColor, String dateLayout,
                                     String notifyFontColor, String notifyBgColor, String notifyBorderColor,
                                     boolean includePreviewPage, boolean includePreviewLockscreen) throws IOException {

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<theme format-ver=\"00.99\" package=\"0\">\n");
        xml.append("  <HomeProperty>\n");
        xml.append("    <m_bgParam>\n");

        for (int i = 0; i < bgNames.size(); i++) {
            xml.append("      <BackgroundParam>\n");
            xml.append("        <m_imageFilePath>").append(bgNames.get(i)).append("</m_imageFilePath>\n");
            String thumb = bgNames.get(i).replace(".png", "t.png");
            xml.append("        <m_thumbnailFilePath>").append(thumb).append("</m_thumbnailFilePath>\n");
            xml.append("        <m_waveType>").append(waveTypes.get(i)).append("</m_waveType>\n");
            xml.append("      </BackgroundParam>\n");
        }

        xml.append("    </m_bgParam>\n");
        xml.append("    <m_bgmFilePath>BGM.at9</m_bgmFilePath>\n");
        xml.append("  </HomeProperty>\n");

        xml.append("  <InfomationBarProperty>\n");
        xml.append("    <m_barColor>").append(convertToARGBIfRGB(barColor)).append("</m_barColor>\n");
        xml.append("    <m_indicatorColor>").append(convertToARGBIfRGB(indicatorColor)).append("</m_indicatorColor>\n");
        xml.append("  </InfomationBarProperty>\n");

        xml.append("  <InfomationProperty>\n");
        xml.append("    <m_contentVer>").append(version).append("</m_contentVer>\n");
        xml.append("    <m_provider><m_default>").append(author).append("</m_default></m_provider>\n");
        xml.append("    <m_title><m_default>").append(themeName).append("</m_default></m_title>\n");
        xml.append("    <m_packageImageFilePath>preview_thumbnail.png</m_packageImageFilePath>\n");
        if (includePreviewPage)
            xml.append("    <m_homePreviewFilePath>preview_page.png</m_homePreviewFilePath>\n");
        if (includePreviewLockscreen)
            xml.append("    <m_startPreviewFilePath>preview_lockscreen.png</m_startPreviewFilePath>\n");
        xml.append("  </InfomationProperty>\n");

        xml.append("  <StartScreenProperty>\n");
        xml.append("    <m_filePath>start_bg.png</m_filePath>\n");
        xml.append("    <m_dateColor>").append(convertToARGBIfRGB(dateColor)).append("</m_dateColor>\n");
        xml.append("    <m_dateLayout>").append(dateLayout).append("</m_dateLayout>\n");
        xml.append("    <m_notifyFontColor>").append(convertToARGBIfRGB(notifyFontColor)).append("</m_notifyFontColor>\n");
        xml.append("    <m_notifyBgColor>").append(notifyBgColor).append("</m_notifyBgColor>\n");
        xml.append("    <m_notifyBorderColor>").append(notifyBorderColor).append("</m_notifyBorderColor>\n");
        xml.append("  </StartScreenProperty>\n");

        xml.append("</theme>\n");

        File xmlFile = new File(outDir, "theme.xml");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(xmlFile), StandardCharsets.UTF_8)) {
            writer.write(xml.toString());
        }
    }

    // Updated format to enforce ARGB consistency
    public static String toARGB(String rgb) {
        return "ff" + rgb.toLowerCase(); // prepend full opacity if missing alpha
    }

    public static String convertToARGBIfRGB(String color) {
        return (color.length() == 6) ? toARGB(color) : color;
    }
}
