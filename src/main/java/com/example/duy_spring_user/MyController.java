package com.example.duy_spring_user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1")
public class MyController {
    @GetMapping("/health")
    public @ResponseBody String health() {
        return "OK";
    }    
}
