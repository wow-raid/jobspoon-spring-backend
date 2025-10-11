package com.wowraid.jobspoon.awsCost.service;

import com.wowraid.jobspoon.awsCost.entity.AwsDailyCost;

import java.time.LocalDate;
import java.util.List;

public interface AwsCostService {
    List<AwsDailyCost> getDailyTotalCost(LocalDate start, LocalDate end);
}
