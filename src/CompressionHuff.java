import java.io.*;
import java.util.*;

public class CompressionHuff {

//TODO: leser inn fra fil, tar inn det som ikke er kompimert og gjør om til int, finner frekvens og lager prioriteskø
// av prioritetskøen lager man huffmantre av huffman treet kan man gjøre om teksten til bitstrenger


    public static List<Integer> readIntegersFromFile(String filename) {
        List<Integer> resultList = new ArrayList<>();
        try {
            DataInputStream inFile = new DataInputStream(new FileInputStream(filename));


            int i;
            while ((i = inFile.read()) != -1) {
                if (i == 1) {
                    i = inFile.read();
                    resultList.add(i + Compression.LZ.SIZE);
                } else if (i > Byte.MAX_VALUE) {
                    resultList.add(i - Compression.LZ.SIZE);
                } else {
                    resultList.add(i);
                }
            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return resultList;

    }


    //TODO: remove elements from list after counted sequence - for speed
    public LinkedList<LinkedList<Integer>> frequencyTable(List<Integer> listAllInt) {
        LinkedList<LinkedList<Integer>> freqArray = new LinkedList<>();

        List<Integer> listDistinct = listAllInt.stream().distinct().toList();


        for (Integer integer : listDistinct) {
            int frequency = 0;

            for (Integer value : listAllInt) {

                if (Objects.equals(integer, value)) {
                    frequency++;
                }
            }
            LinkedList<Integer> charAndFrequency = new LinkedList<>();
            charAndFrequency.add(integer);
            charAndFrequency.add(frequency);

            freqArray.add(charAndFrequency);
        }

        return freqArray;
    }


        public static void main(String[] args) throws IOException {
        CompressionHuff compressionHuff = new CompressionHuff();

        String filename = "test.txt";

        System.out.println(readIntegersFromFile(filename));

        System.out.println("----------------------");
        System.out.println(compressionHuff.frequencyTable(readIntegersFromFile(filename)));




    }
}

