package cn.jiiiiiin.vplus.security.utils;

/**
 * Created by jiiiiiin on 2017/9/12.
 */

public class Base64 {
    private static final int BASELENGTH = 255;
    private static final int LOOKUPLENGTH = 64;
    private static final int TWENTYFOURBITGROUP = 24;
    private static final int EIGHTBIT = 8;
    private static final int SIXTEENBIT = 16;
    private static final int SIXBIT = 6;
    private static final int FOURBYTE = 4;
    private static final int SIGN = -128;
    private static final char PAD = '=';
    private static final boolean fDebug = false;
    private static final byte[] base64Alphabet = new byte[255];
    private static final char[] lookUpBase64Alphabet = new char[64];

    static {
        int j1;
        for(j1 = 0; j1 < 255; ++j1) {
            base64Alphabet[j1] = -1;
        }

        for(j1 = 90; j1 >= 65; --j1) {
            base64Alphabet[j1] = (byte)(j1 - 65);
        }

        for(j1 = 122; j1 >= 97; --j1) {
            base64Alphabet[j1] = (byte)(j1 - 97 + 26);
        }

        for(j1 = 57; j1 >= 48; --j1) {
            base64Alphabet[j1] = (byte)(j1 - 48 + 52);
        }

        base64Alphabet[43] = 62;
        base64Alphabet[47] = 63;

        for(j1 = 0; j1 <= 25; ++j1) {
            lookUpBase64Alphabet[j1] = (char)(65 + j1);
        }

        j1 = 26;

        int l1;
        for(l1 = 0; j1 <= 51; ++l1) {
            lookUpBase64Alphabet[j1] = (char)(97 + l1);
            ++j1;
        }

        j1 = 52;

        for(l1 = 0; j1 <= 61; ++l1) {
            lookUpBase64Alphabet[j1] = (char)(48 + l1);
            ++j1;
        }

        lookUpBase64Alphabet[62] = 43;
        lookUpBase64Alphabet[63] = 47;
    }

    public Base64() {
    }

    protected static boolean isWhiteSpace(char c) {
        return c == 32 || c == 13 || c == 10 || c == 9;
    }

    protected static boolean isPad(char c) {
        return c == 61;
    }

    protected static boolean isData(char c) {
        return base64Alphabet[c] != -1;
    }

    protected static boolean isBase64(char c) {
        return isWhiteSpace(c) || isPad(c) || isData(c);
    }

    public static String encode(byte[] abyte0) {
        if(abyte0 == null) {
            return null;
        } else {
            int i = abyte0.length * 8;
            if(i == 0) {
                return "";
            } else {
                int j = i % 24;
                int k = i / 24;
                int l = j == 0?k:k + 1;
                int i1 = (l - 1) / 19 + 1;
                Object ac = null;
                char[] var25 = new char[l * 4 + i1];
                boolean flag = false;
                boolean flag1 = false;
                boolean flag2 = false;
                boolean flag3 = false;
                boolean flag4 = false;
                int j1 = 0;
                int k1 = 0;
                int l1 = 0;

                byte byte6;
                byte byte3;
                byte byte18;
                byte byte20;
                byte byte19;
                byte byte22;
                for(int byte10 = 0; byte10 < i1 - 1; ++byte10) {
                    for(int byte13 = 0; byte13 < 19; ++byte13) {
                        byte6 = abyte0[k1++];
                        byte3 = abyte0[k1++];
                        byte18 = abyte0[k1++];
                        byte20 = (byte)(byte3 & 15);
                        byte19 = (byte)(byte6 & 3);
                        byte22 = (byte6 & -128) != 0?(byte)(byte6 >> 2 ^ 192):(byte)(byte6 >> 2);
                        byte byte23 = (byte3 & -128) != 0?(byte)(byte3 >> 4 ^ 240):(byte)(byte3 >> 4);
                        byte byte24 = (byte18 & -128) != 0?(byte)(byte18 >> 6 ^ 252):(byte)(byte18 >> 6);
                        var25[j1++] = lookUpBase64Alphabet[byte22];
                        var25[j1++] = lookUpBase64Alphabet[byte23 | byte19 << 4];
                        var25[j1++] = lookUpBase64Alphabet[byte20 << 2 | byte24];
                        var25[j1++] = lookUpBase64Alphabet[byte18 & 63];
                        ++l1;
                    }

                    var25[j1++] = 10;
                }

                byte var26;
                byte var27;
                while(l1 < k) {
                    var26 = abyte0[k1++];
                    var27 = abyte0[k1++];
                    byte6 = abyte0[k1++];
                    byte3 = (byte)(var27 & 15);
                    byte18 = (byte)(var26 & 3);
                    byte20 = (var26 & -128) != 0?(byte)(var26 >> 2 ^ 192):(byte)(var26 >> 2);
                    byte19 = (var27 & -128) != 0?(byte)(var27 >> 4 ^ 240):(byte)(var27 >> 4);
                    byte22 = (byte6 & -128) != 0?(byte)(byte6 >> 6 ^ 252):(byte)(byte6 >> 6);
                    var25[j1++] = lookUpBase64Alphabet[byte20];
                    var25[j1++] = lookUpBase64Alphabet[byte19 | byte18 << 4];
                    var25[j1++] = lookUpBase64Alphabet[byte3 << 2 | byte22];
                    var25[j1++] = lookUpBase64Alphabet[byte6 & 63];
                    ++l1;
                }

                if(j == 8) {
                    var26 = abyte0[k1];
                    var27 = (byte)(var26 & 3);
                    byte6 = (var26 & -128) != 0?(byte)(var26 >> 2 ^ 192):(byte)(var26 >> 2);
                    var25[j1++] = lookUpBase64Alphabet[byte6];
                    var25[j1++] = lookUpBase64Alphabet[var27 << 4];
                    var25[j1++] = 61;
                    var25[j1++] = 61;
                } else if(j == 16) {
                    var26 = abyte0[k1];
                    var27 = abyte0[k1 + 1];
                    byte6 = (byte)(var27 & 15);
                    byte3 = (byte)(var26 & 3);
                    byte18 = (var26 & -128) != 0?(byte)(var26 >> 2 ^ 192):(byte)(var26 >> 2);
                    byte20 = (var27 & -128) != 0?(byte)(var27 >> 4 ^ 240):(byte)(var27 >> 4);
                    var25[j1++] = lookUpBase64Alphabet[byte18];
                    var25[j1++] = lookUpBase64Alphabet[byte20 | byte3 << 4];
                    var25[j1++] = lookUpBase64Alphabet[byte6 << 2];
                    var25[j1++] = 61;
                }

                var25[j1] = 10;
                return new String(var25);
            }
        }
    }

