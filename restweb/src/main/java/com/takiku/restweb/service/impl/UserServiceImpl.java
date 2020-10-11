package com.takiku.restweb.service.impl;

import com.takiku.restweb.service.UserService;
import org.springframework.stereotype.Service;
import po.UserCertification;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserCertification verifyAndGet(String userId, String token) {
        UserCertification userCertification = new UserCertification();
        userCertification.setResult(true);
        return userCertification;
    }
}
