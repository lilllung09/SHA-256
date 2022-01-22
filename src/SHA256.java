import java.nio.charset.StandardCharsets;

public class SHA256 {
    public static void main(String[] args) {
        SHA256 m = new SHA256();

        System.out.println(m.getHashString("abc"));
    }

    public SHA256() {
        this.init_H();
        this.init_K();
    }

    public String getHashString(String message) {
        StringBuilder hashed_sb = new StringBuilder();

        // Padding and Expansion the Message
        int[][] M = this.init_M(message);

        // Round Function
        int[] H = H0.clone();
        for (int[] block_512 : M) {
            for (int i = 0; i < 64; i++)
                H = this.Round_Function(this.K[i], H, block_512[i]);
        }


        for (int i = 0; i < 8; i++) {
            hashed_sb.append(String.format("%X", H[i] + H0[i]));
        }

        return hashed_sb.toString().replace(' ', '0');
    }

    private final long multiple = 4294967296L;
    private final double[] prime_numbers = {
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
        73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
        127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
        179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
        233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
        283, 293, 307, 311
    };
    private final int[] H0 = new int[8];
    private final int[] K = new int[64];

    // SHA-256 initial hash value
    private void init_H() {
        for (int i = 0; i < 8; i++)
            H0[i] = (int)(long) ((Math.sqrt(prime_numbers[i]) % 1) * multiple);
    }
    // SHA-256 Constants
    private void init_K() {
        for (int i = 0; i < 64; i++)
            K[i] = (int)(long) ((Math.cbrt(prime_numbers[i]) % 1) * multiple);
    }

    // Padding the Message
    private int[][] init_M(String message) {
        final byte[] mb = message.getBytes(StandardCharsets.UTF_8);

        // message의 bit 수
        long bit_size = mb.length * 8L;

        // 생성될 512bit 블럭 갯수
        final int length_block_512 = mb.length / 64 + (bit_size % 512 > 448 - 1 ? 1 : 0) + 1;

        ////////////////////////////////////////////////////////////////
        // 1. Message Expansion Function을 거쳐 반환할 message_M
        ////////////////////////////////////////////////////////////////
        int[][] message_M = new int[length_block_512][64];


        ////////////////////////////////////////////////////////////////
        // 2. message를 message_M으로 옮기기 (chunk 변경, byte -> int)
        ////////////////////////////////////////////////////////////////
        for (int i = 0, pos = 0; i < length_block_512 && pos < mb.length; i++) {
            for (int j = 0; j < 16 && pos < mb.length; j++)
                for (int k = 3; k > -1 && pos < mb.length; k--, pos++)
                    message_M[i][j] |= mb[pos] << (8 * k);
        }
        // 확인용: message를 message_M으로 옮기기
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
                            String.format("%32s", Integer.toBinaryString(message_M[i][j]))
                                    .replace(' ', '0')
                            + ", message_P[" + i + "][" + j + "]"
                    );
            }
        }*/


        ////////////////////////////////////////////////////////////////
        // 3. 추가된 zero-bit와 message를 구분하기 위해 '1' 넣기
        ////////////////////////////////////////////////////////////////
        message_M[(int)bit_size / 512][(int)bit_size % 512 / 32] |= 0b1000_0000_0000_0000_0000_0000_0000_0000 >>> mb.length % 4 * 8;


        ////////////////////////////////////////////////////////////////
        // 4. message의 사이즈를 message_M의 끝 64bit에 넣기 (뒷 부분 부터)
        ////////////////////////////////////////////////////////////////
        message_M[length_block_512 - 1][15] = (int)bit_size;
        message_M[length_block_512 - 1][14] = (int)(bit_size >>> 32);


        ////////////////////////////////////////////////////////////////
        // 5. Message Expansion Function 적용
        ////////////////////////////////////////////////////////////////
        for (int[] block_512 : message_M) {
            for (int j = 16; j < 64; j++)
                block_512[j] = MEXP(block_512[j - 2], block_512[j - 7], block_512[j - 15], block_512[j - 16], j);
        }

        // 결과
        /*{
            System.out.println("-------------------------padding message output-------------------------");
            for (int[] block_512 : message_M) {
                for (int j = 0; j < 16; j++)
                    System.out.print(
                            String.format("%32s", Integer.toBinaryString(block_512[j]))
                                    .replace(' ', '0')
                            + " "
                    );
                System.out.println();
            }
        }*/
        return message_M;
    }

    // Message Expansion Function
    private int MEXP(int block_2, int block_7, int block_15, int block_16, int j) {
        return ((Integer.rotateRight(block_2, 17)) ^ (Integer.rotateRight(block_2, 19)) ^ (block_2 >>> 10))  // σ1
                + block_7
                + ((Integer.rotateRight(block_15, 7)) ^ (Integer.rotateRight(block_15, 18)) ^ (block_15 >>> 3)) // σ0
                + block_16;
    }

    // Round Function
    private int[] Round_Function(int k, int[] H, int w) {
        int upper_sigma_1 = (Integer.rotateRight(H[4], 6)) ^ (Integer.rotateRight(H[4], 11)) ^ (Integer.rotateRight(H[4], 25)); // Σ1
        int choose = (H[4] & H[5]) ^ (~H[4] & H[6]);

        return new int[] {
                ((Integer.rotateRight(H[0], 2)) ^ (Integer.rotateRight(H[0], 13)) ^ (Integer.rotateRight(H[0], 22))) // Σ0
                + ((H[0] & H[1]) ^ (H[0] & H[2]) ^ (H[1] & H[2])) // Maj(a1, b1, c1)
                + (k + upper_sigma_1 + choose + H[7] + w),

                H[0], // a1 -> b2
                H[1], // b1 -> c2
                H[2], // c1 -> d2

                H[3] + (k + upper_sigma_1 + choose + H[7] + w),

                H[4], // e1 -> f2
                H[5], // f1 -> g2
                H[6], // g1 -> h2
        };
    }
}
