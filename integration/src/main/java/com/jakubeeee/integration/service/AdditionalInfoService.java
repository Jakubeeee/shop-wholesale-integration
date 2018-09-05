package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.AdditionalInfoContainer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.jakubeeee.common.utils.DateTimeUtils.format;
import static com.jakubeeee.integration.model.AdditionalInfoContainer.Type.*;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AdditionalInfoService {

    @Autowired
    IntegrationService integrationService;

    @Autowired
    SchedulerService schedulerService;

    @Getter
    Set<AdditionalInfoContainer> additionalInfoSet = new HashSet<>();

    @PostConstruct
    public void initialize() {
        updateAdditionalInfo(schedulerService.getNextUpdateDateTime(), NEXT_UPDATE);
        updateAdditionalInfo(schedulerService.getUpdateIntervalInMillis() / 60000 + " min", UPDATE_INTERVAL);
    }

    public void updateAdditionalInfos() {
        additionalInfoSet.clear();
        updateAdditionalInfo(integrationService.getLastUpdateDateTime(), LAST_UPDATE);
        updateAdditionalInfo(integrationService.getProductsUpdatedInLastUpdate(), PRODUCTS_UPDATED);
        updateAdditionalInfo(schedulerService.getNextUpdateDateTime(), NEXT_UPDATE);
        updateAdditionalInfo(schedulerService.getUpdateIntervalInMillis() / 60000 + " min", UPDATE_INTERVAL);
    }

    private void updateAdditionalInfo(Object information, AdditionalInfoContainer.Type type) {
        String formattedInformation;
        if (information instanceof LocalDateTime)
            formattedInformation = format((LocalDateTime) information);
        else if (information instanceof Integer)
            formattedInformation = String.valueOf(information);
        else
            formattedInformation = (String) information;

        try {
            additionalInfoSet.add(new AdditionalInfoContainer(formattedInformation, type));
        } catch (NullPointerException ignored) {
            LOG.debug("Problem has occurred while updating info tile of type {}", type);
        }
    }
}
