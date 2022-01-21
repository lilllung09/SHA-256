import java.nio.charset.StandardCharsets;

public class SHA256 {
    public static void main(String[] args) {
        SHA256 m = new SHA256();

        m.getHashValue("abcd");
    }

    public SHA256() {
        this.init_H();
        this.init_K();
    }

    public String getHashValue(String message) {
        String hashed_message = "";

        byte[][][] message_padded = this.padding_M(message);

        return hashed_message;
    }

    public long multiple = 4294967296L;
    public double[] prime_numbers = {
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
        73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
        127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
        179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
        233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
        283, 293, 307, 311
    };
    public long[] H = new long[8];
    public long[] K = new long[64];

    //For SHA-256 initial hash value
    private void init_H() {
        for (int i = 0; i < 8; i++)
            H[i] = (long)((Math.sqrt(prime_numbers[i]) % 1) * multiple);
    }
    //SHA-256 Constants
    private void init_K() {
        for (int i = 0; i < 64; i++)
            K[i] = (long)((Math.cbrt(prime_numbers[i]) % 1) * multiple);
    }

    public byte[][][] padding_M(String message) {
        byte[] mb = message.getBytes(StandardCharsets.UTF_8);

        // message의 bit 수
        long bit_size = mb.length * 8L;

        // 1. message의 길이를 512bit의 배수로 만들 공간
        byte[][][] message_P = new byte[mb.length / 64 + 1 + (bit_size % 512 + 1 > 448 ? 1 : 0)][16][4];

        // 2. message를 message_P에 넣기 (앞 부분 부터)
        for (int i = 0, pos = 0; i < message_P.length && pos < mb.length; i++) {
            for (int j = 0; j < message_P[0].length && pos < mb.length; j++)
                for (int k = 0; k < message_P[0][0].length && pos < mb.length; k++, pos++)
                    message_P[i][j][k] = mb[pos];
        }
        // 확인용: message를 message_P에 넣기
        {
            System.out.println("-------------------------origin message input-------------------------");
            for (byte b : mb)
                System.out.print(
                        String.format("%32s", Integer.toBinaryString(b))
                                .substring(24)
                                .replace(' ', '0')
                        + " "
                );
            System.out.println();
            for (int i = 0, pos = 0; i < message_P.length && pos < mb.length; i++) {
                for (int j = 0; j < message_P[0].length && pos < mb.length; j++)
                    for (int k = 0; k < message_P[0][0].length && pos < mb.length; k++, pos++)
                        System.out.println(
                                String.format("%32s", Integer.toBinaryString(message_P[i][j][k]))
                                        .substring(24)
                                        .replace(' ', '0')
                                + ", message_P[" + i + "][" + j + "][" + k + "]"
                        );
            }
        }

        // 3. 추가된 zero-bit와 message를 구분하기 위해 '0b1000_0000' 넣기
        message_P[message_P.length - 1][(int)bit_size % 512 / 32][(int)bit_size % 512 % 32 / 8] = (byte)0b1000_0000;

        // 4. message의 사이즈를 message_P의 끝 64bit에 넣기 (뒷 부분 부터)
        for (int j = message_P[0].length - 1; j > 13; j--) {
            for (int k = message_P[0][0].length - 1; k > -1; k--) {
                message_P[message_P.length - 1][j][k] = (byte) bit_size;
                bit_size = bit_size >> 8;
            }
        }
        // 확인용: message의 사이즈를 message_P의 끝 64bit에 넣기
        {
            System.out.println("-------------------------origin message size-------------------------");
            System.out.println(String.format("%64s", Long.toBinaryString(mb.length * 8L)).replace(' ', '0'));
            for (int i = 14; i < message_P[0].length; i++) {
                for (int j = 0; j < message_P[0][0].length; j++)
                    System.out.println(
                            String.format("%32s", Integer.toBinaryString(message_P[message_P.length - 1][i][j]))
                                    .substring(24)
                                    .replace(' ', '0')
                            + ", message_P[" + (message_P.length - 1) + "][" + i + "][" + j + "]"
                    );
            }
        }


        // 결과
        {
            System.out.println("-------------------------padding message output-------------------------");
            for (byte[][] block_512 : message_P) {
                for (byte[] block_32 : block_512)
                    for (byte block_8 : block_32)
                        System.out.print(
                                String.format("%32s", Integer.toBinaryString(block_8))
                                        .substring(24)
                                        .replace(' ', '0')
                                + " "
                        );
            }
        }

        return message_P;
    }
}
