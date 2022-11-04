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
        static List<Integer> compress(byte[] input){

            List<List<Integer>> listOfIntegerSets = new ArrayList<>();
            List<Integer> result = new ArrayList<>();

            for (var i = 0; i < input.length; i++) {
                int current = input[i];
                List<Integer> tempList = new ArrayList<>();
                tempList.add(current);

                boolean inList = false;
                int appends = 1;
                while(i + appends < input.length){
                    tempList.add((int) input[i + appends]);

                    if(!listOfIntegerSets.contains(tempList)){
                        listOfIntegerSets.add(tempList);
                        break;
                    }else{
                        current = SIZE + listOfIntegerSets.indexOf(tempList);
                        inList = true;
                    }

                    appends++;
                }
                if(inList){
                    i += appends -1;
                }

                result.add(current);
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
                    if(input.get(i + appends) >= 256 && input.get(i + appends) - 256 < listOfIntegerSets.size()){
                        int firstIndex = listOfIntegerSets.get(input.get(i + appends) - 256).get(0);
                        tempList.add(firstIndex);
                    }else if(input.get(i + appends) >= 256){
                        int firstIndex = tempList.get(0);
                        tempList.add(firstIndex);
                    } else{
                        tempList.add(input.get(i + appends));
                    }

                    if(!listOfIntegerSets.contains(tempList)){
                        listOfIntegerSets.add(tempList);
                        break;
                    }

                    appends++;

                }

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
        //READ TEXT DATA FOR COMPRESSION
        String filename = "compressTest.txt";
        String contentFromFile = Files.readString(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + filename));


        System.out.println("--------------------");
        System.out.println("BEFORE COMPRESSION:");
        System.out.println("--------------------");
        System.out.println(contentFromFile);
        byte[] textToBytes = contentFromFile.getBytes(StandardCharsets.UTF_8);
        List<Integer> intFromCompressedFile = new ArrayList<>();

        // COMPRESSION, RESULT WRITTEN TO FILE
        List<Integer> result = LZ.compress(textToBytes);
        writeToFile(result);


        //COMPRESSED FILE READ
        String compressedFile = "Compressed_LZ.txt";
        String compressedFileText = Files.readString(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + compressedFile));

        for (String s: compressedFileText.split(" ")) {
            intFromCompressedFile.add(Integer.parseInt(s));
        }

        //DECOMPRESS COMPRESSED FILE DATA
        List<Integer> decompressed = LZ.decompress(intFromCompressedFile);

        System.out.println("\n\nOriginal size: " + textToBytes.length);
        System.out.println("Compressed size: " + result.size());
        System.out.println("Compress percentage achieved: " + String.format("%.2f%%",(1 - (double)result.size()/(double)textToBytes.length)*100));

        System.out.println("\nDecompressed size: " + decompressed.size() + "\n");

        String intToString = "";

        for (int i: decompressed) {
            if(i > -1){
                char c = (char)i;
                intToString += c;
            }else if(i < -61){
                switch (i) {
                    case -90 -> intToString += "æ";
                    case -72 -> intToString += "ø";
                    case -91 -> intToString += "å";
                }
            }
        }

        System.out.println("--------------------");
        System.out.println("AFTER DECOMPRESSION:");
        System.out.println("--------------------");
        System.out.println(intToString);

    }


}
