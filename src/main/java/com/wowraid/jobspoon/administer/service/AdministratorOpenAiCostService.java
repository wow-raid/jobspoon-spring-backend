package com.wowraid.jobspoon.administer.service;

import java.time.Instant;
import java.util.List;

public interface AdministratorOpenAiCostService {
    String getDailyCosts(Instant startInclusiveUtc, Instant endExclusiveUtc);
}
