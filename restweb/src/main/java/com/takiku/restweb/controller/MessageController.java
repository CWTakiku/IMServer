package com.takiku.restweb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.takiku.restweb.service.OfflineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/poll/{userId}")
    public ResponseModel<List<Offline>> pollOfflineMsg(@PathVariable("userId") String userId) {

        System.out.println(" usrid " + userId);
        try {
            return ResponseModel.buildOk(offlineService.pollOfflineMsg(userId));
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
