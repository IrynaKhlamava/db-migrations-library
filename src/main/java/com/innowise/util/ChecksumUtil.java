package com.innowise.util;

import com.innowise.exception.ChecksumCalculationException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
/**
 * Provides a utility method to calculate a SHA-256 checksum for a given file.
 **/

public class ChecksumUtil {

    public static String calculateChecksum(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes;

            try (InputStream inputStream = ChecksumUtil.class.getClassLoader().getResourceAsStream(filePath)) {
                if (inputStream != null) {
                    fileBytes = inputStream.readAllBytes();
                } else {
                    fileBytes = Files.readAllBytes(Path.of(filePath));
                }
            }

            byte[] checksumBytes = digest.digest(fileBytes);
            StringBuilder checksum = new StringBuilder();
            for (byte b : checksumBytes) {
                checksum.append(String.format("%02x", b));
            }
            return checksum.toString();
        } catch (Exception e) {
            throw new ChecksumCalculationException("Failed to calculate checksum for file: " + filePath, e);
        }
    }
}