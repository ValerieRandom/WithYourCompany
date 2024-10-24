import java.util.*;

public class Main {

    public static void main(String[] args) {

        // 1. 載入設定檔
        ConfigLoader config = new ConfigLoader();
        config.loadConfig();

        // 2. 初始化 FTP 客戶端
        FTPService ftpHelper = new FTPService(config.getConfig());

        // 3. 掃描整個資料夾並處理所有 log 檔案
        LogProcessor.processAllLogs(config.getLogPath(), ftpHelper, config);
    }
}
