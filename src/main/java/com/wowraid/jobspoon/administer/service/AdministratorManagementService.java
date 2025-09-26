package com.wowraid.jobspoon.administer.service;

import com.wowraid.jobspoon.administer.controller.dto.AdministratorUserInfoRequest;
import com.wowraid.jobspoon.administer.controller.dto.AdministratorUserInfoResponse;
import com.wowraid.jobspoon.administer.service.dto.AdministratorUserListResponse;

public interface AdministratorManagementService {
    AdministratorUserListResponse getUserInfo(AdministratorUserInfoRequest request);

}
