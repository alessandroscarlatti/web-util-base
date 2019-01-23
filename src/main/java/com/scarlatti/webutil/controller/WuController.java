package com.scarlatti.webutil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scarlatti.webutil.WuAppState;
import com.scarlatti.webutil.model.WuBreadcrumb;
import com.scarlatti.webutil.model.WuCrumb;
import com.scarlatti.webutil.model.WuActivityDetails.WuActivity;
import com.scarlatti.webutil.model.WuActivityDetails.WuActivityGroup;
import com.scarlatti.webutil.model.WuDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private WuDetails wuDetails;
    private List<ActivityController> activityControllers;

    public WuController(WuAppState wuAppState, WuDetails wuDetails, List<ActivityController> activityControllers) {
        this.wuAppState = wuAppState;
        this.wuDetails = wuDetails;
        this.activityControllers = activityControllers;
    }

    @GetMapping("/")
    public String index(@RequestParam(value = "motd", required = false) String motd,
                        Map<String, Object> model, HttpServletResponse response, HttpServletRequest request) {

        if (motd == null) {
            motd = "hey hey!";
        }

        model.put("motd", motd);
        model.put("view", "index");

        model.put("appName", wuDetails.getName());

        model.put("breadcrumb", new WuBreadcrumb(
            new WuCrumb(wuDetails.getName(), "/"),
            new WuCrumb("Home", "/")
        ));


        return "default";
    }

    @GetMapping("/login")
    public String login(Map<String, Object> model) {
        model.put("view", "login");

        model.put("appName", wuDetails.getName());

        return "default";
    }

    @GetMapping("/groups")
    public String groups(Map<String, Object> model) {
        model.put("view", "groups");
        model.put("appName", wuDetails.getName());

        model.put("breadcrumb", new WuBreadcrumb(
            new WuCrumb(wuDetails.getName(), "/"),
            new WuCrumb("Activities", "/groups")
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
        model.put("appName", wuDetails.getName());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<WuActivityGroup> taskGroups = objectMapper.readValue(groupsJson, new TypeReference<List<WuActivityGroup>>() {});
            wuAppState.getWuActivityDetails().setGroups(taskGroups);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing json: " + groupsJson, e);
        }

        model.put("groups", wuAppState.allGroups());

        return "redirect:/groups";
    }

    @GetMapping("/groups/{groupName}")
    public String group(@PathVariable("groupName") String groupName, Map<String, Object> model) {
        model.put("view", "group");
        model.put("appName", wuDetails.getName());

        model.put("breadcrumb", new WuBreadcrumb(
            new WuCrumb(wuDetails.getName(), "/"),
            new WuCrumb("Activities", "/groups"),
            new WuCrumb(wuAppState.groupByName(groupName).getPrettyName(), "/groups/" + groupName)
        ));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            model.put("activitiesJson", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wuAppState.activitiesByGroup(groupName)));
        } catch (Exception e) {
            throw new RuntimeException("Error writing JSON.", e);
        }
        model.put("group", wuAppState.groupByName(groupName));
        model.put("activities", wuAppState.activitiesByGroup(groupName));

        return "default";
    }

    @PostMapping("/groups/{groupName}")
    public String updateGroup(@PathVariable("groupName") String groupName, @RequestParam("activitiesJson") String activitiesJson, Map<String, Object> model) {
        model.put("view", "group");
        model.put("appName", wuDetails.getName());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<WuActivity> activities = objectMapper.readValue(activitiesJson, new TypeReference<List<WuActivity>>() {});
            wuAppState.updateActivitiesByGroup(groupName, activities);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing json: " + activitiesJson, e);
        }

        return "redirect:/group/" + groupName;
    }

    @GetMapping("/activities/{activityName}")
    public String activity(@PathVariable("activityName") String activityName, Map<String, Object> model) {
        model.put("view", "group");
        model.put("appName", wuDetails.getName());

        ActivityController activityController = findActivityController(activityName);

        WuActivity wuActivity = wuAppState.activityByName(activityName);
        WuActivityGroup wuActivityGroup = wuAppState.groupByName(wuActivity.getGroup());

        model.put("view", wuActivity.getName());
        model.put("breadcrumb", new WuBreadcrumb(
            new WuCrumb(wuDetails.getName(), "/"),
            new WuCrumb("Activities", "/groups"),
            new WuCrumb(wuActivityGroup.getPrettyName(), "/groups/" + wuActivityGroup.getName()),
            new WuCrumb(wuActivity.getPrettyName(), "/groups/" + wuActivity.getName())
        ));

        activityController.loadActivity(model);

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
        model.put("appName", wuDetails.getName());
        model.put("view", "error");
        return "default";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    private ActivityController findActivityController(String activityName) {
        for (ActivityController activityController : activityControllers) {
            if (activityName.equals(activityController.getActivity().getName())) {
                return activityController;
            }
        }

        throw new RuntimeException("No activity found: " + activityName);
    }
}
