package com.management.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/test")
public class TestView {

    @GetMapping("index")
    public String getExcel(){
        return "test/test";
    }
    @GetMapping("test")
    public String test(){
        return "config/index";
    }
}
