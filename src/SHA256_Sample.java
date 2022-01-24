import java.io.IOException;

public class SHA256_Sample {
    public static void main(String[] args) throws IOException {
        // Create new instance of SHA256
        SHA256 m = new SHA256();

        // Test with string
        System.out.println(m.getHashFromString(new String("Hello World!")));
        // 7F83B1657FF1FC53B92DC18148A1D65DFC2D4B1FA3D677284ADDD200126D9069

        // Test with File
        System.out.println(m.getHashFromFile("NIST.FIPS.180-4.pdf"));
        // 0455B406D89648D20CBDE375561E19C245B9815E894164C2670772E3D54DEB82
    }
}
