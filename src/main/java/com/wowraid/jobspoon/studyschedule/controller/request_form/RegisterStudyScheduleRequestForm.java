package com.wowraid.jobspoon.studyschedule.controller.request_form;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterStudyScheduleRequestForm {
    @NotBlank(message = "필수입력: 제목을 입력해주세요.")
    private String title;

    private String content;

    @NotBlank(message = "필수입력: 장소를 입력해주세요.")
    private String place;

    @NotNull(message = "필수입력: 시작시간을 입력해주세요.")
    @Future(message = "시작시간은 현재시간 이후로 설정해야 합니다.")
    private LocalDateTime startTime;

    @NotNull(message = "필수입력: 종료시간을 입력해주세요.")
    @Future(message = "종료시간은 현재시간 이후로 설정해야 합니다.")
    private LocalDateTime endTime;
}