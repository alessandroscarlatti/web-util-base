package com.scarlatti.webutil.controller;

import com.scarlatti.webutil.model.WuActivityDetails.WuActivity;

import java.util.Map;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
public abstract class ActivityController {

    public abstract WuActivity getActivity();

    public void loadActivity(Map<String, Object> model){
    }
}
