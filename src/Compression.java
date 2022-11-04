import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Compression {

    static class LZ{
        static final int SIZE = 256;
        static List<Integer> compress(byte[] input, boolean printBytes){

            List<List<Integer>> listOfIntegerSets = new ArrayList<>();
            List<Integer> result = new ArrayList<>();

            for (var i = 0; i < input.length; i++) {
                int current = input[i];
                List<Integer> tempList = new ArrayList<>();
                tempList.add(current);

                int appends = 1;
                while(i + appends < input.length){
                    tempList.add((int) input[i + appends]);

                    if(!listOfIntegerSets.contains(tempList)){
                        listOfIntegerSets.add(tempList);
                        break;
                    }else{
                        int count = 0;

                        for (List<Integer> l: listOfIntegerSets) {
                            if(l.equals(tempList)){
                                break;
                            }
                            count++;
                        }

                        current = SIZE + count;
                        i++;
                    }

                    appends++;
                }

                result.add(current);
            }

            //Temporary printing
            if(printBytes){
                for (List<Integer> l: listOfIntegerSets) {
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

        List<Integer> result = LZ.compress(textToBytes, true);


        System.out.println();
        System.out.println();
        System.out.println("Original size: " + textToBytes.length);
        System.out.println("Compressed size: " + result.size());
        System.out.println("Compress percentage achieved: " + String.format("%.2f%%",(1 - (double)result.size()/(double)textToBytes.length)*100));

    }


}
