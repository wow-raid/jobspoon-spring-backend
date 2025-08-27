package com.wowraid.jobspoon.administer.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministerCodeLoginRequest {
    private String administerId;
    private String administerpassword;
}
