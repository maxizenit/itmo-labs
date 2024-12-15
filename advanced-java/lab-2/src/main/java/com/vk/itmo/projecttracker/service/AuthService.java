package com.vk.itmo.projecttracker.service;

import com.vk.itmo.projecttracker.exception.InvalidLoginDetailsException;
import com.vk.itmo.projecttracker.exception.UserNotFoundException;
import com.vk.itmo.projecttracker.exception.UsernameAlreadyRegisteredException;
import com.vk.itmo.projecttracker.model.entity.User;
import com.vk.itmo.projecttracker.util.JwtTokenUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthService {

    UserService userService;
    AuthenticationManager authenticationManager;
    JwtTokenUtils jwtTokenUtils;
    PasswordEncoder passwordEncoder;

    @Transactional
    public synchronized User registerUser(@NonNull String username, @NonNull String password, @NonNull String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password).toCharArray());
        user.setEmail(email);

        try {
            userService.getUserByUsername(username);
            throw new UsernameAlreadyRegisteredException(username);
        } catch (UserNotFoundException _) {
            return userService.saveUser(user);
        }
    }

    public String getToken(@NonNull String username, @NonNull String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = userService.loadUserByUsername(username);
            return jwtTokenUtils.generateToken(userDetails);
        } catch (AuthenticationException | UserNotFoundException _) {
            throw new InvalidLoginDetailsException();
        }
    }
}
