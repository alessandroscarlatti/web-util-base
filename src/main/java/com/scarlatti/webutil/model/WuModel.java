package com.scarlatti.webutil.model;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
public class WuModel {
    private String appName;
    private WuUser user = new WuUser();

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public WuUser getUser() {
        return user;
    }

    public void setUser(WuUser user) {
        this.user = user;
    }
}
