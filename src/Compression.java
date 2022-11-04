import java.nio.charset.StandardCharsets;

public class Compression {





    public static void main(String[] args) {
        String test = "JegheterDaniel";
        System.out.println(test);
        byte[] textToBytes = test.getBytes(StandardCharsets.UTF_8);
        System.out.println(textToBytes.length);
        System.out.println();

        for (byte b: textToBytes) {
            System.out.print(b + " ");
        }
    }


}
