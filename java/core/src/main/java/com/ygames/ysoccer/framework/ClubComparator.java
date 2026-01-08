package com.ygames.ysoccer.framework;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClubComparator {

    private static final List<String> PREFIXES = Arrays.asList(
        "fc", "cf", "as", "sd", "cd", "ud", "sc", "ac", "rc", "af", "asd", "cp", "rcd"
    );

    public static final Comparator<String> CLUB_COMPARATOR = Comparator.comparing(ClubComparator::normalize);

    private static String normalize(String name) {
        if (name == null) {
            return "";
        }

        String result = name.toLowerCase(Locale.ROOT).trim();

        for (String prefijo : PREFIXES) {
            result = result.replaceFirst("^" + prefijo + "\\b\\s*", "");
        }

        return result;
    }
}
