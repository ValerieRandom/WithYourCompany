import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {

    private final Properties config = new Properties();
    private int linesBefore;
    private int linesAfter;
    private String outputPath;

    public void loadConfig() {
        try (InputStream input = Files.newInputStream(Paths.get("config.properties"))) {
            config.load(input);
            loadErrorPatterns();

            linesBefore = getConfigValue("lines.before", 10);
            linesAfter = getConfigValue("lines.after", 10);
          //outputPath = config.getProperty("output.path", "output");

        } catch (IOException e) {
            System.err.println("讀取設定檔失敗: " + e.getMessage());
        }
    }

    private void loadErrorPatterns() {
        for (String key : config.stringPropertyNames()) {
            if (key.startsWith("err")) {
                String regex = config.getProperty(key).trim();
                LogProcessor.addErrorPattern(regex);  // 加入 LogProcessor 的模式列表
                System.out.println("載入模式: " + key + " -> " + regex);
            }
        }
    }

    public String getLogPath() {
        return config.getProperty("log.path");
    }

    public Properties getConfig() {
        return config;
    }

    public int getLinesBefore() {
        return linesBefore;
    }

    public int getLinesAfter() {
        return linesAfter;
    }


      public String getOutputPath() {
       return outputPath;
    }


    private int getConfigValue(String key, int defaultValue) {
        String value = config.getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("無法解析為整數: " + key + "，使用預設值 " + defaultValue);
            return defaultValue;
        }
    }
}
