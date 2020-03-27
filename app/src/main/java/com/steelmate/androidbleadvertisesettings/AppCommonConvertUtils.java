package com.steelmate.androidbleadvertisesettings;

/**
 * created by XuTi on 2019/4/29 16:08
 */
public class AppCommonConvertUtils {
    private static final char hexDigits[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Bytes to chars.
     *
     * @param bytes The bytes.
     * @return chars
     */
    public static char[] bytes2Chars(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        if (len <= 0) {
            return null;
        }
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }
        return chars;
    }

    /**
     * Chars to bytes.
     *
     * @param chars The chars.
     * @return bytes
     */
    public static byte[] chars2Bytes(final char[] chars) {
        if (chars == null || chars.length <= 0) {
            return null;
        }
        int    len   = chars.length;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (chars[i]);
        }
        return bytes;
    }

    /**
     * Hex string to bytes.
     * <p>e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }</p>
     *
     * @param hexString The hex string.
     * @return the bytes
     */
    public static byte[] hexString2Bytes(String hexString) {
        if (isSpace(hexString)) {
            return null;
        }
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len = len + 1;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret      = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Int(hexBytes[i]) << 4 | hex2Int(hexBytes[i + 1]));
        }
        return ret;
    }

    private static int hex2Int(final char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            return hexChar - 'A' + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Bytes to hex string.
     * <p>e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns "00A8"</p>
     *
     * @param bytes The bytes.
     * @return hex string
     */
    public static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        int len = bytes.length;
        if (len <= 0) {
            return "";
        }
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    public static String bytes2String(byte[] bytes) {
        char[] chars = bytes2Chars(bytes);
        return String.valueOf(chars);
    }

    /**
     * 字符串转成十六进制字符串
     *
     * @param string
     * @return
     */
    public static String string2HexString(String string) {
        if (string == null) {
            return "";
        }
        byte[] bytes = chars2Bytes(string.toCharArray());
        return bytes2HexString(bytes);
    }

    public static String getBytes2hex(String localName) {
        if (localName == null) {
            return "";
        }
        return bytes2HexString(localName.getBytes());
    }

    public byte getRightShiftByte(long value, int offsetNumber) {
        return (byte) ((value >> (offsetNumber * 8)) & 0xff);
    }

    /**
     * @param bytes 长度小于等于4
     * @return
     */
    public static int byteArrayToIntWithNegativeNumber(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (i == 0) {
                value = (value | bytes[i]);
            } else {
                value = (value << (i * 8)) | (bytes[i] & 0xff);
            }
        }
        return value;
    }

    public static int byteArrayToIntNoNegativeNumber(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << (i * 8)) | (bytes[i] & 0xff);
        }
        return value;
    }

    public static int hexString2IntValue(String hexString) {
        return byteArrayToIntNoNegativeNumber(hexString2Bytes(hexString));
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    public static byte[] longToByteArray(long value) {
        return new byte[]{
                (byte) ((value >> (7 * 8)) & 0xFF),
                (byte) ((value >> (6 * 8)) & 0xFF),
                (byte) ((value >> (5 * 8)) & 0xFF),
                (byte) ((value >> (4 * 8)) & 0xFF),
                (byte) ((value >> (3 * 8)) & 0xFF),
                (byte) ((value >> (2 * 8)) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    /**
     * @param number    数字
     * @param byteCount 字节个数
     * @return
     */
    public static String numberToHex(Number number, int byteCount) {
        if (number == null) {
            return "";
        }
        //2表示需要两个16进制位
        return String.format("%0" + byteCount * 2 + "x", number);
    }

    /**
     * 使用1字节就可以表示b
     * hex8表示8位二进制
     *
     * @param b
     * @return
     */
    public static String intToHex8(Integer b) {
        if (b == null) {
            return "";
        }
        //2表示需要两个16进行数
        return String.format("%02x", b);
    }

    /**
     * 需要使用2字节表示b
     * hex16表示16位二进制
     *
     * @param b
     * @return
     */
    public static String intToHex16(Integer b) {
        if (b == null) {
            return "";
        }
        return String.format("%04x", b);
    }

    /**
     * 需要使用4字节表示b
     * hex32表示32位二进制
     *
     * @param b
     * @return
     */
    public static String intToHex32(Integer b) {
        if (b == null) {
            return "";
        }
        return String.format("%08x", b);
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
