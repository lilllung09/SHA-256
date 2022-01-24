import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256_Performance_Test {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        long ch, beforeTime, afterTime;
        double secDiffTime;

        // My SHA256
        SHA256 m = new SHA256();

        for (int i = 0; i < 5; i++) {
            ch = 0;
            beforeTime = System.currentTimeMillis();
            for (int j = 0; j < 1000000; j++) {
                m.getHashFromString(String.valueOf(ch));

                ch++;
            }
            afterTime = System.currentTimeMillis();
            secDiffTime = (afterTime - beforeTime) / 1000.0;
            System.out.println("Time(1-" + i + ") : " + secDiffTime);
        }

        // java.security.MessageDigest
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            ch = 0;
            beforeTime = System.currentTimeMillis();
            for (int j = 0; j < 1000000; j++) {
                md.update(String.valueOf(ch).getBytes());

                for (byte b : md.digest())
                    builder.append(String.format("%02X", b));

                builder.toString();
                builder.setLength(0);

                ch++;
            }
            afterTime = System.currentTimeMillis();
            secDiffTime = (afterTime - beforeTime) / 1000.0;
            System.out.println("Time(2-" + i + ") : " + secDiffTime);
        }
    }
}
