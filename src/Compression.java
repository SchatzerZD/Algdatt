import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Compression {

    class Huffman{
        static List<Node> nodes = new ArrayList<>();
        static List<Node> originalNodes = new ArrayList<>();
        static class Node{
            int nodeValue;
            List<Boolean> codeword;
            List<Node> children;
            Node leftNode;
            Node rightNode;
            Node parent;
            boolean pathValue;
            Node() {
                this.nodeValue = 0;
                codeword = null;
                this.leftNode = null;
                this.rightNode = null;
                this.parent = null;
                this.pathValue = false;
                children = new ArrayList<>();
            }
        }

        static void sortNodesByValue(){
            nodes.sort((o1, o2) -> {
                if (o1.nodeValue == o2.nodeValue) return 0;
                return o1.nodeValue < o2.nodeValue ? 1 : -1;
            });
        }
        static int listLength(){
            return nodes.size();
        }
        static Node lastNode(){
            return nodes.get(listLength()-1);
        }

        static Node root(){
            return nodes.get(0);
        }

        static void constructTree(){
            while(listLength() != 1){
                sortNodesByValue();
                Node treeNode = new Node();

                treeNode.leftNode = lastNode();
                nodes.remove(lastNode());
                treeNode.rightNode = lastNode();
                nodes.remove(lastNode());

                treeNode.nodeValue = treeNode.leftNode.nodeValue + treeNode.rightNode.nodeValue;
                treeNode.children.add(treeNode.leftNode);
                treeNode.children.add(treeNode.rightNode);

                treeNode.leftNode.parent = treeNode;
                treeNode.rightNode.parent = treeNode;

                treeNode.rightNode.pathValue = true;

                nodes.add(treeNode);
                sortNodesByValue();
            }
        }

        static Node DFS(Node node,List<Boolean> input){
            if(node == null)return null;
            if(node.children.size() == 0){
                if(node.codeword.equals(input)){
                    return node;
                }
            }
            Node left = DFS(node.leftNode,input);
            if(left != null)return left;

            return DFS(node.rightNode,input);
        }

        static List<Boolean> huffmanCode(Node node){
            List<Boolean> result = new ArrayList<>();
            Node currentNode = node;
            while(currentNode.parent != null){
                result.add(0,currentNode.pathValue);
                currentNode = currentNode.parent;
            }
            return result;
        }

        static Node navigate(Node node,boolean right){
            if(right) return node.rightNode;
            else return node.leftNode;
        }

        static void reset(){
            nodes = new ArrayList<>();
            originalNodes = new ArrayList<>();
        }

    }

    static class LZ{

        static final int SIZE = 256;

        List<Boolean> dictLoc;
        List<Boolean> content;
        List<Boolean> codeword;
        int codeWordDictIndex;

        public LZ() {
            this.dictLoc = new ArrayList<>();
            this.content = new ArrayList<>();
            this.codeword = new ArrayList<>();
            this.codeWordDictIndex = 0;
        }

        static byte[] compress(byte[] input, int radix){

            Huffman.reset();
            List<Boolean> inputToBits = bytesToBits(input);
            List<LZ> rows = new ArrayList<>();
            List<ArrayList<Boolean>> contentList = new ArrayList<>();
            List<List<Boolean>> uniqueBitStrings = new ArrayList<>();

            int dictIndex = 1;

            ArrayList<Boolean> tempBitList = new ArrayList<>();
            for (int i = 0; i < inputToBits.size(); i+=radix) {

                for (int j = 0; j < radix && i+j < inputToBits.size(); j++) {
                    tempBitList.add(inputToBits.get(i+j));
                }

                if(!contentList.contains(tempBitList)){
                    LZ row = new LZ();
                    row.content = tempBitList;
                    row.dictLoc.addAll(intToBits(dictIndex));


                    for (int j = 0; j < row.content.size()/radix; j++) {
                        List<Boolean> tempCharacterBooleanString = new ArrayList<>();
                        for (int k = 0; k < radix; k++) {
                            tempCharacterBooleanString.add(row.content.get(k + (radix*j)));
                        }
                        if(!uniqueBitStrings.contains(tempCharacterBooleanString)){
                            uniqueBitStrings.add(tempCharacterBooleanString);
                            Huffman.Node node = new Huffman.Node();
                            node.codeword = tempCharacterBooleanString;
                            node.nodeValue++;
                            Huffman.nodes.add(node);
                        }else{
                            for (int k = 0; k < Huffman.nodes.size(); k++) {
                                if(Huffman.nodes.get(k).codeword.equals(tempCharacterBooleanString)){
                                    Huffman.nodes.get(k).nodeValue += 1;
                                    break;
                                }
                            }
                        }
                    }


                    if(row.content.size() != radix){
                        ArrayList<Boolean> prefix = new ArrayList<>();
                        for (int j = 0; j < row.content.size()-radix; j++) {
                            prefix.add(row.content.get(j));
                        }
                        for (LZ checkRow: rows) {
                            if(checkRow.content.equals(prefix)){
                                row.codeword.addAll(checkRow.dictLoc);
                                row.codeWordDictIndex = checkRow.dictLoc.size();
                                break;
                            }
                        }
                    }


                    dictIndex++;
                    rows.add(row);
                    contentList.add(tempBitList);
                    tempBitList = new ArrayList<>();
                }

                System.out.print("Compressing [" + "#".repeat(i/(inputToBits.size()/10)) + " ".repeat(10-(i/(inputToBits.size()/10))) + "] " + String.format("%.2f%%",(((double)i/(double)inputToBits.size())*100)) + "\r");
            }
            //end

            Huffman.sortNodesByValue();
            Huffman.originalNodes.addAll(Huffman.nodes);
            Huffman.constructTree();

            System.out.println();


            //Adds to codeword after dictionary index added
            for (LZ row: rows) {
                List<Boolean> leastSignificantBits = new ArrayList<>();
                for (int j = radix; j > 0; j--) {
                    if(!(row.content.size() < radix)){
                        boolean leastSignificantBit = row.content.get(row.content.size()-j);
                        leastSignificantBits.add(leastSignificantBit);
                    }else{
                        leastSignificantBits.addAll(row.content);
                    }
                }
                if(row.content.size() % radix == 0){
                    row.codeword.addAll(Huffman.huffmanCode(Huffman.DFS(Huffman.root(),leastSignificantBits)));
                }else{
                    row.codeWordDictIndex = -1;
                    row.codeword.addAll(row.content);
                }
            }


            /*for (LZ row: rows) {
                System.out.printf("%16s %2s %64s %8s %64s",getBitString(row.dictLoc),"||",getBitString(row.content),"||",getBitString(row.codeword) + "           " + row.codeWordDictIndex + "\n");
            }*/

            List<Boolean> output = new ArrayList<>();

            output.addAll(intToBitsN(radix/8,4));

            output.addAll(intToBitsN(uniqueBitStrings.size(), radix));

            for (Huffman.Node node: Huffman.originalNodes) {
                output.addAll(node.codeword);
                output.addAll(intToBitsN(node.nodeValue,16));
            }

            //ADD COMPRESSED DATA INTO OUTPUT
            for (LZ row: rows) {
                output.addAll(bitStringLengthInBits(row.codeWordDictIndex+1));
                output.addAll(row.codeword);
            }

            System.out.println();
            for (List<Boolean> b: uniqueBitStrings) {
                System.out.println(getBitString(b));
            }
            System.out.println();

            //System.out.println(Huffman.originalNodes.get(0).nodeValue);
            /*System.out.println(uniqueBitStrings.size());
            System.out.println(getBitString(byteToBits((byte) uniqueBitStrings.size())));
            System.out.println(output.size()/8 + " B");
            System.out.println(getBitString(output));*/

            byte[] byteListOutput = new byte[(output.size()/8) + 1];
            for (int i = 0; i < byteListOutput.length; i++) {
                byte b = 0;
                for (int j = 0; j < 8; j++) {
                    if((i*8)+j < output.size() && output.get((i*8)+j)) b |= (128 >> j);
                }
                byteListOutput[i] = b;
            }

            return byteListOutput;


        }


        static void decompress(byte[] compressedBytes){
            Huffman.reset();
            List<Boolean> compressedBitString = bytesToBits(compressedBytes);
            List<LZ> rows = new ArrayList<>();
            System.out.println(getBitString(compressedBitString));

            int radix = bitsToInt(compressedBitString.subList(0,4))*8;
            int numberOfCharacters = bitsToInt(compressedBitString.subList(4,4+radix));
            int position = 4+radix;

            System.out.println(numberOfCharacters);
            for (int i = 0; i < numberOfCharacters; i++) {
                List<Boolean> characterBinaryString = compressedBitString.subList(position,position+radix);
                position += radix;
                int frequencyOfCharacter = bitsToInt(compressedBitString.subList(position,position+16));
                position += 16;

                Huffman.Node node = new Huffman.Node();
                node.codeword = characterBinaryString;
                node.nodeValue = frequencyOfCharacter;
                Huffman.nodes.add(node);
                Huffman.originalNodes.add(node);

                System.out.printf("%8s %4s",getBitString(Huffman.nodes.get(i).codeword),Huffman.nodes.get(i).nodeValue + "\n");
            }
            Huffman.constructTree();

            while(position < compressedBitString.size()){
                int stepsAheadHuffmancode = bitsToInt(compressedBitString.subList(position,position+5));
                if(stepsAheadHuffmancode != 1){
                    List<Boolean> dictReference = compressedBitString.subList(position+5,position+5+stepsAheadHuffmancode);
                }
                LZ row = new LZ();
                //TODO: Use navigate() method for huffman decoding
                position += 5 + stepsAheadHuffmancode;

            }


        }


    }

    static int bitsToInt(List<Boolean> bits){
        int b = 0;
        int check = 1 << bits.size()-1;
        for (int i = 0; i < bits.size(); i++) {
            if(bits.get(i)) b |= (check >> i);
        }
        return b;
    }
    static List<Boolean> bytesToBits(byte[] inputBytes){
        List<Boolean> resultBits = new ArrayList<>();

        for (byte Byte: inputBytes) {
            int value = Byte;
            for (int i = 0; i < 8; i++) {
                resultBits.add((value & 128) != 0);
                value <<= 1;
            }
        }

        return resultBits;
    }

    static List<Boolean> byteToBits(byte inputByte){
        List<Boolean> resultBits = new ArrayList<>();

        int value = inputByte;
        for (int i = 0; i < 8; i++) {
            resultBits.add((value & 128) != 0);
            value <<= 1;
        }
        return resultBits;
    }

    static List<Boolean> intToBits(int inputByte){
        List<Boolean> resultBits = new ArrayList<>();

        int value = inputByte;
        for (int i = 0; i < Integer.numberOfTrailingZeros(Integer.highestOneBit(inputByte)) + 1; i++) {
            resultBits.add((value & Integer.highestOneBit(inputByte)) != 0);
            value <<= 1;
        }

        return resultBits;
    }

    static List<Boolean> intToBitsN(int inputByte, int N){
        List<Boolean> resultBits = new ArrayList<>();

        int value = inputByte;
        long oneByte = 1;
        oneByte <<= N-1;
        for (int i = 0; i < N; i++) {
            resultBits.add((value & oneByte) != 0);
            value <<= 1;
        }
        return resultBits;
    }

    static List<Boolean> bitStringLengthInBits(int lengthOfBitString){
        List<Boolean> resultBits = new ArrayList<>();

        int value = lengthOfBitString;
        for (int i = 0; i < 5; i++) {
            resultBits.add((value & 16) != 0);
            value <<= 1;
        }

        return resultBits;
    }

    static byte booleanArrayToByte(List<Boolean> booleanArrayList){
        byte b = 0;
        for (int i = booleanArrayList.size(); i > 0; i--) {
            if(booleanArrayList.get(booleanArrayList.size()-i)) b |= (1<<i-1);
        }
        return b;
    }

    static String getBitString(boolean[] bits){
        StringBuilder returnString = new StringBuilder();
        for (boolean b: bits) {
            returnString.append(b ? "1":"0");
        }
        return String.valueOf(returnString);
    }
    static String getBitString(List<Boolean> bits){
        StringBuilder returnString = new StringBuilder();
        for (boolean b: bits) {
            returnString.append(b ? "1":"0");
        }
        return String.valueOf(returnString);
    }
    static byte booleanToByte(boolean[] array) {
        byte val = 0;
        for (boolean b : array) {
            val <<= 1;
            if (b)
                val |= 1;
        }
        return val;
    }

    static void writeToFile(byte[] byteInput, String filename){
        try {
            DataOutputStream outFile = new DataOutputStream((new FileOutputStream(filename)));

            for (byte b: byteInput) {
                outFile.writeByte(b);
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
        String compressedFileName = "Deflate.txt";
        //READ TEXT DATA FOR COMPRESSION
        String filename = "compressTest.txt";
        String contentFromFile = Files.readString(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + filename));


        System.out.println("--------------------");
        System.out.println("BEFORE COMPRESSION:");
        System.out.println("--------------------");
        //System.out.println(contentFromFile);
        byte[] textToBytes = contentFromFile.getBytes(StandardCharsets.UTF_8);


        System.out.println();

        List<Boolean> bits = bytesToBits(textToBytes);
        for (boolean b: bits) {
            System.out.print(b ? "1":"0");
        }
        System.out.println();
        System.out.println(bits.size()/8 + " B");
        System.out.println();

        byte[] compressedBytes = LZ.compress(textToBytes,8);
        writeToFile(compressedBytes, compressedFileName);
        System.out.println(compressedBytes.length + " B");

        byte[] compressedBytesFromFile = Files.readAllBytes(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + compressedFileName));
        List<Boolean> bitsCompressed = bytesToBits(compressedBytesFromFile);

        for (boolean b: bitsCompressed) {
            System.out.print(b ? "1":"0");
        }
        System.out.println();
        System.out.println(compressedBytes.equals(compressedBytes));

        LZ.decompress(compressedBytesFromFile);


        /*
        // COMPRESSION, RESULT WRITTEN TO FILE
        List<Integer> result = LZ.compress(textToBytes);
        writeToFile(result,compressedFileName);


        //READ COMPRESSED FILE FOR DECOMPRESSION
        List<Integer> intFromCompressedFile = readIntegersFromFile(filename);


        //DECOMPRESS COMPRESSED FILE DATA
        List<Integer> decompressed = LZ.decompress(intFromCompressedFile);


        //CONVERTING DECOMPRESSED DATA INTO CHARACTERS
        String intToString = "";
        for (int i: decompressed) {
            if(i > -61){
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


        //PRINTING OUT DECOMPRESSED RESULT AFTER CHARACTER CONVERSION
        System.out.println("\n\n--------------------");
        System.out.println("AFTER DECOMPRESSION:");
        System.out.println("--------------------");
        System.out.println(intToString);


        //COMPRESSION RESULTS PRINTED OUT
        System.out.println("\n\nOriginal size: " + textToBytes.length);
        System.out.println("Compressed size: " + result.size());
        System.out.println("Compress percentage achieved: " + String.format("%.2f%%",(1 - (double)result.size()/(double)textToBytes.length)*100));

        System.out.println("\nDecompressed size: " + decompressed.size() + "\n");
*/

    }


}
