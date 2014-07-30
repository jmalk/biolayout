package org.BioLayoutExpress3D.StaticLibraries;

/**
*
* Base64Converter is a final class containing only static methods for coverting a string to a Base64 encoded one.
* For details on BASE 64 see RFC 1421.
*
* @see org.BioLayoutExpress3D.Connections.Proxy
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class Base64Converter
{

    /**
    *  Variable to hold the base64 alphabet.
    */
    private static final char[] alphabet =
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', // 0 to 7
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', // 8 to 15
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', // 16 to 23
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', // 24 to 31
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', // 32 to 39
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 40 to 47
        'w', 'x', 'y', 'z', '0', '1', '2', '3', // 48 to 55
        '4', '5', '6', '7', '8', '9', '+', '/'  // 56 to 63
    };

    /**
    *  Encodes a given string to base64 encoding.
    */
    public static String encode(String string)
    {
        return encode( string.getBytes() );
    }

    /**
    *  Encodes a given byte[] array to base64 encoding.
    */
    public static String encode(byte[] octetString)
    {
        int bits24 = 0;
        int bits6 = 0;
        char[] out = new char[((octetString.length - 1) / 3 + 1) * 4];

        int outIndex = 0;
        int i = 0;

        while ( (i + 3) <= octetString.length )
        {
            // store the octets
            bits24 = ( octetString[i++] & 0xFF ) << 16;
            bits24 |= ( octetString[i++] & 0xFF ) << 8;
            bits24 |= ( octetString[i++] & 0xFF ) << 0;
            bits6 = ( bits24 & 0x00FC0000 ) >> 18;
            out[outIndex++] = alphabet[bits6];

            bits6 = ( bits24 & 0x0003F000 ) >> 12;
            out[outIndex++] = alphabet[bits6];

            bits6 = ( bits24 & 0x00000FC0 ) >> 6;
            out[outIndex++] = alphabet[ bits6];

            bits6 = ( bits24 & 0x0000003F );
            out[outIndex++] = alphabet[bits6];
        }

        if (octetString.length - i == 2)
        {
            // store the octets
            bits24 = (octetString[i] & 0xFF) << 16;
            bits24 |= (octetString[i+1] & 0xFF) << 8;

            bits6 = ( bits24 & 0x00FC0000) >> 18;
            out[outIndex++] = alphabet[bits6];

            bits6 = ( bits24 & 0x0003F000) >> 12;
            out[outIndex++] = alphabet[bits6];

            bits6 = ( bits24 & 0x00000FC0) >> 6;
            out[outIndex++] = alphabet[bits6];

            // padding
            out[outIndex++] = '=';
        }
        else if (octetString.length - i == 1)
        {
            // store the octets
            bits24 = ( octetString[i] & 0xFF ) << 16;

            bits6 = ( bits24 & 0x00FC0000) >> 18;
            out[outIndex++] = alphabet[bits6];

            bits6 = ( bits24 & 0x0003F000) >> 12;
            out[outIndex++] = alphabet[bits6];

            // padding
            out[outIndex++] = '=';
        }

        return new String(out);
    }


}