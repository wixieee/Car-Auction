package edu.lpnu.auction.factory;

import edu.lpnu.auction.dto.LoginRequest;
import edu.lpnu.auction.dto.RegisterRequest;

public class AuthDtoFactory {

    private static RegisterRequest getRegisterRequest(String password, String confirmPassword) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(TestConstants.DEFAULT_EMAIL);
        request.setPassword(password != null ? password : TestConstants.DEFAULT_PASSWORD);
        request.setPasswordConfirm(confirmPassword != null ? confirmPassword : TestConstants.DEFAULT_PASSWORD);
        request.setFirstName(TestConstants.DEFAULT_FIRSTNAME);
        request.setLastName(TestConstants.DEFAULT_LASTNAME);
        return request;
    }

    public static RegisterRequest getValidRegisterRequest() {
        return getRegisterRequest(null, null);
    }

    public static RegisterRequest getMismatchRegisterRequest() {
        return getRegisterRequest(TestConstants.DEFAULT_PASSWORD, "wrongPass");
    }

    public static LoginRequest getValidLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail(TestConstants.DEFAULT_EMAIL);
        request.setPassword(TestConstants.DEFAULT_PASSWORD);
        return request;
    }
}
