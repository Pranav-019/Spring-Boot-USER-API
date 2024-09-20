package com.Xandred.service;

import com.Xandred.domain.VerificationType;
import com.Xandred.model.User;
import com.Xandred.model.VerificationCode;
import com.Xandred.repository.VerificationCodeRepository;
import com.Xandred.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;


    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType ) {
        VerificationCode verificationCode1=new VerificationCode();
        verificationCode1.setOtp(OtpUtils.generateOTP());
        verificationCode1.setVerificationType(verificationType);
        verificationCode1.setUser(user);
        return verificationCodeRepository.save(verificationCode1);
    }

    @Override
    public VerificationCode getVerificationCodeById(long id) throws Exception {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);

        if(verificationCode.isPresent()){
            return verificationCode.get();
        }
        throw new Exception("Verification Code not Found");
    }

    @Override
    public VerificationCode getVerificationCodeByUser(long UserId) {
        return verificationCodeRepository.findByUserId(UserId);
    }

    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {
        verificationCodeRepository.delete(verificationCode);

    }
}
