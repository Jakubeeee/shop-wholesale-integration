package com.jakubeeee.integration.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class ProgressMonitor {

    int isUpdating = 0;
    int currentProgress = 0;
    int maxProgress = 1;

}
