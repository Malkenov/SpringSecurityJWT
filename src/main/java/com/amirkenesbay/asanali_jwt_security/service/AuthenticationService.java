package com.amirkenesbay.asanali_jwt_security.service;

import com.amirkenesbay.asanali_jwt_security.config.JwtService;
import com.amirkenesbay.asanali_jwt_security.dto.request.AuthenticationRequest;
import com.amirkenesbay.asanali_jwt_security.dto.request.RegistrationRequest;
import com.amirkenesbay.asanali_jwt_security.dto.response.AuthenticationResponse;
import com.amirkenesbay.asanali_jwt_security.entity.RefreshToken;
import com.amirkenesbay.asanali_jwt_security.entity.Role;
import com.amirkenesbay.asanali_jwt_security.entity.User;
import com.amirkenesbay.asanali_jwt_security.repository.RefreshTokenRepository;
import com.amirkenesbay.asanali_jwt_security.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticationResponse register(RegistrationRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .expiryDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        refreshTokenRepository.deleteByUserId(user.getId());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .expiryDate(LocalDateTime.now().plusDays(30))
                        .build()
        );

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse refresh(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = token.getUser();
        String newAccessToken = jwtService.generateToken(user);

        return new AuthenticationResponse(newAccessToken, token.getToken());
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

}
