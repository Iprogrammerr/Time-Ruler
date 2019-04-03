package com.iprogrammerr.time.ruler.security;

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
        return new String(digest.digest(message.getBytes(StandardCharsets.UTF_8)));
    }
}
