import java.io.*;
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
                System.out.print("Compressing [" + "#".repeat(i/(input.length/10)) + " ".repeat(10-(i/(input.length/10))) + "] " + String.format("%.2f%%",(((double)i/(double)input.length)*100)) + "\r");
            }
            System.out.println("\nCompression Successful\n");

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

                System.out.print("Decompressing [" + "#".repeat(i/(input.size()/10)) + " ".repeat(10-(i/(input.size()/10))) + "] " + String.format("%.2f%%",(((double)i/(double)input.size())*100)) + "\r");
            }

            System.out.println("\nDecompression Successful\n");

            return result;
        }


    }

        static void writeToFile(List<Integer> results, String filename){
            try {
                DataOutputStream outFile = new DataOutputStream((new FileOutputStream(filename)));

                for (int i: results) {
                    if(i < 128 && i > -129){
                        outFile.writeByte(i);
                    }else{
                        outFile.writeChar(i);
                    }
                }

                outFile.flush();
                outFile.close();

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        static List<Integer> readIntegersFromFile(String filename){
            List<Integer> resultList = new ArrayList<>();
                try{
                    DataInputStream inFile = new DataInputStream(new FileInputStream(filename));


                    int i;
                    while((i=inFile.read())!= -1){
                        if(i==1){
                            i = inFile.read();
                            resultList.add(i+LZ.SIZE);
                        }else if(i>Byte.MAX_VALUE){
                            resultList.add(i-LZ.SIZE);
                        }
                        else{
                            resultList.add(i);
                        }
                    }

                }catch (IOException e){
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

            return resultList;

        }


    public static void main(String[] args) throws IOException {
        String compressedFileName = "Compressed_LZ.txt";
        //READ TEXT DATA FOR COMPRESSION
        String filename = "diverse.txt";
        String contentFromFile = Files.readString(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + filename));


        /*System.out.println("--------------------");
        System.out.println("BEFORE COMPRESSION:");
        System.out.println("--------------------");
        System.out.println(contentFromFile);*/
        byte[] textToBytes = contentFromFile.getBytes(StandardCharsets.UTF_8);


        // COMPRESSION, RESULT WRITTEN TO FILE
        List<Integer> result = LZ.compress(textToBytes);
        writeToFile(result,compressedFileName);


        //READ COMPRESSED FILE FOR DECOMPRESSION
        List<Integer> intFromCompressedFile = readIntegersFromFile(filename);


        //DECOMPRESS COMPRESSED FILE DATA
        List<Integer> decompressed = LZ.decompress(intFromCompressedFile);


        //CONVERTING DECOMPRESSED DATA INTO STRING
        byte[] decompressedBytes = new byte[decompressed.size()];
        for (int i = 0; i < decompressedBytes.length; i++) {
            decompressedBytes[i] = (byte) decompressed.get(i).intValue();
        }
        String decompressedString = new String(decompressedBytes, StandardCharsets.UTF_8);


        //PRINTING OUT DECOMPRESSED RESULT AFTER CHARACTER CONVERSION
        /*System.out.println("\n\n--------------------");
        System.out.println("AFTER DECOMPRESSION:");
        System.out.println("--------------------");
        System.out.println(decompressedString);*/


        //COUNT NUMBER OF BYTES IN COMPRESSED FILE
        int compressedSize = 0;
        for (int i: result) {
            compressedSize += (i < 128) ? 1:2;
        }


        //CHECK IF DECOMPRESSED HASN'T LOST ANY DATA
        boolean beforeCompressedEqualsAfterCompressed = true;
        for (int i = 0; i < textToBytes.length; i++) {
            if (!(textToBytes[i] == decompressed.get(i))) {
                beforeCompressedEqualsAfterCompressed = false;
                break;
            }
        }


        //COMPRESSION RESULTS PRINTED OUT
        System.out.format("%-32s %s %2s %s","\n\n\n+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %16s","|Original size","|",textToBytes.length,"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %16s","|Compressed size","|",compressedSize,"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %15s","|Compress percentage achieved","|",String.format("%.2f%%",(1 - (double)compressedSize/(double)textToBytes.length)*100),"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %16s","|Decompressed size","|",decompressed.size(),"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %17s","|Decompressed same as original","|",beforeCompressedEqualsAfterCompressed,"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");

    }


}
