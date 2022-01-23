import java.io.IOException;

public class SHA256_Sample {
    public static void main(String[] args) throws IOException {
        // Create new instance of SHA256
        SHA256 m = new SHA256();

        // Test with string
        System.out.println(m.getHashFromString(new String("Hello World!")));

        // Test with Text File
        System.out.println(m.getHashFromFile("sample.pdf"));
    }
}
