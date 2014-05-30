import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by lwj on 14-5-12.
 */
public class Test {
    public static void main(String[] args) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(addr.getHostAddress());
        System.out.println(addr.getHostName());
    }
}
