package com.exadel.frs.service;

import com.exadel.frs.entity.ResetPasswordToken;
import com.exadel.frs.entity.User;
import com.exadel.frs.repository.ResetPasswordTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    public ResetPasswordToken createToken(User user) {
        ResetPasswordToken token = ResetPasswordToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        return resetPasswordTokenRepository.save(token);
    }

    public Optional<ResetPasswordToken> findByToken(String token) {
        return resetPasswordTokenRepository.findByToken(token);
    }

    public void deleteToken(ResetPasswordToken token) {
        resetPasswordTokenRepository.delete(token);
    }

    public void deleteExpiredTokens() {
        resetPasswordTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
