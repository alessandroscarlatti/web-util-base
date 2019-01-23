package com.scarlatti.webutil.controller;

import com.scarlatti.webutil.WuAppState;
import com.scarlatti.webutil.model.WuActivityDetails.WuActivity;
import org.springframework.stereotype.Component;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
@Component
public class BasicActivityController extends ActivityController {

    private WuAppState wuAppState;

    public BasicActivityController(WuAppState wuAppState) {
        this.wuAppState = wuAppState;
    }

    @Override
    public WuActivity getActivity() {
        return wuAppState.activityByName("basicActivity");
    }
}
