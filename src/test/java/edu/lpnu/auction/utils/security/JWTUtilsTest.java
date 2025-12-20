package edu.lpnu.auction.utils.security;

import edu.lpnu.auction.factory.JwtTestHelper;
import edu.lpnu.auction.factory.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JWTUtilsTest {

    private JWTUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JWTUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", TestConstants.CORRECT_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", TestConstants.EXPIRATION_TIME);

        jwtUtils.init();
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TestConstants.DEFAULT_EMAIL);

        String token = jwtUtils.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals(TestConstants.DEFAULT_EMAIL, jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void validateToken_WhenExpired_ShouldReturnFalse() {
        String expiredToken = JwtTestHelper.generateExpiredToken();

        assertFalse(jwtUtils.validateToken(expiredToken));
    }

    @Test
    void validateToken_WhenWrongSignature_ShouldReturnFalse() {
        String forgedToken = JwtTestHelper.generateTokenWithWrongSignature();

        assertFalse(jwtUtils.validateToken(forgedToken));
    }

    @Test
    void validateToken_WhenMalformed_ShouldReturnFalse() {
        assertFalse(jwtUtils.validateToken("not.a.valid.token"));
        assertFalse(jwtUtils.validateToken(""));
    }
}