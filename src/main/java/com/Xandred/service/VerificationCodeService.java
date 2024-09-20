package com.Xandred.service;

import com.Xandred.domain.VerificationType;
import com.Xandred.model.User;
import com.Xandred.model.VerificationCode;

public interface VerificationCodeService {

    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(long id) throws Exception;

    VerificationCode getVerificationCodeByUser(long UserId);



    void deleteVerificationCodeById(VerificationCode verificationCode);
}
