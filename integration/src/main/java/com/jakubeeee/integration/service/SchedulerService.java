package com.jakubeeee.integration.service;

import com.jakubeeee.common.service.TimerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class SchedulerService {

    @Autowired
    TimerService timerService;

    @Autowired
    IntegrationService integrationService;

    final int UPDATE_DELAY = 10000;

    @Getter
    int updateIntervalInMillis = 3600000;

    @Getter
    LocalDateTime nextUpdateDateTime = now().plusSeconds(UPDATE_DELAY / 1000);

    public void scheduleUpdates() {
        timerService.setRecurrentTask(() -> {
            nextUpdateDateTime = now().plusSeconds(updateIntervalInMillis / 1000);
            integrationService.updateAllProductsStocks();
        }, UPDATE_DELAY, updateIntervalInMillis);
    }

}
