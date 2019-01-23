package com.scarlatti.webutil.controller;

import com.scarlatti.webutil.WuAppState;
import com.scarlatti.webutil.model.WuActivityDetails.WuActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/22/2019
 */
@Controller
@RequestMapping("/activities/basicActivity")
public class BasicActivityController extends ActivityController {

    private WuAppState wuAppState;
    private static final Logger log = LoggerFactory.getLogger(BasicActivityController.class);

    public BasicActivityController(WuAppState wuAppState) {
        this.wuAppState = wuAppState;
    }

    @Override
    public WuActivity getActivity() {
        return wuAppState.activityByName("basicActivity");
    }


    @PostMapping("/task1")
    public ResponseEntity<String> task1(@RequestParam(value = "ttl", required = false, defaultValue = "3000") String ttl) {
        try {
            log.info("beginning task 1");
            log.info("TTL = " + ttl);
            Thread.sleep(Long.parseLong(ttl));
            log.info("finished task 1");

            return ResponseEntity.ok("done");
        } catch (Exception e) {
            throw new RuntimeException("Error running task 1");
        }
    }

}
