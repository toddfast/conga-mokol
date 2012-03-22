package com.conga.tools.mokol.util;

/**
 *
 * 
 * @author Todd Fast
 */
public class ByteArrayUtil {

	/**
	 * Can't instantiate
	 *
	 */
	private ByteArrayUtil() {
		super();
	}


	/**
	 * Encode a byte array as a hexadecimal string
	 *
	 * @param	bytes
	 * @return
	 */
	public static String toHex(byte[] bytes) {
		StringBuilder hexString=new StringBuilder();
		for (int i=0; i<bytes.length; i++) {

			String hex=Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}

			hexString.append(hex);
		}

		return hexString.toString();
	}


    /**
     * Decode hexadecimal-encoded string and return raw byte array.
     * Important note: This method preserves leading 0 filled bytes on the
     * conversion process, which is important for cryptographic operations
     * in dealing with things like keys, initialization vectors, etc. For
     * example, the string "0x0000face" is going to return a byte array
     * whose length is 4, not 2.
     *
     * @param	hex
	 *			Hexadecimal-encoded string, with or without leading "0x".
     * @return	The equivalent byte array.
     */
    public static byte[] fromHex(String hex) {

		if (hex==null || hex.trim().isEmpty()) {
			throw new IllegalArgumentException("Parameter \"hex\" cannot be "+
				"null or an empty string");
		}

		if (hex.length() % 2==1) {
			throw new IllegalArgumentException(
				"Invalid hex string \""+hex+"\" (not enough bytes)");
		}

        if (hex.startsWith("0x")) {
            hex=hex.substring(2);
        }

        int length=hex.length()/2;
        byte[] result=new byte[length];
        for (int i=0; i<length; i++) {
            String pair=hex.substring(i*2,(i*2)+2);
            result[i]=(byte)(Integer.parseInt(pair,16));
        }

        return result;
    }
}
