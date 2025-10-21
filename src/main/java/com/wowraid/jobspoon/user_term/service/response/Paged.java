package com.wowraid.jobspoon.user_term.service.response;

import java.util.List;

public record Paged<T>(List<T> items, long total) {
}
