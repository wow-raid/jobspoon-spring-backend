package com.wowraid.jobspoon.studyroom.service.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateInterviewChannelRequest {
    private final String channelName;
    private final String url;
}
