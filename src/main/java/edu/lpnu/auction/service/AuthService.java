package edu.lpnu.auction.service;

import edu.lpnu.auction.dto.LoginRequest;
import edu.lpnu.auction.dto.RegisterRequest;
import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.Role;
import edu.lpnu.auction.utils.exception.types.AlreadyExistsException;
import edu.lpnu.auction.repository.UserRepository;
import edu.lpnu.auction.utils.exception.types.PasswordMismatchException;
import edu.lpnu.auction.utils.mapper.UserMapper;
import edu.lpnu.auction.utils.security.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserDetailsService userDetailsService;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest registerRequest) {
         if(userRepository.existsByEmail(registerRequest.getEmail())) {
             throw new AlreadyExistsException("Користувач з таким email уже існує");
         }

        if(Period.between(registerRequest.getBirthDate(), LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("Вам має бути щонайменше 18 років");
        }

        if(!Objects.equals(registerRequest.getPassword(), registerRequest.getPasswordConfirm())) {
            throw new PasswordMismatchException("Паролі не співпадають");
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.addRole(Role.ROLE_USER);
        userRepository.save(user);

        return generateToken(registerRequest.getEmail());
    }

    public String login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        return generateToken(loginRequest.getEmail());
    }

    private String generateToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtils.generateToken(userDetails);
    }
}
