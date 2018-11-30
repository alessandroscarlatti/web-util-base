package com.scarlatti.springsecuritydemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 11/10/2018
 */
@Controller
public class DemoController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/")
    public String index(@RequestParam(value = "motd", required = false) String motd,
                        Map<String, Object> model, HttpServletResponse response, HttpServletRequest request) {

        Cookie cookie = new Cookie("test-cookie", "cool-value");
//        cookie.setDomain(".google.com");

        response.addCookie(cookie);

        if (motd == null) {
            motd = "hey hey!";
        }

        model.put("motd",  motd);
        model.put("view", "index");

        return "default";
    }

    @GetMapping("/login")
    public String login(Map<String, Object> model) {
        model.put("view", "login");

        return "default";
    }

    @GetMapping("/secret")
    public String secret(Map<String, Object> model) {
        model.put("view", "secret");

        return "default";
    }

    @GetMapping("/admin")
    public String admin(Map<String, Object> model) {
        model.put("view", "admin");

        return "default";
    }

    @PostMapping("/task1")
    public ResponseEntity<String> task1(@RequestParam(value = "ttl", required = false, defaultValue = "3000") String ttl) {
        try {
            log.info("beginning task 1");
            log.info("TTL = " + ttl);
            Thread.sleep(Long.parseLong(ttl));
            log.info("finished task 1");

            return ResponseEntity.ok("done");
        } catch (Exception e) {
            throw new RuntimeException("Error running task 1");
        }
    }

    @GetMapping("/error")
    public String error(Map<String, Object> model) {
        model.put("view", "error");
        return "default";
    }

    @Override
    public String getErrorPath() {
        return "/__dummy_error";
    }
}
