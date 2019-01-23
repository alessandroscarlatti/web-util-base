package com.scarlatti.webutil.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
public class WuUser {
    private String prettyName = "DOE, JOHN";
    private List<String> authorities = new ArrayList<>();

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
