import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FTPService {

    private final String ftpHost;
    private final String ftpUser;
    private final String ftpPass;

    public FTPService(Properties config) {
        this.ftpHost = config.getProperty("ftp.host");
        this.ftpUser = config.getProperty("ftp.user");
        this.ftpPass = config.getProperty("ftp.pass");
    }

    public void uploadFile(String localFilePath, String remoteFileName) throws IOException {
        FTPClient ftpClient = new FTPClient();
        try (FileInputStream fis = new FileInputStream(localFilePath)) {
            ftpClient.connect(ftpHost);
            ftpClient.login(ftpUser, ftpPass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            boolean done = ftpClient.storeFile(remoteFileName, fis);
            if (done) {
                System.out.println("FTP 上傳成功: " + remoteFileName);
            } else {
                System.err.println("FTP 上傳失敗: " + remoteFileName);
            }
        } finally {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }
}
