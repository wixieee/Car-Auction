package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.LoginRequest;
import edu.lpnu.auction.dto.RegisterRequest;
import edu.lpnu.auction.factory.AuthDtoFactory;
import edu.lpnu.auction.factory.TestConstants;
import edu.lpnu.auction.factory.UserFactory;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.AuthProvider;
import edu.lpnu.auction.model.enums.Role;
import edu.lpnu.auction.repository.UserRepository;
import edu.lpnu.auction.utils.exception.types.AlreadyExistsException;
import edu.lpnu.auction.utils.exception.types.NotFoundException;
import edu.lpnu.auction.utils.exception.types.PasswordMismatchException;
import edu.lpnu.auction.utils.exception.types.WrongProviderException;
import edu.lpnu.auction.utils.mapper.UserMapper;
import edu.lpnu.auction.utils.security.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private UserDetailsService userDetailsService;
    @Mock private JWTUtils jwtUtils;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_WhenEmailExists_ShouldThrowException(){
        RegisterRequest registerRequest = AuthDtoFactory.getValidRegisterRequest();
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> authService.register(registerRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_WhenPasswordMismatch_ShouldThrowException(){
        RegisterRequest registerRequest = AuthDtoFactory.getMismatchRegisterRequest();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(PasswordMismatchException.class, () -> authService.register(registerRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_WhenValidRequest_ShouldSaveUserAndReturnToken(){
        RegisterRequest registerRequest = AuthDtoFactory.getValidRegisterRequest();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(RegisterRequest.class))).thenReturn(UserFactory.getRawMappedUser());
        when(passwordEncoder.encode(anyString())).thenReturn(TestConstants.DEFAULT_HASHED_PASSWORD);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));
        when(jwtUtils.generateToken(any())).thenReturn(TestConstants.DEFAULT_JWT);

        String token = authService.register(registerRequest);
        assertEquals(TestConstants.DEFAULT_JWT, token);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User user = captor.getValue();

        assertEquals(TestConstants.DEFAULT_HASHED_PASSWORD, user.getPassword());
        assertTrue(user.getRoles().contains(Role.ROLE_USER));
        assertEquals(AuthProvider.LOCAL, user.getProvider());
    }

    @Test
    void login_WhenEmailNotFound_ShouldThrowException(){
        LoginRequest loginRequest = AuthDtoFactory.getValidLoginRequest();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_WhenProviderIsGoogle_ShouldThrowException(){
        LoginRequest loginRequest = AuthDtoFactory.getValidLoginRequest();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(UserFactory.getGoogleUser()));

        assertThrows(WrongProviderException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_WhenInvalidPassword_ShouldThrowException(){
        LoginRequest loginRequest = AuthDtoFactory.getValidLoginRequest();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(UserFactory.getLocalUser()));
        doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_WhenValidRequest_ShouldReturnToken(){
        LoginRequest loginRequest = AuthDtoFactory.getValidLoginRequest();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(UserFactory.getLocalUser()));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));
        when(jwtUtils.generateToken(any())).thenReturn(TestConstants.DEFAULT_JWT);

        String token = authService.login(loginRequest);
        assertEquals(TestConstants.DEFAULT_JWT, token);

        verify(authenticationManager).authenticate(any());
    }
}