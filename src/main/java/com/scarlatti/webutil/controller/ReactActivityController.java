package com.scarlatti.webutil.controller;

import com.scarlatti.webutil.WuAppState;
import com.scarlatti.webutil.model.WuActivityDetails.WuActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
@Controller
@RequestMapping("/activities/reactActivity")
public class ReactActivityController extends ActivityController {

    private WuAppState wuAppState;
    private static final Logger log = LoggerFactory.getLogger(ReactActivityController.class);

    public ReactActivityController(WuAppState wuAppState) {
        this.wuAppState = wuAppState;
    }

    @Override
    public WuActivity getActivity() {
        return wuAppState.activityByName("reactActivity");
    }

}
