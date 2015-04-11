package com.eltiland.bl.utils;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * Utility class containing convenience methods for calculation of the hashes.
 * <p/>
 * <p/>
 * created on 10/5/11
 */
public final class HashesUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashesUtils.class);

    /**
     * Compute a SHA1 hash and return it in a BASE64-encoded string.
     *
     * @param text string to compute hash of
     * @return base64-encoded string of that hash.
     */
    public static String getSHA1inHex(String text) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");

            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();

            Hex hexCoder = new Hex();
            return new String(hexCoder.encode(sha1hash));

        } catch (Exception e) {
            LOGGER.error("Could not compute hash. Critical issue", e);
            throw new RuntimeException(e);
        }
    }
}
