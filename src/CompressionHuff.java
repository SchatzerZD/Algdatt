import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//Ikke diss oss
public class CompressionHuff {
String text;

    public String readFromFile(File myObj){
        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                 text = myReader.nextLine();

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return text;
    }

    //TODO: do we have to make it into int??
    public static String[] split(String text){
        return text.split(" ");
    }

    public void frequencyTable(String[] list) {
        ArrayList<Integer> freqArray = new ArrayList<>();

        System.out.println(Arrays.toString(list));

        List<String> listofOptions = (List<String>) Arrays.asList(list);
        List<String> listDistinct = listofOptions.stream().distinct().collect(Collectors.toList());

        System.out.println(listDistinct);

        for (int i = 0; i < listDistinct.size(); i++) {
            int frequency = 0;

            for (int j = 0; j < list.length; j++) {


                if (Objects.equals(listDistinct.get(i), list[j])) {
                    frequency++;
                }
            }
            freqArray.add(frequency);
        }
        System.out.println(freqArray);


    }

    public static void main(String[] args) throws IOException {
        CompressionHuff compressionHuff = new CompressionHuff();

        String filename = "Compressed_LZ";

        File myObj = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + System.getProperty("file.separator") + filename + ".txt");

        compressionHuff.frequencyTable(split(compressionHuff.readFromFile(myObj)));


    }
}