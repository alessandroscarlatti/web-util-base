package com.scarlatti.webutil.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
public class WuDetails {

    private List<WuTaskGroup> groups = new ArrayList<>();
    private List<WuTask> tasks = new ArrayList<>();

    public List<WuTaskGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<WuTaskGroup> groups) {
        this.groups = groups;
    }

    public List<WuTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<WuTask> tasks) {
        this.tasks = tasks;
    }

    public static class WuTaskGroup {
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

    public static class WuTask {
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
