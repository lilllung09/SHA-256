# SHA-256
To understanding 'SHA-256' by developing my own faster sha-256 algorithm.

---------------------------------------

### Performance test java.security.MessageDigest VS My SHA256
|Class|Count|Elapsed Time|AVG|Enhanced %|
|:---:|:---:|:---:|:---:|:---:|
|java.security.MessageDigest|1|27.46|27.444|+ 0%|
|"|2|27.408|||
|"|3|27.64|||
|"|4|27.324|||
|"|5|27.389|||
|My SHA256|1|9.749|8.466|+ 69.15%|
|"|2|8.159|||
|"|3|8.085|||
|"|4|8.113|||
|"|5|8.226|||

*Elapsed Time and AVG shorter is better, Enhanced % = 100 * (1 - (8.466 / 27.444))*

---------------------------------------

### Code for performance test
_Is there something wrong what I missed?_
```java
public class SHA256_Sample {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        long ch, beforeTime, afterTime;
        double secDiffTime;

        SHA256 m = new SHA256();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        StringBuilder builder = new StringBuilder();
        
        
        ch = 0;
        beforeTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            m.getHashFromString(String.valueOf(ch));
            
            ch++;
        }
        afterTime = System.currentTimeMillis();
        secDiffTime = (afterTime - beforeTime)/1000.0;
        System.out.println("Time(1-1) : "+secDiffTime);
        ///////////////////////////
        // X 5 
        ///////////////////////////


        ch = 0;
        beforeTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            md.update(String.valueOf(ch).getBytes());

            for (byte b : md.digest())
                builder.append(String.format("%02X", b));

            builder.toString();
            builder.setLength(0);
            
            ch++;
        }
        afterTime = System.currentTimeMillis();
        secDiffTime = (afterTime - beforeTime)/1000.0;
        System.out.println("Time(2-1) : "+secDiffTime);
        ///////////////////////////
        // X 5 
        ///////////////////////////
    }
}
```
---------------------------------------
### Source for Study and Develope (Thanks!)
```
SHA-256 Hash Algorithm - https://jusths.tistory.com/43?category=796552
Cryptographic One-Way Hash Functions - https://www.cs.rit.edu/~ark/lectures/onewayhash/onewayhash.shtml
Secure Hash Standard (SHS) - https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf
```
