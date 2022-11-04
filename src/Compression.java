import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Compression {



    static class LZ{

        static int[] compress(byte[] input){

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
            System.out.println(input.length);
            System.out.println(result.size());



            return null;
        }


    }


    public static void main(String[] args) {
        String test = "Her er en tekst som er veldig repeterende med mange eer fordi e er en vanlig bokstav";
        System.out.println(test);
        byte[] textToBytes = test.getBytes(StandardCharsets.UTF_8);
        System.out.println(textToBytes.length);
        System.out.println();

        for (byte b: textToBytes) {
            System.out.print(b + " ");
        }
        System.out.println();

        LZ.compress(textToBytes);

    }


}
