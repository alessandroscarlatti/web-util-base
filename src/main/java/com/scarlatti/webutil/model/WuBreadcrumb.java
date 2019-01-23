package com.scarlatti.webutil.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
public class WuBreadcrumb {
    private List<WuCrumb> crumbs = new ArrayList<>();

    public WuBreadcrumb() {
    }

    public WuBreadcrumb(WuCrumb... crumbs) {
        this.crumbs = new ArrayList<>(Arrays.asList(crumbs));
    }

    public List<WuCrumb> getAncestors() {
        return crumbs.subList(0, crumbs.size() - 1);
    }

    public WuCrumb getLast() {
        return crumbs.get(crumbs.size() - 1);
    }

    public List<WuCrumb> getCrumbs() {
        return crumbs;
    }

    public void setCrumbs(List<WuCrumb> crumbs) {
        this.crumbs = crumbs;
    }
}
