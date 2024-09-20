package com.Xandred.controller;

import com.Xandred.request.ForgotPasswordTokenRequest;
import com.Xandred.domain.VerificationType;
import com.Xandred.model.ForgotPasswordToken;
import com.Xandred.model.User;
import com.Xandred.model.VerificationCode;
import com.Xandred.request.ResetPasswordRequest;
import com.Xandred.response.ApiResponse;
import com.Xandred.response.AuthResponse;
import com.Xandred.service.EmailService;
import com.Xandred.service.ForgotPasswordService;
import com.Xandred.service.UserService;
import com.Xandred.service.VerificationCodeService;
import com.Xandred.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;
    private String jwt;


    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        return new ResponseEntity<User>(user, HttpStatus.OK);

    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if(verificationCode==null){
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        if(verificationType.equals(verificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }

        return new ResponseEntity<String>("Verification Otp sent", HttpStatus.OK);

    }


    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@PathVariable String otp,@RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo=verificationCode.getVerificationType().equals(VerificationType.EMAIL)?
                verificationCode.getEmail():verificationCode.getMobile();

        boolean isVerified=verificationCode.getOtp().equals(otp);

        if(isVerified){
            User updaatedUser=userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(), sendTo, user);
            verificationCodeService.deleteVerificationCodeById(verificationCode);

            return new ResponseEntity<>(updaatedUser, HttpStatus.OK);
        }

       throw new Exception("Wrong Otp");

    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest req) throws Exception {

        User user = userService.findUserProfileByEmail(req.getSendTo());
        String otp = OtpUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());

        if(token==null){
            token=forgotPasswordService.createToken(user, id, otp, req.getVerificationType(), req.getSendTo());
        }

        if(req.getVerificationType().equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
        }

        AuthResponse response = new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password Reset Otp Sent Sucessfully");


        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPasswordOtp(@RequestParam String id,
                                                 @RequestBody ResetPasswordRequest req,
                                                 @RequestHeader("Authorization") String jwt) throws Exception {



         ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

         boolean isVerified=forgotPasswordToken.getOtp().equals(req.getOtp());

         if(isVerified){
             userService.updatePssword(forgotPasswordToken.getUser(), req.getPassword());
             ApiResponse res = new ApiResponse();
             res.setMessage("Password Updated Successfully");

             return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
         }

         throw new Exception("Wrong Otp");



    }
}
