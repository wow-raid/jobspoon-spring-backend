package com.wowraid.jobspoon.administer.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorCodeLoginRequest {
    private String administratorId;
    private String administratorpassword;
}
