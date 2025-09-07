package com.wowraid.jobspoon.term.support;

import java.util.*;
import java.util.regex.*;

public final class TagTextParser {
    private static final Pattern HASH = Pattern.compile("#([^#\\s,;]+)");

    private TagTextParser() {}

    public static List<String> parse(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        Matcher m = HASH.matcher(raw);
        while (m.find()) {
            String name = m.group(1).trim();
            if (!name.isEmpty()) set.add(name);
        }
        return new ArrayList<>(set); // 필요하면 .stream().sorted().toList()
    }
}
