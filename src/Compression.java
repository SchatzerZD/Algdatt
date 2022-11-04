import java.io.FileWriter;
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
                        current = SIZE + listOfIntegerSets.indexOf(tempList);
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


        static List<Integer> decompress(List<Integer> input){

            List<List<Integer>> listOfIntegerSets = new ArrayList<>();
            List<Integer> result = new ArrayList<>();


            for (var i = 0; i < input.size(); i++) {
                int current = input.get(i);
                List<Integer> tempList = new ArrayList<>();

                if(current >= LZ.SIZE){
                    List<Integer> currentGroupOfInt = listOfIntegerSets.get(current-256);
                    tempList.addAll(currentGroupOfInt);
                    result.addAll(currentGroupOfInt);

                }else{
                    tempList.add(current);
                    result.add(current);
                }

                int appends = 1;
                while(i + appends < input.size()){
                    if(input.get(i + appends) >= 256){
                        int firstIndex = listOfIntegerSets.get(input.get(i + appends) - 256).get(0);
                        tempList.add(firstIndex);
                    }else{
                        tempList.add(input.get(i + appends));
                    }

                    if(!listOfIntegerSets.contains(tempList)){
                        listOfIntegerSets.add(tempList);
                        break;
                    }else{
                        current = SIZE + listOfIntegerSets.indexOf(tempList);
                        i++;
                    }

                    appends++;

                }

                listOfIntegerSets.add(tempList);

            }


            return result;
        }


    }

     static void writeToFile(List<Integer> results){
            try {
                FileWriter myWriter = new FileWriter("Compressed_LZ.txt");
                for (int i: results) {
                    myWriter.write(i + " ");
                }
                myWriter.close();

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }


    public static void main(String[] args) throws IOException {
        String filename = "compressTest.txt";
        String contentFromFile = Files.readString(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + filename));

        System.out.println(contentFromFile);
        byte[] textToBytes = contentFromFile.getBytes(StandardCharsets.UTF_8);
        boolean debugInfo = true;


        for (byte b: textToBytes) {
            System.out.print(b + " ");
        }
        System.out.println();
        System.out.println(contentFromFile);
        System.out.println();


        List<Integer> result = LZ.compress(textToBytes, debugInfo);
        List<Integer> decompressed = LZ.decompress(result);


        System.out.println();
        System.out.println();
        System.out.println("Original size: " + textToBytes.length);
        System.out.println("Compressed size: " + result.size());
        System.out.println("Compress percentage achieved: " + String.format("%.2f%%",(1 - (double)result.size()/(double)textToBytes.length)*100));

        System.out.println();
        for (int i: decompressed) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("Decompressed size: " + decompressed.size());
        System.out.println();
        String intToString = "";
        for (int i: decompressed) {
            char c = (char)i;
            intToString += c;
        }
        System.out.println();
        System.out.println(intToString);

        writeToFile(result);
    }


}
