package com.takiku.restweb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.takiku.restweb.service.OfflineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import po.Offline;
import po.ResponseModel;
import po.UserCertification;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/api/offline")
public class MessageController {

    @Autowired
    OfflineService offlineService;

    @GetMapping("/poll/{token}")
    public ResponseModel<List<Offline>> pollOfflineMsg(@PathParam("token") String token) {

        System.out.println(token);
        try {
            return ResponseModel.buildOk(offlineService.pollOfflineMsg(token));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseModel.buildServiceError();
    }
    @GetMapping("/test")
    public String test() {
        return "jhhh";
    }
}
