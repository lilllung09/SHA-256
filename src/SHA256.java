import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SHA256 {
    private final int[] H0 = new int[8];
    private final int[] K = new int[64];

    public SHA256() {
        double[] prime_numbers = {
                2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
                31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
                73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
                127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
                179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
                233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
                283, 293, 307, 311
        };

        // Setting initial hash value, H
        for (int i = 0; i < 8; i++)
            H0[i] = (int)(long) ((Math.sqrt(prime_numbers[i]) % 1) * 4294967296L);

        // Constants, K
        for (int i = 0; i < 64; i++)
            K[i] = (int)(long) ((Math.cbrt(prime_numbers[i]) % 1) * 4294967296L);
    }

    public String getHashFromString(String message) {
        return this.getHashString(message.getBytes());
    }
    public String getHashFromFile(String filepath) throws IOException {
        return this.getHashString(Files.readAllBytes(Paths.get(filepath)));
    }

    // Get Hash value with human-readable string
    private String getHashString(byte[] object) {
        // Padding and Expansion the Message
        int[][] M = this.init_M(object);

        // Result 256bit message digest of the message
        StringBuilder hashed_sb = new StringBuilder();

        // H = present hash value, H_prev = previous hash value
        int[] H = H0;
        int[] H_prev;

        // Round Function
        for (int i = 1; i < M.length + 1; i++) {
            H_prev = H;
            for (int j = 0; j < 64; j++)
                H = this.Round_Function(this.K[j], H, M[i-1][j]);

            // Get hash value, Hⁱ₀₋₇ (H[j]) of Message block, Mⁱ (M[i])
            for (int j = 0; j < 8; j++)
                H[j] += H_prev[j];
        }

        // Binary to Hex for human-readable
        for (int j = 0; j < 8; j++)
            hashed_sb.append(String.format("%8X", H[j]));

        return hashed_sb.toString().replace(' ', '0');
    }

    // Padding the Message
    private int[][] init_M(byte[] object) {
        // bit length of message
        long bit_length = object.length * 8L;

        // Count of message block divided by 512bit to be created before hash computation begins
        final int length_block_512 = object.length / 64 + (bit_length % 512 > 448 - 1 ? 1 : 0) + 1;

        // Padded message (Multiple of 512bits), will be returned
        int[][] message_P = new int[length_block_512][64];

        ////////////////////////////////////////////////////////////////
        // 1. Move bit arrays of message to message_P (Change chunk size, 8 -> 32bit)
        ////////////////////////////////////////////////////////////////
        for (int i = 0, pos = 0; i < length_block_512 && pos < object.length; i++) {
            for (int j = 0; j < 16 && pos < object.length; j++)
                for (int k = 3; k > -1 && pos < object.length; k--, pos++)
                    message_P[i][j] |= (object[pos] & 255) << (8 * k);
        }
        // (int)255 is equal (int)0b0000_0000_0000_0000_0000_0000_1111_1111
        // FOR CHECK: Move bit arrays
        /*{
            System.out.println("-------------------------origin message input-------------------------");
            for (byte b : mb)
                System.out.print(
                        String.format("%32s", Integer.toBinaryString(b))
                                .substring(24)
                                .replace(' ', '0')
                        + " "
                );
            System.out.println();
            for (int i = 0, pos = 0; i < length_block_512 && pos < mb.length; i++) {
                for (int j = 0; j < 16 && pos < mb.length; j++, pos += 4)
                    System.out.println(
                            String.format("%32s", Integer.toBinaryString(message_P[i][j]))
                                    .replace(' ', '0')
                            + ", message_P[" + i + "][" + j + "]"
                    );
            }
        }*/


        ////////////////////////////////////////////////////////////////
        // 2. Padding '1' by 1bit end of message
        ////////////////////////////////////////////////////////////////
        message_P[(int)bit_length / 512][(int)bit_length % 512 / 32]
                |= -2147483648 >>> object.length % 4 * 8;
        // (int)-2147483648 is equal (int)0b1000_0000_0000_0000_0000_0000_0000_0000


        ////////////////////////////////////////////////////////////////
        // 3. Padding message length by 64bit
        ////////////////////////////////////////////////////////////////
        message_P[length_block_512 - 1][15] = (int)bit_length;
        message_P[length_block_512 - 1][14] = (int)(bit_length >>> 32);


        ////////////////////////////////////////////////////////////////
        // 4. Apply Message Expansion Function
        ////////////////////////////////////////////////////////////////
        for (int i = 0; i < message_P.length; i++) {
            for (int j = 16; j < 64; j++)
                message_P[i][j] = MEXP(message_P[i][j - 2], message_P[i][j - 7], message_P[i][j - 15], message_P[i][j - 16]);
        }


        // FOR CHECK: Padded message
        /*{
            System.out.println("-------------------------padding message output-------------------------");
            for (int[] block_512 : message_P) {
                for (int j = 0; j < 16; j++)
                    System.out.print(
                            String.format("%32s", Integer.toBinaryString(block_512[j]))
                                    .replace(' ', '0')
                            + " "
                    );
                System.out.println();
            }
        }*/
        return message_P;
    }

    // Message Expansion Function
    private int MEXP(int block_2, int block_7, int block_15, int block_16) {
        return ((Integer.rotateRight(block_2, 17)) ^ (Integer.rotateRight(block_2, 19)) ^ (block_2 >>> 10))  // σ₁
                + block_7
                + ((Integer.rotateRight(block_15, 7)) ^ (Integer.rotateRight(block_15, 18)) ^ (block_15 >>> 3)) // σ₀
                + block_16;
    }

    // Round Function
    private int[] Round_Function(int k, int[] H, int w) {
        int upper_sigma_1 = (Integer.rotateRight(H[4], 6)) ^ (Integer.rotateRight(H[4], 11)) ^ (Integer.rotateRight(H[4], 25)); // Σ₁
        int choose = (H[4] & H[5]) ^ (~H[4] & H[6]); // Ch(e, f, g)

        return new int[] {
                ((Integer.rotateRight(H[0], 2)) ^ (Integer.rotateRight(H[0], 13)) ^ (Integer.rotateRight(H[0], 22))) // Σ₀
                + ((H[0] & H[1]) ^ (H[0] & H[2]) ^ (H[1] & H[2])) // Maj(a, b, c)
                + (k + upper_sigma_1 + choose + H[7] + w),
                H[0],
                H[1],
                H[2],
                H[3] + (k + upper_sigma_1 + choose + H[7] + w),
                H[4],
                H[5],
                H[6],
        };
    }
}
