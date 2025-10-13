package com.appswella.wisepaise.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SchedulerClass {
    @Scheduled(fixedRate = 60000, initialDelay = 0, zone = "Asia/Dubai")
    public void triggerScheduler() {
        System.out.println("Print now TS:::" + LocalDateTime.now());
    }
}
