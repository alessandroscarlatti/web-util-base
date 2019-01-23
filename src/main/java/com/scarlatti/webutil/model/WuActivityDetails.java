package com.scarlatti.webutil.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
public class WuActivityDetails {

    private List<WuActivityGroup> groups = new ArrayList<>();
    private List<WuActivity> activities = new ArrayList<>();

    public List<WuActivityGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<WuActivityGroup> groups) {
        this.groups = groups;
    }

    public List<WuActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<WuActivity> activities) {
        this.activities = activities;
    }

    public static class WuActivityGroup {
        private String name;
        private String prettyName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrettyName() {
            return prettyName;
        }

        public void setPrettyName(String prettyName) {
            this.prettyName = prettyName;
        }
    }

    public static class WuActivity {
        private String name;
        private String prettyName;
        private String link;  // link to task page
        private String group;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrettyName() {
            return prettyName;
        }

        public void setPrettyName(String prettyName) {
            this.prettyName = prettyName;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }
    }
}
