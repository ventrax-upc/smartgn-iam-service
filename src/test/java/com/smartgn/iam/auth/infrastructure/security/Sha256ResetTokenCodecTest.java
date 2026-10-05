package com.smartgn.iam.auth.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Sha256ResetTokenCodecTest {

    private final Sha256ResetTokenCodec codec = new Sha256ResetTokenCodec();

    @Test
    void generaTokensDistintosYSeguros() {
        String a = codec.generarToken();
        String b = codec.generarToken();

        assertNotEquals(a, b);
        assertTrue(a.length() >= 43);
        assertTrue(a.matches("[A-Za-z0-9_-]+"));
    }

    @Test
    void elHashEsDeterministaYDe64Hex() {
        String hash = codec.hashear("TOKEN");

        assertEquals(hash, codec.hashear("TOKEN"));
        assertEquals(64, hash.length());
        assertNotEquals(hash, codec.hashear("OTRO"));
    }
}
