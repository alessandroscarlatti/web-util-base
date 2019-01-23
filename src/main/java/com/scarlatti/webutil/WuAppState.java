package com.scarlatti.webutil;

import com.scarlatti.webutil.model.WuDetails;
import com.scarlatti.webutil.model.WuDetails.WuTask;
import com.scarlatti.webutil.model.WuDetails.WuTaskGroup;
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
    private WuDetails wuDetails;
    private static final Logger log = LoggerFactory.getLogger(WuAppState.class);

    public WuAppState(WuDetails wuDetails) {
        this.wuDetails = wuDetails;
    }

    public List<WuTaskGroup> allGroups() {
        return wuDetails.getGroups();
    }

    public WuTaskGroup groupByName(String groupName) {
        return wuDetails.getGroups()
            .stream()
            .filter(wuTaskGroup -> wuTaskGroup.getName().equals(groupName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Could not find group " + groupName));
    }

    public List<WuTask> tasksByGroup(String groupName) {
        return wuDetails.getTasks()
            .stream()
            .filter(wuTask -> wuTask.getName().equals(groupName))
            .collect(toList());
    }

    public void updateWuDetails(WuDetails wuDetails) {
        log.info("Updating WU Details: {}", wuDetails);
        this.wuDetails = wuDetails;
    }

    public WuDetails getWuDetails() {
        return wuDetails;
    }
}
