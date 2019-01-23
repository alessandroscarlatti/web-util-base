package com.scarlatti.webutil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scarlatti.webutil.model.WuBreadcrumb;
import com.scarlatti.webutil.model.WuCrumb;
import com.scarlatti.webutil.model.WuDetails.WuTaskGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 11/10/2018
 */
@Controller
public class WuController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(WuController.class);
    private WuAppState wuAppState;

    public WuController(WuAppState wuAppState) {
        this.wuAppState = wuAppState;
    }

    @GetMapping("/")
    public String index(@RequestParam(value = "motd", required = false) String motd,
                        Map<String, Object> model, HttpServletResponse response, HttpServletRequest request) {

        if (motd == null) {
            motd = "hey hey!";
        }

        model.put("motd", motd);
        model.put("view", "index");

        return "default";
    }

    @GetMapping("/login")
    public String login(Map<String, Object> model) {
        model.put("view", "login");

        return "default";
    }

    @GetMapping("/groups")
    public String groups(Map<String, Object> model) {
        model.put("view", "groups");

        model.put("breadcrumb", new WuBreadcrumb(
            new WuCrumb("WebSiteName", "/"),
            new WuCrumb("Task Groups", "/groups")
        ));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            model.put("groupsJson", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wuAppState.allGroups()));
        } catch (Exception e) {
            throw new RuntimeException("Error writing JSON.", e);
        }

        model.put("groups", wuAppState.allGroups());

        return "default";
    }

    @PostMapping("/groups")
    public String groups(@RequestParam("groupsJson") String groupsJson, Map<String, Object> model) {
        model.put("view", "groups");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<WuTaskGroup> taskGroups = objectMapper.readValue(groupsJson, new TypeReference<List<WuTaskGroup>>() {});
            wuAppState.getWuDetails().setGroups(taskGroups);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing json: " + groupsJson, e);
        }

        model.put("groups", wuAppState.allGroups());

        return "redirect:/groups";
    }

    @GetMapping("/group/{groupName}")
    public String groupw(@PathVariable("groupName") String groupName, Map<String, Object> model) {
        model.put("view", "group");

        model.put("tasks", wuAppState.tasksByGroup(groupName));

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
        return "/error";
    }
}
