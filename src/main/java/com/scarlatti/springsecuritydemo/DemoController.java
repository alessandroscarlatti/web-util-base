package com.scarlatti.springsecuritydemo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 11/10/2018
 */
@Controller
public class DemoController {


    @GetMapping("/")
    public String index(@RequestParam(value = "motd", required = false) String motd,
                        Map<String, Object> model) {

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
}
