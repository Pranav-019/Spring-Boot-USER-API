package com.Xandred.service;

import com.Xandred.domain.VerificationType;
import com.Xandred.model.ForgotPasswordToken;
import com.Xandred.model.User;

public interface ForgotPasswordService {
    ForgotPasswordToken createToken(User user,
                                    String id, String otp,
                                    VerificationType verificationType,
                                    String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}
