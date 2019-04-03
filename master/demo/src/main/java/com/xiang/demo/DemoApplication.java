package com.xiang.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@EnableRedisHttpSession
@RestController
@CrossOrigin(origins = "*",allowCredentials = "true")
public class DemoApplication {




    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class,args);
        System.out.println("内置tomcat启动成功");
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello(HttpSession session) {
        logger.info(session.isNew()+"");
        if (session.isNew()) {
            logger.info("Successfully creates a session ，the id of session ：" + session.getId());
            Random random = new Random();

            session.setAttribute("key", random.nextInt());
            logger.info(session.getAttribute("key").toString());
        } else {
            logger.info("session already exists in the server, the id of session ："+ session.getId());
            logger.info(session.getAttribute("key").toString());
        }
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }

    @GetMapping("qu")
    public Map<String, Object> qu(HttpSession session){
        Map<String, Object> map = new HashMap<>();
        map.put("key",session.getAttribute("key"));
        return map;
    }

    @RequestMapping(value = "/first", method = RequestMethod.GET)
    public Map<String, Object> firstResp (HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        request.getSession().setAttribute("request Url", request.getRequestURL());
        map.put("request Url", request.getRequestURL());
        return map;
    }

    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public Object sessions (HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", request.getSession().getId());
        map.put("message", request.getSession().getAttribute("map"));
        return map;
    }
}