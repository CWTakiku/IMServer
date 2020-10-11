package com.takiku.restweb.controller;


import com.takiku.restweb.service.UserService;
import com.takiku.restweb.vo.UserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import po.ResponseModel;
import po.ShakeHands;
import po.UserCertification;
import retrofit2.http.GET;

@RestController
@RequestMapping("/api/user")
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/auth")
    public ResponseModel<UserCertification> auth(@RequestBody ShakeHands shakeHands) {
        if (shakeHands != null) {
            UserCertification userCertification = userService.verifyAndGet(shakeHands.getUserId(), shakeHands.getToken());
            return ResponseModel.buildOk(userCertification);
        }
        return ResponseModel.buildServiceError();
    }

    @GetMapping("/test")
    public String test() {
        return "jhhh";
    }
}
