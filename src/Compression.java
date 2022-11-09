import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Compression {

    static class Huffman{
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

    static class LZ {
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

        static byte[] compress(byte[] input, int radix) {

            Huffman.reset();
            List<Boolean> inputToBits = bytesToBits(input);
            List<LZ> rows = new ArrayList<>();
            List<ArrayList<Boolean>> contentList = new ArrayList<>();
            List<List<Boolean>> uniqueBitStrings = new ArrayList<>();

            int dictIndex = 1;

            ArrayList<Boolean> tempBitList = new ArrayList<>();
            for (int i = 0; i < inputToBits.size(); i += radix) {

                for (int j = 0; j < radix && i + j < inputToBits.size(); j++) {
                    tempBitList.add(inputToBits.get(i + j));
                }

                if (!contentList.contains(tempBitList) || i >= inputToBits.size() - radix) {
                    LZ row = new LZ();
                    row.content = tempBitList;
                    row.dictLoc.addAll(intToBits(dictIndex));


                    for (int j = 0; j < row.content.size() / radix; j++) {
                        List<Boolean> tempCharacterBooleanString = new ArrayList<>();
                        for (int k = 0; k < radix; k++) {
                            tempCharacterBooleanString.add(row.content.get(k + (radix * j)));
                        }
                        if (!uniqueBitStrings.contains(tempCharacterBooleanString)) {
                            uniqueBitStrings.add(tempCharacterBooleanString);
                            Huffman.Node node = new Huffman.Node();
                            node.codeword = tempCharacterBooleanString;
                            node.nodeValue++;
                            Huffman.nodes.add(node);
                        } else {
                            for (int k = 0; k < Huffman.nodes.size(); k++) {
                                if (Huffman.nodes.get(k).codeword.equals(tempCharacterBooleanString)) {
                                    Huffman.nodes.get(k).nodeValue += 1;
                                    break;
                                }
                            }
                        }
                    }


                    dictIndex++;
                    rows.add(row);
                    contentList.add(tempBitList);
                    tempBitList = new ArrayList<>();
                }

                System.out.print("Compressing [" + "#".repeat(i / (inputToBits.size() / 10)) + " ".repeat(10 - (i / (inputToBits.size() / 10))) + "] " + String.format("%.2f%%", (((double) i / (double) inputToBits.size()) * 100)) + "\r");
            }
            //end

            Huffman.sortNodesByValue();
            Huffman.originalNodes.addAll(Huffman.nodes);
            Huffman.constructTree();

            System.out.println();


            //Adds to codeword after dictionary index added
            for (LZ row : rows) {

                if (row.content.size() != radix) {
                    ArrayList<Boolean> prefix = new ArrayList<>();
                    for (int j = 0; j < row.content.size() - radix; j++) {
                        prefix.add(row.content.get(j));
                    }

                    List<List<Boolean>> prefixDivided = divideListIntoList(prefix);
                    for (List<Boolean> l: prefixDivided) {
                        row.codeword.addAll(Huffman.huffmanCode(Huffman.DFS(Huffman.root(),l)));
                    }
                }


                List<Boolean> leastSignificantBits = new ArrayList<>();
                for (int j = radix; j > 0; j--) {
                    if (!(row.content.size() < radix)) {
                        boolean leastSignificantBit = row.content.get(row.content.size() - j);
                        leastSignificantBits.add(leastSignificantBit);
                    } else {
                        leastSignificantBits.addAll(row.content);
                    }
                }
                if (row.content.size() % radix == 0) {
                    row.codeword.addAll(Huffman.huffmanCode(Huffman.DFS(Huffman.root(), leastSignificantBits)));
                } else {
                    row.codeWordDictIndex = -1;
                    row.codeword.addAll(row.content);
                }
            }


            for (LZ row: rows) {
               System.out.printf("%16s %2s %64s %8s %64s",getBitString(row.dictLoc),"||",getBitString(row.content),"||",getBitString(row.codeword) + "           " + row.codeWordDictIndex + "\n");
            }

            List<Boolean> output = new ArrayList<>();

            output.addAll(intToBitsN(radix / 8, 4));

            output.addAll(intToBitsN(uniqueBitStrings.size(), radix));

            for (Huffman.Node node : Huffman.originalNodes) {
                output.addAll(node.codeword);
                output.addAll(intToBitsN(node.nodeValue, 16));
            }

            //ADD COMPRESSED DATA INTO OUTPUT
            for (LZ row : rows) {
                output.addAll(row.codeword);
            }

            byte[] byteListOutput = new byte[(output.size() / 8) + 1];
            divideListIntoBytes(output, byteListOutput);

            return byteListOutput;


        }


        static byte[] decompress(byte[] compressedBytes) {
            Huffman.reset();
            List<Boolean> compressedBitString = bytesToBits(compressedBytes);

            int radix = bitsToInt(compressedBitString.subList(0, 4)) * 8;
            int numberOfCharacters = bitsToInt(compressedBitString.subList(4, 4 + radix));
            int position = 4 + radix;


            for (int i = 0; i < numberOfCharacters; i++) {
                List<Boolean> characterBinaryString = compressedBitString.subList(position, position + radix);
                position += radix;
                int frequencyOfCharacter = bitsToInt(compressedBitString.subList(position, position + 16));
                position += 16;

                Huffman.Node node = new Huffman.Node();
                node.codeword = characterBinaryString;
                node.nodeValue = frequencyOfCharacter;
                Huffman.nodes.add(node);
                Huffman.originalNodes.add(node);

            }
            Huffman.constructTree();
            List<Boolean> output = new ArrayList<>();

            while (position < compressedBitString.size()) {

                Huffman.Node node = Huffman.root();
                while (node.children.size() != 0) {
                    if(position < compressedBitString.size()){
                        node = Huffman.navigate(node, compressedBitString.get(position));
                        position++;
                    }else{
                        break;
                    }
                }

                if(node.codeword != null){
                    output.addAll(node.codeword);
                }

                System.out.print("Decompressing [" + "#".repeat((int) (((double) position / (double) compressedBitString.size()) * 10)) + " ".repeat(10 - (int) (((double) position / (double) compressedBitString.size()) * 10)) + "] " + String.format("%.2f%%", (((double) position / (double) compressedBitString.size()) * 100)) + "\r");

            }

            byte[] byteListOutput = new byte[(output.size() / 8)];
            divideListIntoBytes(output, byteListOutput);
            return byteListOutput;


        }
    }

    static void divideListIntoBytes(List<Boolean> list, byte[] byteListOutput) {
        for (int i = 0; i < byteListOutput.length; i++) {
            byte b = 0;
            for (int j = 0; j < 8; j++) {
                if((i*8)+j < list.size() && list.get((i*8)+j)) b |= (128 >> j);
            }
            byteListOutput[i] = b;
        }
    }

    static List<List<Boolean>> divideListIntoList(List<Boolean> list){
        List<List<Boolean>> returnList = new ArrayList<>();
        List<Boolean> tempList = new ArrayList<>();
        for (boolean b: list) {
            tempList.add(b);
            if(tempList.size() == 8){
                returnList.add(tempList);
                tempList = new ArrayList<>();
            }
        }
        return returnList;
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

    static boolean listContainsOnlyFalse(List<Boolean> booleanList){
        for (boolean b: booleanList) {
            if(b)return false;
        }
        return true;
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
    static String getBitString(List<Boolean> bits){
        StringBuilder returnString = new StringBuilder();
        for (boolean b: bits) {
            returnString.append(b ? "1":"0");
        }
        return String.valueOf(returnString);
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


    public static void main(String[] args) throws IOException {
        String compressedFileName = "Compressed_LZ.txt";

        //READ TEXT DATA FOR COMPRESSION
        String filename = "diverse.txt";
        String contentFromFile = Files.readString(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + filename));

        byte[] textToBytes = contentFromFile.getBytes(StandardCharsets.UTF_8);

        System.out.println(getBitString(bytesToBits(textToBytes)));


        //COMPRESS DATA AND WRITE TO FILE
        byte[] compressedBytes = LZ.compress(textToBytes,8);
        writeToFile(compressedBytes, compressedFileName);

        //RETRIEVE BYTES FROM COMPRESSED FILE
        byte[] compressedBytesFromFile = Files.readAllBytes(Path.of(System.getProperty("user.dir") + System.getProperty("file.separator") + compressedFileName));

        //DECOMPRESS COMPRESSED DATA
        byte[] decompressedBytes = LZ.decompress(compressedBytesFromFile);
        System.out.println(getBitString(bytesToBits(decompressedBytes)));

        //COMPRESSION RESULTS PRINTED OUT
        System.out.format("%-32s %s %2s %s","\n\n\n+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %16s","|Original size","|",textToBytes.length,"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %16s","|Compressed size","|",compressedBytes.length,"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %15s","|Compress percentage achieved","|",String.format("%.2f%%",(1 - (double)compressedBytes.length/(double)textToBytes.length)*100),"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %16s","|Decompressed size","|",decompressedBytes.length,"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");
        System.out.format("%-32s %2s %2s %17s","|Decompressed same as original","|", Arrays.equals(decompressedBytes, textToBytes),"|\n");
        System.out.format("%-32s %s %2s %s","+ " + "-".repeat(31),"+","-".repeat(19),"+\n");


    }


}
