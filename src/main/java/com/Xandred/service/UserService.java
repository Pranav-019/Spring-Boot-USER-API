package com.Xandred.service;

import com.Xandred.domain.VerificationType;
import com.Xandred.model.User;

public interface UserService {

    public User findUserProfileByJwt(String jwt) throws Exception;
    public User findUserProfileByEmail(String email) throws Exception;

    public User findUserById(long userId) throws Exception;

    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user);

    User updatePssword(User user, String newPassword);
}
