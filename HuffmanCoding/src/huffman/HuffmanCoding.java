package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
        sortedCharFreqList = new ArrayList<CharFreq>();
        int[] a = new int[128];
        int c = 0;
        while(StdIn.hasNextChar() != false){
            a[StdIn.readChar()] += 1;
            c++;
        }
        for(int i = 0; i < a.length; i++){
            if(a[i] == 0){

            }else{
                sortedCharFreqList.add(new CharFreq((char)i, (double)a[i]/c));
            }
        }
        if(sortedCharFreqList.size() == 1){
            char b = (char)((int)sortedCharFreqList.get(0).getCharacter()+1);
            sortedCharFreqList.add(new CharFreq(b,0));
        }
        Collections.sort(sortedCharFreqList);
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() 
    {
        Queue<CharFreq> sourceQueue = new Queue<CharFreq>();
        Queue<TreeNode> targetQueue = new Queue<TreeNode>();
        TreeNode thing1 = new TreeNode();
        TreeNode thing2 = new TreeNode();
        TreeNode thing3 = new TreeNode();
        for(int i = 0; i < sortedCharFreqList.size(); i++){
            sourceQueue.enqueue(sortedCharFreqList.get(i));
        }
        while((sourceQueue.isEmpty() != true) || (targetQueue.size() != 1)){
            thing1 = new TreeNode();
            thing2 = new TreeNode();
            if(!sourceQueue.isEmpty() && !targetQueue.isEmpty()){
                if((sourceQueue.peek().getProbOcc() < targetQueue.peek().getData().getProbOcc()) || (sourceQueue.peek().getProbOcc() == targetQueue.peek().getData().getProbOcc())){
                    thing1.setData(sourceQueue.dequeue());
                    thing1.setLeft(null);
                    thing1.setRight(null);
                    if(!sourceQueue.isEmpty()){
                        if((sourceQueue.peek().getProbOcc() < targetQueue.peek().getData().getProbOcc()) || (sourceQueue.peek().getProbOcc() == targetQueue.peek().getData().getProbOcc())){
                            thing2.setData(sourceQueue.dequeue());
                            thing2.setLeft(null);
                            thing2.setRight(null);
                        }
                        else if(sourceQueue.peek().getProbOcc() > targetQueue.peek().getData().getProbOcc()){
                            thing2 = targetQueue.dequeue();
                        }
                    }
                    else{
                        thing2 = targetQueue.dequeue();
                    }
                }
                else if(sourceQueue.peek().getProbOcc() > targetQueue.peek().getData().getProbOcc()){
                    thing1 = targetQueue.dequeue();
                    if(!targetQueue.isEmpty()){
                        if(((sourceQueue.peek().getProbOcc() < targetQueue.peek().getData().getProbOcc())) || (sourceQueue.peek().getProbOcc() == targetQueue.peek().getData().getProbOcc())){
                            thing2.setData(sourceQueue.dequeue());
                            thing2.setLeft(null);
                            thing2.setRight(null);
                        }
                        else if(sourceQueue.peek().getProbOcc() > targetQueue.peek().getData().getProbOcc()){
                            thing2 = targetQueue.dequeue();
                        }
                    }
                    else{
                        thing2.setData(sourceQueue.dequeue());
                        thing2.setLeft(null);
                        thing2.setRight(null);
                    }   
                }
            }
            else{
                if(targetQueue.isEmpty() && !sourceQueue.isEmpty()){
                    thing1.setData(sourceQueue.dequeue());
                    thing1.setLeft(null);
                    thing1.setRight(null);
                    thing2.setData(sourceQueue.dequeue());
                    thing2.setLeft(null);
                    thing2.setRight(null);
                }
                else if(sourceQueue.isEmpty() && !targetQueue.isEmpty()){
                    thing1 = targetQueue.dequeue();
                    thing2 = targetQueue.dequeue();
                }
            }   
            CharFreq temp = new CharFreq(null, thing1.getData().getProbOcc() + thing2.getData().getProbOcc());
            thing3 = new TreeNode(temp, thing1, thing2);
            targetQueue.enqueue(thing3);
        }
        huffmanRoot = targetQueue.dequeue();
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array. This method is currently fucked.
     * It's been six hours since I wrote the previous sentence and this is still true.
     * Why is there an infinite loop error??
     */
    private void makeEncodings(TreeNode track, String temp){ 
        if (track.getData().getCharacter() != null){encodings[(int) track.getData().getCharacter()] = temp; return;}
        makeEncodings(track.getLeft(), temp + "0");
        makeEncodings(track.getRight(), temp + "1");
    }
    public void makeEncodings() { 
    encodings = new String[128];
    TreeNode track = huffmanRoot;
    String temp = "";
    makeEncodings(track, temp);
    }
    
    
    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String s = "";
        while(StdIn.hasNextChar()){
            s = s + encodings[(int)StdIn.readChar()];
        }
        writeBitString(encodedFile, s);
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
        String s = readBitString(encodedFile);
        TreeNode temp = huffmanRoot;
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == '0'){
                temp = temp.getLeft();
                if(temp.getData().getCharacter() != null){
                    StdOut.print(temp.getData().getCharacter());
                    temp = huffmanRoot;
                }
            }
            else if(s.charAt(i) == '1'){
                temp = temp.getRight();
                if(temp.getData().getCharacter() != null){
                    StdOut.print(temp.getData().getCharacter());
                    temp = huffmanRoot;
                }
            }
        }

	/* Your code goes here */
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
