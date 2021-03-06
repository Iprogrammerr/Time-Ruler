package com.iprogrammerr.time.ruler.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hashing {

    private final MessageDigest digest;

    public Hashing(MessageDigest digest) {
        this.digest = digest;
    }

    public Hashing() throws Exception {
        this(MessageDigest.getInstance("SHA-256"));
    }

    public String hash(String message) {
        return bytesToHex(digest.digest(message.getBytes(StandardCharsets.UTF_8)));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String hash(String message, String... messages) {
        StringBuilder builder = new StringBuilder(message);
        for (String m : messages) {
            builder.append("&").append(m);
        }
        return hash(builder.toString());
    }
}
