package com.jakubeeee.integration.controllers;

import com.jakubeeee.integration.model.AdditionalInfoContainer;
import com.jakubeeee.integration.service.AdditionalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class AdditionalInfoController {

    @Autowired
    AdditionalInfoService additionalInfoService;

    @GetMapping("/additionalInfo")
    public Set<AdditionalInfoContainer> updateInfoTileData() {
        return additionalInfoService.getAdditionalInfoSet();
    }

}
