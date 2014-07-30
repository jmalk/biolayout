package org.BioLayoutExpress3D.StaticLibraries;

import java.nio.charset.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* SymmetricCipher is a final class containing only static method(s) to be used for encrypting/decrypting a given string.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class SymmetricCipher
{

    /**
    *  Initialization vector used for the encryption/decryption process.
    */
    private static final byte[] INITIALIZATION_VECTOR = { 0x0a, 0x01, 0x02, 0x03, 0x04, 0x0b, 0x0c, 0x0d };

    /**
    *  Regular expression used for the encryption/decryption process.
    */
    private static final String REGEX = "#";

    /**
    *  Type of encryption/decryption algorithm used.
    */
    private static final String XFORM = "DESEDE/CBC/PKCS5Padding";

    /**
    *  Key size used for the encryption/decryption process.
    */
    private static final int KEY_SIZE = 168;

    /**
    *  SecureRandom reference used for the encryption/decryption process.
    */
    private static SecureRandom sr = null;

    /**
    *  KeyGenerator reference used for the encryption/decryption process.
    */
    private static KeyGenerator kg = null;

    /**
    *  SecretKey reference used for the encryption/decryption process.
    */
    private static SecretKey key = null;

    /**
    *  Static initializer to initialize all relevant encryption/decryption process references.
    */
    static
    {
        try
        {
            sr = new SecureRandom( "seed".getBytes(  Charset.defaultCharset()  ) ); // could choose another charset
            kg = KeyGenerator.getInstance("DESEDE");
            kg.init(KEY_SIZE, sr);
            key  = kg.generateKey();
        }
        catch(Exception exc)
        {
            if (DEBUG_BUILD) println("Exception in static SymmetricCipher initializer: " + exc.getMessage());
        }
    }

    /**
    *  Encrypts a given byte array and returns it. Internal method.
    */
    private static byte[] encrypt(byte[] inputBytes) throws Exception
    {
        Cipher cipher = Cipher.getInstance(XFORM);
        IvParameterSpec ips = new IvParameterSpec(INITIALIZATION_VECTOR);
        cipher.init(Cipher.ENCRYPT_MODE, key, ips);

        return cipher.doFinal(inputBytes);
    }

    /**
    *  Decrypts a given byte array and returns it. Internal method.
    */
    private static byte[] decrypt(byte[] inputBytes) throws Exception
    {
        Cipher cipher = Cipher.getInstance(XFORM);
        IvParameterSpec ips = new IvParameterSpec(INITIALIZATION_VECTOR);
        cipher.init(Cipher.DECRYPT_MODE, key, ips);

        return cipher.doFinal(inputBytes);
    }

    /**
    *  Encrypts a given string and returns it.
    */
    public static String encryptString(String stringToEncrypt)
    {
        try
        {
            if (!IS_MAC) // encryption seems to not work for MacOSX
            {
                byte[] stringToEncryptBytes = stringToEncrypt.getBytes( Charset.defaultCharset() ); // could choose another charset
                byte[] stringEncryptedBytes = encrypt(stringToEncryptBytes);
                String returnString = "";
                for (byte b : stringEncryptedBytes)
                   returnString += b + REGEX;

                return returnString.substring(0, returnString.length() - 1);
            }
            else
                return stringToEncrypt;
        }
        catch(Exception exc)
        {
            if (DEBUG_BUILD) println("Exception in SymmetricCipher.encryptString(): " + exc.getMessage());
        }

        return stringToEncrypt;
    }

    /**
    *  Encrypts a given string and returns it.
    */
    public static String decryptString(String stringToDecrypt)
    {
        try
        {
            if (!IS_MAC) // encryption seems to not work for MacOSX
            {
                String[] results = stringToDecrypt.split(REGEX);
                byte[] stringToDecryptBytes = new byte[results.length];
                for (int i = 0; i < stringToDecryptBytes.length; i++)
                    stringToDecryptBytes[i] = Byte.parseByte(results[i]);

                return new String( decrypt(stringToDecryptBytes), Charset.defaultCharset() );
            }
            else
                return stringToDecrypt;
        }
        catch(Exception exc)
        {
            if (DEBUG_BUILD) println("Exception in SymmetricCipher.decryptString(): " + exc.getMessage());
        }

        return stringToDecrypt;
    }


}