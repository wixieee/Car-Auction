package edu.lpnu.auction.utils.security;

import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.AuthProvider;
import edu.lpnu.auction.model.enums.Role;
import edu.lpnu.auction.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtils jwtUtils;
    private final UserRepository userRepository;

    @Value("${OAUTH_REDIRECT_URL}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        User user = userRepository.findByProviderId(providerId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .map(existingUser -> {
                            existingUser.setProvider(AuthProvider.GOOGLE);
                            existingUser.setProviderId(providerId);

                            return userRepository.save(existingUser);
                        })
                        .orElseGet(() -> {
                            User newUser = new User();
                            newUser.setEmail(email);
                            newUser.setFirstName(firstName);
                            newUser.setLastName(lastName);
                            newUser.setProvider(AuthProvider.GOOGLE);
                            newUser.setProviderId(providerId);
                            newUser.addRole(Role.ROLE_USER);

                            return userRepository.save(newUser);
                        }));

        UserDetails userDetails = new UserDetailsImpl(user);
        String token = jwtUtils.generateToken(userDetails);

        Cookie cookie = new Cookie("auth_transfer_token", URLEncoder.encode(token, StandardCharsets.UTF_8));
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(30);
        response.addCookie(cookie);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
