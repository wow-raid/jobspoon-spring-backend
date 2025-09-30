package com.wowraid.jobspoon.studyroom.service.response;

import com.wowraid.jobspoon.studyroom.entity.InterviewChannel;
import lombok.Getter;

@Getter
public class InterviewChannelResponse {
    private final String name;
    private final String url;

    private InterviewChannelResponse(InterviewChannel channel) {
        this.name = channel.getChannelName();
        this.url = channel.getUrl();
    }

    public static InterviewChannelResponse from(InterviewChannel channel) {
        return new InterviewChannelResponse(channel);
    }
}
