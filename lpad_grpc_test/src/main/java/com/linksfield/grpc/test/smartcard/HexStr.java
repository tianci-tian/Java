package com.linksfield.grpc.test.smartcard;

import java.util.Base64;

public class HexStr {
    private static final char[] kHexChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String ascToHex(String s) {
        byte[] stringBytes = s.getBytes();
        return HexStr.bufferToHex(stringBytes);
    }

    public static String hexToASC(String hexString) throws NumberFormatException {
        byte[] bytes = HexStr.hexToBuffer(hexString);
        return new String(bytes);
    }

    public static String bufferToHex(byte[] buffer) {
        return HexStr.bufferToHex(buffer, 0, buffer.length).toUpperCase();
    }

    public static String bufferToHex(byte[] buffer, int startOffset, int length) {
        StringBuffer hexString = new StringBuffer(2 * length);
        int endOffset = startOffset + length;
        for (int i = startOffset; i < endOffset; ++i) {
            HexStr.appendHexPair(buffer[i], hexString);
        }
        return hexString.toString().toUpperCase();
    }

    public static byte[] hexToBuffer(String hexString) throws NumberFormatException {
        int length = hexString.length();
        if (length % 2 != 0) {
            throw new NumberFormatException("Invalid hex digit len" + length + "\n" + hexString);
        }
        byte[] buffer = new byte[(length + 1) / 2];
        boolean evenByte = true;
        byte nextByte = 0;
        int bufferOffset = 0;
        if (length % 2 == Integer.valueOf(1)) {
            evenByte = false;
        }
        for (int i = 0; i < length; ++i) {
            int nibble;
            char c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                nibble = c - 48;
            } else if (c >= 'A' && c <= 'F') {
                nibble = c - 65 + 10;
            } else if (c >= 'a' && c <= 'f') {
                nibble = c - 97 + 10;
            } else {
                throw new NumberFormatException("Invalid hex digit '" + c + "'.");
            }
            if (evenByte) {
                nextByte = (byte)(nibble << 4);
            } else {
                nextByte = (byte)(nextByte + (byte)nibble);
                buffer[bufferOffset++] = nextByte;
            }
            evenByte = !evenByte;
        }
        return buffer;
    }

    private static void appendHexPair(byte b, StringBuffer hexString) {
        char highNibble = kHexChars[(b & 0xF0) >> 4];
        char lowNibble = kHexChars[b & 0xF];
        hexString.append(highNibble);
        hexString.append(lowNibble);
    }

    public static String hexToBinary(String hex) {
        hex = hex.toUpperCase();
        String result = "";
        int max = hex.length();
        block18 : for (int i = 0; i < max; ++i) {
            char c = hex.charAt(i);
            switch (c) {
                case '0': {
                    result = String.valueOf(result) + "0000";
                    continue block18;
                }
                case '1': {
                    result = String.valueOf(result) + "0001";
                    continue block18;
                }
                case '2': {
                    result = String.valueOf(result) + "0010";
                    continue block18;
                }
                case '3': {
                    result = String.valueOf(result) + "0011";
                    continue block18;
                }
                case '4': {
                    result = String.valueOf(result) + "0100";
                    continue block18;
                }
                case '5': {
                    result = String.valueOf(result) + "0101";
                    continue block18;
                }
                case '6': {
                    result = String.valueOf(result) + "0110";
                    continue block18;
                }
                case '7': {
                    result = String.valueOf(result) + "0111";
                    continue block18;
                }
                case '8': {
                    result = String.valueOf(result) + "1000";
                    continue block18;
                }
                case '9': {
                    result = String.valueOf(result) + "1001";
                    continue block18;
                }
                case 'A': {
                    result = String.valueOf(result) + "1010";
                    continue block18;
                }
                case 'B': {
                    result = String.valueOf(result) + "1011";
                    continue block18;
                }
                case 'C': {
                    result = String.valueOf(result) + "1100";
                    continue block18;
                }
                case 'D': {
                    result = String.valueOf(result) + "1101";
                    continue block18;
                }
                case 'E': {
                    result = String.valueOf(result) + "1110";
                    continue block18;
                }
                case 'F': {
                    result = String.valueOf(result) + "1111";
                }
            }
        }
        return result;
    }

}

