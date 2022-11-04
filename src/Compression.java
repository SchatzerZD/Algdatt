import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Compression {



    static class LZ{
        static List<Integer> compress(byte[] input, boolean printBytes){

            int size = 256;
            List<List<Integer>> listOfByteSet = new ArrayList<>();
            List<Integer> result = new ArrayList<>();

            for (var i = 0; i < input.length; i++) {
                int current = input[i];
                List<Integer> tempList = new ArrayList<>();
                tempList.add(current);

                int appends = 1;
                while(i + appends < input.length){
                    tempList.add((int) input[i + appends]);

                    if(!listOfByteSet.contains(tempList)){
                        listOfByteSet.add(tempList);
                        break;
                    }else{
                        int count = 0;

                        for (List<Integer> l: listOfByteSet) {
                            if(l.equals(tempList)){
                                break;
                            }
                            count++;
                        }

                        current = size + count;
                        i++;
                    }

                    appends++;
                }

                result.add(current);
            }

            //Temporary printing
            if(printBytes){
                for (List<Integer> l: listOfByteSet) {
                    for (int i: l) {
                        System.out.print(i + " ");
                    }
                    System.out.println();
                }

                System.out.println();
                for (int i: result) {
                    System.out.print(i + " ");
                }
                System.out.println();
            }


            return result;
        }
    }


    public static void main(String[] args) throws IOException {
        String filename = "diverse.txt";
        String contentFromFile = Files.readString(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + filename));

        System.out.println(contentFromFile);
        byte[] textToBytes = contentFromFile.getBytes(StandardCharsets.UTF_8);
        System.out.println(textToBytes.length);
        System.out.println();

        for (byte b: textToBytes) {
            System.out.print(b + " ");
        }
        System.out.println();


        List<Integer> result = LZ.compress(textToBytes, true);


        System.out.println();
        System.out.println();
        System.out.println("Original size: " + textToBytes.length);
        System.out.println("Compressed size: " + result.size());
        System.out.println("Compress ratio: " + (double)result.size()/(double)textToBytes.length);

    }


}
