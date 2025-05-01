
package src;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ThemeXmlWriter {
    public static void writeThemeXml(File outDir, String themeName, String author, List<String> bgNames) throws IOException {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<theme format-ver=\"00.99\" package=\"0\">\n");
        xml.append("  <HomeProperty>\n");
        xml.append("    <m_bgParam>\n");

        for (String bg : bgNames) {
            xml.append("      <BackgroundParam>\n");
            xml.append("        <m_imageFilePath>").append(bg).append("</m_imageFilePath>\n");
            String thumb = bg.replace(".png", "t.png");
            xml.append("        <m_thumbnailFilePath>").append(thumb).append("</m_thumbnailFilePath>\n");
            xml.append("        <m_waveType>11</m_waveType>\n");
            xml.append("      </BackgroundParam>\n");
        }

        xml.append("    </m_bgParam>\n");
        xml.append("    <m_bgmFilePath>BGM.at9</m_bgmFilePath>\n");
        xml.append("  </HomeProperty>\n");

        xml.append("  <InfomationBarProperty>\n");
        xml.append("    <m_barColor>ff000000</m_barColor>\n");
        xml.append("    <m_indicatorColor>ffffffff</m_indicatorColor>\n");
        xml.append("  </InfomationBarProperty>\n");

        xml.append("  <InfomationProperty>\n");
        xml.append("    <m_contentVer>01.00</m_contentVer>\n");
        xml.append("    <m_provider><m_default>").append(author).append("</m_default></m_provider>\n");
        xml.append("    <m_title><m_default>").append(themeName).append("</m_default></m_title>\n");
        xml.append("    <m_packageImageFilePath>preview_thumbnail.png</m_packageImageFilePath>\n");
        xml.append("    <m_homePreviewFilePath>preview_page.png</m_homePreviewFilePath>\n");
        xml.append("    <m_startPreviewFilePath>preview_lockscreen.png</m_startPreviewFilePath>\n");
        xml.append("  </InfomationProperty>\n");

        xml.append("  <StartScreenProperty>\n");
        xml.append("    <m_filePath>start_bg.png</m_filePath>\n");
        xml.append("    <m_dateColor>ffffffff</m_dateColor>\n");
        xml.append("    <m_dateLayout>0</m_dateLayout>\n");
        xml.append("    <m_notifyFontColor>ffffffff</m_notifyFontColor>\n");
        xml.append("    <m_notifyBgColor>ffffffff</m_notifyBgColor>\n");
        xml.append("    <m_notifyBorderColor>ffcccccc</m_notifyBorderColor>\n");
        xml.append("  </StartScreenProperty>\n");

        xml.append("</theme>\n");

        File xmlFile = new File(outDir, "theme.xml");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(xmlFile), StandardCharsets.UTF_8)) {
            writer.write(xml.toString());
        }
    }
}
