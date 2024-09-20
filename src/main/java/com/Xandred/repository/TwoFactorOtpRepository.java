package com.Xandred.repository;

import com.Xandred.model.TwoFactorOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOtp, String> {

    TwoFactorOtp findByUserId(long userId);
}
