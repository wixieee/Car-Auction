package edu.lpnu.auction.factory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtTestHelper {

    public static String generateExpiredToken() {
        return buildToken(
                TestConstants.CORRECT_SECRET,
                -10000
        );
    }

    public static String generateTokenWithWrongSignature() {
        return buildToken(
                TestConstants.WRONG_SECRET,
                TestConstants.EXPIRATION_TIME
        );
    }

    public static String generateValidToken() {
        return buildToken(
                TestConstants.CORRECT_SECRET,
                TestConstants.EXPIRATION_TIME
        );
    }


    private static String buildToken(String base64Secret, long expirationOffsetMs) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(TestConstants.DEFAULT_EMAIL)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationOffsetMs))
                .signWith(key)
                .compact();
    }
}