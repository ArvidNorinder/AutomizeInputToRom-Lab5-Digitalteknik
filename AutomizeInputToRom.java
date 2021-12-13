import java.util.Map;

import static java.util.Map.entry;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class AutomizeInputToRom {
    static Map<String, String> opCodeMap = Map.ofEntries(
        entry("Call", "0000"),
        entry("Ret", "0001"),
        entry("Bz", "0010"),
        entry("B", "0011"),
        entry("Add", "0100"),
        entry("Sub", "0101"),
        entry("Ld", "0110"),
        entry("In", "0111"),
        entry("Out", "1000"),
        entry("And", "1001"),
        entry("Dout", "1010")
    );

    public static void main(String[] args) throws IOException {
        assemble();
    }

    private static void assemble() throws IOException {
        File outputFile = new File(".\\inputToRom.hex");
        File inputFile = new File(".\\readFrom.txt");

        RandomAccessFile writer = new RandomAccessFile(outputFile, "rw");
        RandomAccessFile reader = new RandomAccessFile(inputFile, "r");

        //clearar varje gång programmet körs
        writer.setLength(0);

        System.out.println("Now reading \nLine 1");
        for(int count = 0; count < 64; count ++) {
            String input = reader.readLine();
            try {
                    if(input == null) {
                        input = "Call 0 0";
                    }
                    String[] splitInput = input.split(" ");
                    String opCode = splitInput[0];
                    String Rx = splitInput[1];
                    String Data = splitInput[2];

                    opCode = opCodeMap.get(opCode);

                    //gör att jag kan ge data i form av en integer också om jag vill
                    Data = padWithZeroes(integerToBinary(Data), 8);

                    String payload = opCode + Rx + Data;
                    String hex = binaryToHexadecimal(payload);
                    String paddedHex = padWithZeroes(hex, 4);
                    paddedHex = paddedHex.toUpperCase();
                    writer.writeBytes(paddedHex + ";\n");

                } catch (Exception e) {
                    System.out.println(e);
                }
            System.out.println("Line " + count);
        }
        reader.close();
        writer.close();
    }

    private static String integerToBinary(String integer) {
        if (integer.length() == 8) {
            return integer;
        }
        return Integer.toBinaryString(Integer.parseInt(integer));
    }

    private static String binaryToHexadecimal(String binary) {
        return Integer.toHexString(Integer.parseInt(binary, 2));
    }

    private static String padWithZeroes(String num, int size) {
        int forNum = size - num.length();
        String padding = "";
        for(int x = 0; x < forNum; x++) {
            padding = padding + "0";
        }
        return padding + num;
    }
}