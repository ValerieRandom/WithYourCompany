import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class LogProcessor {

    private static final List<Pattern> errorPatterns = new ArrayList<>();

    // 設定錯誤正則模式
    public static void addErrorPattern(String regex) {
        errorPatterns.add(Pattern.compile(regex));
    }

    // 遍歷資料夾並處理所有 .log/.txt 檔案
    public static void processAllLogs(String logDirPath, FTPService ftpHelper, ConfigLoader configLoader) {
        try {
            Files.walk(Paths.get(logDirPath))  // 遍歷資料夾
                    .filter(Files::isRegularFile)  // 過濾出檔案
                    .filter(path -> path.toString().endsWith(".log") || path.toString().endsWith(".txt"))  // 只處理 log 和 txt
                    .forEach(path -> processSingleFile(path, ftpHelper, configLoader));  // 處理每一個檔案
        } catch (IOException e) {
            System.err.println("遍歷資料夾時發生錯誤: " + e.getMessage());
        }
    }

    // 處理單一檔案的掃描邏輯
    private static void processSingleFile(Path logFilePath, FTPService ftpHelper, ConfigLoader configLoader) {
        System.out.println("正在掃描檔案: " + logFilePath);

        List<String> logContent = readLogFile(logFilePath.toString());  // 讀取檔案內容
        scanLogs(logContent, ftpHelper, configLoader);  // 掃描日誌內容
    }

    // 讀取單一檔案的所有內容
    public static List<String> readLogFile(String logPath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(logPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("讀取 log 檔案失敗: " + e.getMessage());
        }
        return lines;
    }

    // 掃描日誌內容，並根據錯誤模式進行匹配和處理
    public static void scanLogs(List<String> logContent, FTPService ftpHelper, ConfigLoader configLoader) {
        Set<Integer> writtenLines = new HashSet<>();

        for (int i = 0; i < logContent.size(); i++) {
            if (writtenLines.contains(i)) continue;

            String line = logContent.get(i);
            String matchedPattern = getMatchedPattern(line);

            if (matchedPattern != null) {
                System.out.println("找到匹配於第 " + (i + 1) + " 行: " + line);
                saveAndUploadLog(logContent, i, matchedPattern, writtenLines, ftpHelper, configLoader);
            }
        }
    }

    // 檢查哪個錯誤模式與當前行匹配
    private static String getMatchedPattern(String line) {
        for (int i = 0; i < errorPatterns.size(); i++) {
            Pattern pattern = errorPatterns.get(i);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return "err" + (i + 1);
            }
        }
        return null;
    }

    // 儲存並上傳符合條件的日誌片段
    private static void saveAndUploadLog(List<String> logContent, int index, String matchedPattern,
                                         Set<Integer> writtenLines, FTPService ftpHelper, ConfigLoader configLoader) {
        int start = Math.max(0, index - configLoader.getLinesBefore());
        int end = Math.min(logContent.size(), index + configLoader.getLinesAfter() + 1);

        String deviceIdentifier = DeviceInfo.getDeviceIdentifier();
        String outputFileName = "ERRORScanned_" + deviceIdentifier + ".txt";
        String fullOutputPath = Paths.get(configLoader.getOutputPath(), outputFileName).toString();

        File outputFile = new File(fullOutputPath);
        outputFile.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            writer.write("==== 上下文 ====\n");
            writer.write("匹配到的正則模式: " + matchedPattern + "\n");

            for (int i = start; i < end; i++) {
                if (writtenLines.contains(i)) continue;

                String prefix = (i == index) ? ">>> " : "    ";
                String suffix = (i == index) ? " <<<" : "";
                writer.write(prefix + (i + 1) + ": " + logContent.get(i) + suffix + "\n");

                writtenLines.add(i);
            }
            writer.write("================\n");
            System.out.println("成功儲存至: " + fullOutputPath);

            ftpHelper.uploadFile(fullOutputPath, outputFileName);

        } catch (IOException e) {
            System.err.println("儲存或上傳錯誤: " + e.getMessage());
        }
    }
}
