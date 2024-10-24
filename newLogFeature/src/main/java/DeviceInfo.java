import java.net.InetAddress;
import java.net.UnknownHostException;

public class DeviceInfo {

    public static String getDeviceIdentifier() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostAddress = inetAddress.getHostAddress();
            return hostAddress.replace(".", "_");
        } catch (UnknownHostException e) {
            System.err.println("無法取得裝置資訊: " + e.getMessage());
            return "unknown_device";
        }
    }
}
