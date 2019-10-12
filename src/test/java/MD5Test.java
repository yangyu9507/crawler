import org.apache.commons.codec.digest.DigestUtils;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * created by yangyu on 2019-10-11
 */

public class MD5Test {


    @Test
    public void tset() throws Exception{
        System.out.println(encryptMD5("druid"));

        String druid = DigestUtils.md5Hex("druid");
        System.out.println(druid);
        byte[] bytes = DigestUtils.md5(druid);
        System.out.println(Arrays.toString(bytes));

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase());
        }

        System.out.println(new String(bytes, Charset.forName("GBK")));
        System.out.println(new String(bytes, Charset.forName("UTF-8")));
        System.out.println(new String(bytes, Charset.forName("ISO-8859-1")));
    }

    private static String encryptMD5(String data) {
        byte[] bytes = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data.getBytes("UTF-8"));

        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return byte2hex(bytes).toLowerCase();
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();

        for(int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(bytes[i] & 255);
            if (hex.length() == 1) {
                sign.append("0");
            }

            sign.append(hex.toUpperCase());
        }

        return sign.toString();
    }
}