    public static byte[] decode(String s) {
        if(s == null) {
            return null;
        } else {
            char[] ac = s.toCharArray();
            int i = removeWhiteSpace(ac);
            if(i % 4 != 0) {
                return null;
            } else {
                int j = i / 4;
                if(j == 0) {
                    return new byte[0];
                } else {
                    Object abyte0 = null;
                    boolean byte0 = false;
                    boolean byte1 = false;
                    boolean flag = false;
                    boolean flag1 = false;
                    boolean flag2 = false;
                    boolean flag3 = false;
                    boolean c = false;
                    boolean c1 = false;
                    boolean c2 = false;
                    boolean c3 = false;
                    int k = 0;
                    int l = 0;
                    int i1 = 0;

                    byte byte4;
                    byte byte6;
                    byte[] var20;
                    byte var21;
                    byte var22;
                    char var23;
                    char var24;
                    char var25;
                    char var26;
                    for(var20 = new byte[j * 3]; k < j - 1; ++k) {
                        if(!isData(var23 = ac[i1++]) || !isData(var24 = ac[i1++]) || !isData(var25 = ac[i1++]) || !isData(var26 = ac[i1++])) {
                            return null;
                        }

                        var21 = base64Alphabet[var23];
                        var22 = base64Alphabet[var24];
                        byte4 = base64Alphabet[var25];
                        byte6 = base64Alphabet[var26];
                        var20[l++] = (byte)(var21 << 2 | var22 >> 4);
                        var20[l++] = (byte)((var22 & 15) << 4 | byte4 >> 2 & 15);
                        var20[l++] = (byte)(byte4 << 6 | byte6);
                    }

                    if(isData(var23 = ac[i1++]) && isData(var24 = ac[i1++])) {
                        var21 = base64Alphabet[var23];
                        var22 = base64Alphabet[var24];
                        var25 = ac[i1++];
                        var26 = ac[i1++];
                        if(isData(var25) && isData(var26)) {
                            byte4 = base64Alphabet[var25];
                            byte6 = base64Alphabet[var26];
                            var20[l++] = (byte)(var21 << 2 | var22 >> 4);
                            var20[l++] = (byte)((var22 & 15) << 4 | byte4 >> 2 & 15);
                            var20[l++] = (byte)(byte4 << 6 | byte6);
                            return var20;
                        } else if(isPad(var25) && isPad(var26)) {
                            if((var22 & 15) != 0) {
                                return null;
                            } else {
                                byte[] var27 = new byte[k * 3 + 1];
                                System.arraycopy(var20, 0, var27, 0, k * 3);
                                var27[l] = (byte)(var21 << 2 | var22 >> 4);
                                return var27;
                            }
                        } else if(!isPad(var25) && isPad(var26)) {
                            byte4 = base64Alphabet[var25];
                            if((byte4 & 3) != 0) {
                                return null;
                            } else {
                                byte[] var28 = new byte[k * 3 + 2];
                                System.arraycopy(var20, 0, var28, 0, k * 3);
                                var28[l++] = (byte)(var21 << 2 | var22 >> 4);
                                var28[l] = (byte)((var22 & 15) << 4 | byte4 >> 2 & 15);
                                return var28;
                            }
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    protected static int removeWhiteSpace(char[] ac) {
        if(ac == null) {
            return 0;
        } else {
            int i = 0;
            int j = ac.length;

            for(int k = 0; k < j; ++k) {
                if(!isWhiteSpace(ac[k])) {
                    ac[i++] = ac[k];
                }
            }

            return i;
        }
    }
}
