package com.karthick.youtubeclone.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${spring.services.path}")
public class IndexController {

    @Value("${spring.data.mongodb.uri}")
    private String mongoDbUri;

    @GetMapping("/health-check")
    @ResponseBody
    public String healthCheck(){
        return "Ok";
    }


    @GetMapping("/db-uri")
    @ResponseBody
    public String getDBURI(){
        return mongoDbUri;
    }
}
