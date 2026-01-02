package edu.lpnu.auction.factory;

import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.AuthProvider;
import edu.lpnu.auction.model.enums.Role;
import edu.lpnu.auction.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

public class UserFactory {

    private static User getUser(AuthProvider authProvider) {
        User user = new User();
        user.setId(TestConstants.DEFAULT_UUID);
        user.setFirstName(TestConstants.DEFAULT_FIRSTNAME);
        user.setLastName(TestConstants.DEFAULT_LASTNAME);
        user.setEmail(TestConstants.DEFAULT_EMAIL);
        user.setPassword(TestConstants.DEFAULT_HASHED_PASSWORD);
        user.setRoles(new HashSet<>(Collections.singleton(Role.ROLE_USER)));
        user.setProvider(authProvider == null ? AuthProvider.LOCAL : authProvider);
        user.setProviderId("providerId");
        return user;
    }

    public static User getLocalUser() {
        return getUser(AuthProvider.LOCAL);
    }

    public static User getGoogleUser() {
        return getUser(AuthProvider.GOOGLE);
    }

    public static User getRawMappedUser() {
        User user = new User();
        user.setEmail(TestConstants.DEFAULT_EMAIL);
        user.setFirstName(TestConstants.DEFAULT_FIRSTNAME);
        user.setLastName(TestConstants.DEFAULT_LASTNAME);
        return user;
    }

    public static User createPersistedUser(UserRepository repository, String emailPrefix, BigDecimal balance) {
        User user = getLocalUser();
        user.setId(null);

        user.setEmail(emailPrefix + "_" + UUID.randomUUID().toString().substring(0, 5) + "@test.com");

        user.setBalance(balance);
        user.setFrozenBalance(BigDecimal.ZERO);

        return repository.save(user);
    }
}
