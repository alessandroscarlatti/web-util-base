package com.scarlatti.webutil;

import com.scarlatti.webutil.model.WuActivityDetails;
import com.scarlatti.webutil.model.WuActivityDetails.WuActivity;
import com.scarlatti.webutil.model.WuActivityDetails.WuActivityGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
@Component
public class WuAppState {
    private WuActivityDetails wuActivityDetails;
    private static final Logger log = LoggerFactory.getLogger(WuAppState.class);

    public WuAppState(WuActivityDetails wuActivityDetails) {
        this.wuActivityDetails = wuActivityDetails;
    }

    public List<WuActivityGroup> allGroups() {
        return wuActivityDetails.getGroups();
    }

    public WuActivityGroup groupByName(String groupName) {
        return wuActivityDetails.getGroups()
            .stream()
            .filter(wuActivityGroup -> wuActivityGroup.getName().equals(groupName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Could not find group " + groupName));
    }

    public List<WuActivity> activitiesByGroup(String groupName) {
        return wuActivityDetails.getActivities()
            .stream()
            .filter(wuActivity -> groupName.equals(wuActivity.getGroup()))
            .collect(toList());
    }

    public WuActivity activityByName(String activityName) {
        return wuActivityDetails.getActivities()
            .stream()
            .filter(wuActivity -> activityName.equals(wuActivity.getName()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Could not find activity " + activityName));
    }

    public void updateActivitiesByGroup(String groupName, List<WuActivity> tasks) {
        wuActivityDetails.getActivities().removeIf(wuActivity -> wuActivity.getGroup().equals(groupName));
        wuActivityDetails.getActivities().addAll(tasks);
    }

    public void updateWuDetails(WuActivityDetails wuActivityDetails) {
        log.info("Updating WU Details: {}", wuActivityDetails);
        this.wuActivityDetails = wuActivityDetails;
    }

    public WuActivityDetails getWuActivityDetails() {
        return wuActivityDetails;
    }
}
