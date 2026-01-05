package com.ygames.ysoccer.framework.commentary;

import com.badlogic.gdx.audio.Sound;
import lombok.AllArgsConstructor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * A comment element
 */
@AllArgsConstructor
public class Sentence {

    public final Sound sound;
    public final SentenceRequirement requiresStart;
    public final SentenceRequirement requiresEnd;

    public static Sentence of(Sound sound, String name) {
        return new Sentence(sound, start(name), end(name));
    }

    public static Sentence of(Sound sound) {
        return new Sentence(sound, SentenceRequirement.NONE, SentenceRequirement.NONE);
    }

    private static SentenceRequirement resolve(String name, BiPredicate<String, String> matcher) {
        return Stream.of(
                new AbstractMap.SimpleEntry<>(SentenceRequirement.TEAM_CH, SentenceRequirement.TEAM),
                new AbstractMap.SimpleEntry<>(SentenceRequirement.PLAYER_CH, SentenceRequirement.PLAYER),
                new AbstractMap.SimpleEntry<>(SentenceRequirement.CITY_CH, SentenceRequirement.CITY),
                new AbstractMap.SimpleEntry<>(SentenceRequirement.STADIUM_CH, SentenceRequirement.STADIUM)
            )
            .filter(e -> matcher.test(name, e.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(SentenceRequirement.NONE);
    }

    public static SentenceRequirement start(String name) {
        return resolve(name, String::startsWith);
    }

    public static SentenceRequirement end(String name) {
        return resolve(name, String::endsWith);
    }

}
