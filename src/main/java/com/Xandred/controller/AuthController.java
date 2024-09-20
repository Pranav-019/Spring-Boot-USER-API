package com.Xandred.controller;

import com.Xandred.config.JwtProvider;
import com.Xandred.model.TwoFactorOtp;
import com.Xandred.model.User;
import com.Xandred.repository.TwoFactorOtpRepository;
import com.Xandred.repository.UserRepository;
import com.Xandred.response.AuthResponse;
import com.Xandred.service.CustomUserDetailsService;
import com.Xandred.service.EmailService;
import com.Xandred.service.TwoFactorOtpService;
import com.Xandred.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;  // Fixed typo in variable name

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;


    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if (isEmailExist != null) {
            throw new Exception("Email already exists");  // Updated message
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());  // Consider encrypting the password here
        newUser.setFullName(user.getFullName());

        userRepository.save(newUser);  // Save the new user in the repository

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);  // Assuming JwtProvider has a static method

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Your registration was successful");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String userName = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(userName, password);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);  // Assuming JwtProvider has a static method

        User authUser = userRepository.findByEmail(userName);

        if(user.getTwoFactorAuth().isEnabled()){
            AuthResponse res = new AuthResponse();
            res.setMessage("Two Factor Auth is Enabled");
            res.setTwoFactorAuthEnabled(true);
            String otp = OtpUtils.generateOTP();

            TwoFactorOtp oldTwoFactorOtp=twoFactorOtpService.findByUser(authUser.getId());

            if(oldTwoFactorOtp!=null){
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
            }

            TwoFactorOtp newTwoFactorOtp=twoFactorOtpService.createTwoFactorOtp(authUser,otp, jwt);

            emailService.sendVerificationOtpEmail(userName, otp);



            res.setSession(newTwoFactorOtp.getId());
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("User logged in successfully");

        return new ResponseEntity<>(res, HttpStatus.OK);  // Use OK for login success
    }

    private Authentication authenticate(String userName, String password) {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);  // Fixed typo

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }

        if (!password.equals(userDetails.getPassword())) {  // Consider using a password encoder
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigninOtp(@PathVariable String otp, @RequestParam String id) throws Exception {

        TwoFactorOtp twoFactorOtp = twoFactorOtpService.findById(id);

        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOtp, otp)){
            AuthResponse res = new AuthResponse();
            res.setMessage("Your Email Account has been verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOtp.getJwt());

            return new ResponseEntity<>(res, HttpStatus.OK);

        }
        throw new Exception("Invalid Otp");

    }
}
