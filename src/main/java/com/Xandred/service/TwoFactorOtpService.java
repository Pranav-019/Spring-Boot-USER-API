package com.Xandred.service;
import com.Xandred.model.TwoFactorOtp;
import com.Xandred.model.User;
public interface TwoFactorOtpService {

    TwoFactorOtp createTwoFactorOtp(User user, String otp, String jwt );

    TwoFactorOtp findByUser(long UserId);

    TwoFactorOtp findById(String id);

    boolean verifyTwoFactorOtp(TwoFactorOtp twoFactorOtp, String otp);

    void deleteTwoFactorOtp(TwoFactorOtp twoFactorOtp);




}
